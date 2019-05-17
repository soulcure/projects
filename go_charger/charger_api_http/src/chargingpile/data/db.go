package data

import (
	"fmt"
	"github.com/astaxie/beego"
	"github.com/astaxie/beego/logs"
	"github.com/garyburd/redigo/redis"
	_ "github.com/go-sql-driver/mysql"
	"github.com/jmoiron/sqlx"
	"time"
)

var db *sqlx.DB //线程安全的
var pool *redis.Pool

func init() {
	initDb()
	initRedis()
}

//初始化数据库
func initDb() {
	dataSourceName := beego.AppConfig.String("mysqluser") + ":" + beego.AppConfig.String("mysqlpass") +
		"@tcp(" + beego.AppConfig.String("mysqlurls") + ")" + beego.AppConfig.String("mysqldb")
	if databases, err := sqlx.Open("mysql", dataSourceName); err != nil {
		panic(err)
	} else {
		logs.Info("mysql db init run !")
		db = databases
		db.SetMaxOpenConns(30)
		db.SetMaxIdleConns(10)
		if e := db.Ping(); e != nil {
			panic(err)
		}
	}

}

//初始化redis
func initRedis() {
	redisHost := beego.AppConfig.String("redis::redis_host")
	redisPost := beego.AppConfig.String("redis::redis_port")
	passWord := beego.AppConfig.String("redis::redis_password")

	maxIdle, err := beego.AppConfig.Int("redis::redis_pool_maxidle")
	if err != nil {
		panic(fmt.Errorf("init read redis config redis_pool_maxIdle err :%v", err))
		return
	}

	maxActive, err := beego.AppConfig.Int("redis::redis_pool_maxactive")
	if err != nil {
		panic(fmt.Errorf("init read redis config redis_pool_maxactive err :%v", err))
		return
	}

	timeOut, err := beego.AppConfig.Int("redis::redis_timeout")
	if err != nil {
		panic(fmt.Errorf("init read redis config redis_timeout err :%v", err))
		return
	}

	pool = &redis.Pool{
		MaxIdle:     maxIdle,   // 最大的空闲连接数
		MaxActive:   maxActive, // 最大的激活连接数，表示同时最多有N个连接 ，为0表示没有限制
		IdleTimeout: time.Duration(timeOut) * time.Second,
		Dial: func() (redis.Conn, error) {
			c, err := redis.Dial("tcp", redisHost+":"+redisPost)
			if err != nil {
				return nil, fmt.Errorf("redis connection error: %s", err)
			}
			//验证redis密码
			if _, authErr := c.Do("AUTH", passWord); authErr != nil {
				return nil, fmt.Errorf("redis auth password error: %s", authErr)
			}

			return c, err
		},
	}
	conn := pool.Get()
	reply, e := conn.Do("ping")
	if e != nil {
		logs.Error("ping redis failed ,err:%v", e)
	}
	logs.Info("redis server run reply :%v", reply)
	defer func() {
		if err := conn.Close(); err != nil {
			panic("redis close error")
		}
	}()
}

func GetDb() *sqlx.DB {
	return db
}

func GetConn() redis.Conn {
	return pool.Get()
}

func DelKey(key string) (interface{}, error) {
	c := pool.Get()

	defer func() {
		if err := c.Close(); err != nil {
			logs.Error(err)
		}
	}()
	return c.Do("DEL", key)
}

func ExpireKey(key string, seconds int64) (interface{}, error) {
	c := pool.Get()
	defer func() {
		if err := c.Close(); err != nil {
			logs.Error(err)
		}
	}()
	return c.Do("EXPIRE", key, seconds)
}

func SetInt(key string, value int) (interface{}, error) {
	c := pool.Get()
	defer func() {
		if err := c.Close(); err != nil {
			logs.Error(err)
		}
	}()

	return c.Do("Set", key, value)
}

func GetInt(key string) (int, error) {
	c := pool.Get()
	defer func() {
		if err := c.Close(); err != nil {
			logs.Error(err)
		}
	}()

	return redis.Int(c.Do("Get", key))
}

func SetString(key, value string) (interface{}, error) {
	c := pool.Get()
	defer func() {
		if err := c.Close(); err != nil {
			logs.Error(err)
		}
	}()

	return c.Do("Set", key, value)
}

func GetString(key string) (string, error) {
	c := pool.Get()
	defer func() {
		if err := c.Close(); err != nil {
			logs.Error(err)
		}
	}()

	return redis.String(c.Do("Get", key))
}

// fieldValue 必须设置 tag ,如： Title  string `redis:"title"`
func SetStruct(key string, fieldValue interface{}) (interface{}, error) {
	c := pool.Get()
	defer func() {
		if err := c.Close(); err != nil {
			logs.Error(err)
		}
	}()
	return c.Do("HMSET", redis.Args{}.Add(key).AddFlat(fieldValue)...)
}

func GetStruct(key string, fieldValue interface{}) error {
	c := pool.Get()
	defer func() {
		if err := c.Close(); err != nil {
			logs.Error(err)
		}
	}()
	r, err := redis.Values(c.Do("HGETALL", key))
	if err != nil {
		logs.Error(err)
		return err
	}

	if e := redis.ScanStruct(r, fieldValue); e != nil {
		logs.Error(err)
		return e
	}

	return err
}
