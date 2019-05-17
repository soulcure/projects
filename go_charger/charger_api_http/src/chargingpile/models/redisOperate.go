package models

import (
	"chargingpile/data"
	"encoding/json"
	"fmt"
	"github.com/astaxie/beego/logs"
	"github.com/garyburd/redigo/redis"
	"math/rand"
	"time"
)

func Get(key string, index int) []byte {
	conn := data.GetConn()
	defer func() {
		if err := conn.Close(); err != nil {
			panic("redis close error")
		}
	}()
	_, err := conn.Do("select", index)
	if err != nil {
		logs.Error("redis method getSession ->select index:%v ,key :%v, err:%v", index, key, err)
		return nil
	}
	bytes, err := redis.Bytes(conn.Do("get", key))
	if err != nil {
		logs.Error("redis method GetSession -> err :%v", err)
		return nil
	}
	return bytes
}

func HGet(key string, index int) map[string]string {
	conn := data.GetConn()
	defer func() {
		if err := conn.Close(); err != nil {
			panic("redis close error")
		}
	}()
	_, err := conn.Do("select", index)
	if err != nil {
		logs.Error("redis method HGet -> select index:%v ,key :%v, err:%v", index, key, err)
		return nil
	}
	values, err := redis.StringMap(conn.Do("hgetall", key))
	if err != nil {
		logs.Error("redis method HGet  -> err :%v", err)
		return nil
	}
	return values
}

func GetChange(key string, value string) (err error) {
	conn := data.GetConn()
	defer func() {
		if err := conn.Close(); err != nil {
			panic("redis close error")
		}
	}()
	_, err = conn.Do("select", 2)
	if err != nil {
		logs.Error("redis method getSession ->select index 2 err:%v", key, err)
		return
	}
	logs.Info("redis getSession 这个是redis是否正确获取到key->key :%v", key)
	bytes, err := redis.Bytes(conn.Do("get", key))
	if err != nil {
		logs.Error("redis method GetSession -> err :%v", err)
		return
	}
	cache := make(map[string]string)
	if len(bytes) != 0 {
		json.Unmarshal(bytes, &cache)
		cache["status"] = value
		b, _ := json.Marshal(cache)
		_, err = conn.Do("set", key, b, "ex", 60*60*24*30)
		if err != nil {
			logs.Error("redis method setSession ->set key err :%v", err)
			return
		}
	}
	return
}

func Exists(token string, isDel bool) bool {
	conn := data.GetConn()
	defer func() {
		if err := conn.Close(); err != nil {
			panic("redis close error")
		}
	}()
	_, err := conn.Do("select", 2)
	if err != nil {
		logs.Error("redis methos ->select index 2 err:%v", err)
		return false
	}
	b, _ := redis.Bool(conn.Do("exists", token))
	if b {
		if isDel {
			if _, e := conn.Do("del", token); e != nil {
				logs.Error(err)
			}
		}
		_, err := conn.Do("expire", token, 60*60*24*30)
		if err != nil {
			logs.Error("redis method ->expire index 2 err:%v", err)
		}
		return true
	}
	return false
}

func Set(key string, value []byte, index int, time int) {
	conn := data.GetConn()
	defer func() {
		if err := conn.Close(); err != nil {
			panic("SetFormId redis close error")
		}
	}()
	_, err := conn.Do("select", index)
	if err != nil {
		logs.Error("redis set method select index %v err:%v", index, err)
		return
	}
	_, err = conn.Do("set", key, value, "ex", time) //time60*60*24*7
	if err != nil {
		logs.Error("redis set key:%v,value:%v,index:%v,time:%v err :%v", key, string(value), index, time, err)
		return
	}
}

func LPush(key, value string, index int) {
	conn := data.GetConn()
	defer func() {
		if err := conn.Close(); err != nil {
			panic("RPush redis close error !")
		}
	}()
	_, err := conn.Do("select", index)
	if err != nil {
		logs.Error("redis RPush method select index %v err:%v", index, err)
		return
	}
	_, err = conn.Do("lpush", key, value)
	if err != nil {
		logs.Error("redis LPush value:%v ,error: ", value, err)
		return
	}
	logs.Info("redis LPush key:%v value:%v success", key, index)
}

func SetAndGet(token string, index int, time int) string {
	conn := data.GetConn()
	defer func() {
		if err := conn.Close(); err != nil {
			panic("redis SetAndGet close error")
		}
	}()
	_, err := conn.Do("select", index)
	if err != nil {
		logs.Error("redis SetAndGet method select index %v err:%v", index, err)
		return ""
	}
	_, err = conn.Do("set", "accessToken", token, "ex", time) //time60*60*24*7
	if err != nil {
		logs.Error("redis set key:%v,value:%v,index:%v,time:%v err :%v", "accessToken", token, index, time, err)
		return ""
	}
	formId, err := redis.String(conn.Do("rpop", "form_id"))
	if err != nil {
		logs.Error("redis rpop key:form_id error :%v", err)
		return ""
	}
	logs.Info("redis SetAndGet method set key:%v lpop:%v success", token, formId)
	return formId
}

//获取accessToken
func GetAccessToken(accessToken string) map[string]string {
	conn := data.GetConn()
	defer func() {
		if err := conn.Close(); err != nil {
			panic("redis close error")
		}
	}()
	_, err := conn.Do("select", 7)
	if err != nil {
		logs.Error("Redis GetAccessToken method select index :7 err:%v", err)
		return nil
	}
	token, err := redis.String(conn.Do("get", accessToken))
	if err != nil {
		logs.Error("Redis GetAccessToken method token:%v err :%v", accessToken, err)
		return nil
	}
	formId, err := redis.String(conn.Do("rpop", "form_id"))
	if err != nil {
		logs.Error("Redis GetAccessToken method rpop key:form_id err :%v", err)
		return nil
	}
	dataMap := make(map[string]string)
	dataMap["accessToken"] = token
	dataMap["formId"] = formId
	return dataMap
}

//获取订单号
func GetOrderNum() string {
	var orderNo string

	conn := data.GetConn()
	defer func() {
		if err := conn.Close(); err != nil {
			panic("redis close error")
		}
	}()

	if num, err := redis.Int64(conn.Do("INCR", "orderKey")); err != nil {
		logs.Error("redis orderKey get value error:", err)
		rand.Seed(time.Now().Unix())
		orderNo = "E" + time.Now().Format("20060102150405") + string(rand.Intn(100))
	} else {
		numStr := fmt.Sprintf("%04d", num)
		logs.Debug("order INCR:", numStr)
		orderNo = time.Now().Format("20060102150405") + numStr
	}

	return orderNo
}
