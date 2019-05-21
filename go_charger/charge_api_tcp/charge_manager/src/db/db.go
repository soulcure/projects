package db

import (
	"database/sql"
	_ "github.com/go-sql-driver/mysql"
	"log"
	"os"
	"sync"
	"time"
	"util"
)

const connsnum int = 32

type DbInstance struct {
	Db   *sql.DB
	busy bool
}
type DatabasePool struct {
	instance []*DbInstance
	num      int
	user     string
	password string
	addr     string
	name     string

	sync.Mutex
}

var DbPool *DatabasePool

func init() {
	addr := util.CFG.Section("db").Key("addr").String()
	user := util.CFG.Section("db").Key("user").String()
	passwd := util.CFG.Section("db").Key("password").String()
	name := util.CFG.Section("db").Key("dbname").String()
	num, err := util.CFG.Section("db").Key("conns").Int()
	if err != nil {
		num = connsnum
	}
	DbPool = &DatabasePool{
		user:     user,
		password: passwd,
		addr:     addr,
		name:     name,
		num:      num,
	}
	DbPool.instance = make([]*DbInstance, num)
	for i := 0; i < num; i++ {
		DbPool.instance[i] = &DbInstance{
			nil,
			false,
		}
	}
	if err = DbPool.Conn(); err != nil {
		log.Println(err)
		os.Exit(0)
	}
	log.Println("connect db success")
}

func (pool *DatabasePool) Conn() error {
	dataSourceName := pool.user + ":" + pool.password + "@tcp(" + pool.addr + ")/" + pool.name
	for i := 0; i < pool.num; i++ {
		d, err := sql.Open("mysql", dataSourceName)
		if err != nil {
			return err // Just for example purpose. You should use proper error handling instead of panic
		}
		pool.instance[i].Db = d
	}
	return nil
}

func (db *DatabasePool) getDb() *DbInstance {
	db.Lock()
	defer db.Unlock()
	for _, instance := range db.instance {
		if !instance.busy {
			instance.busy = true
			return instance
		}
	}
	return nil
}

func (pool *DatabasePool) GetDb() *DbInstance {
	instance := pool.getDb()
	if instance == nil {
		time.Sleep(50 * time.Millisecond)
		instance = pool.getDb()
		if instance == nil {
			log.Println("db is busy,get db instance fail")
			return nil
		}
	}
	return instance
}

func (instance *DbInstance) Release() {
	instance.busy = false
}
