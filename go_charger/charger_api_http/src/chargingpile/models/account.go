package models

import (
	"chargingpile/data"
	"errors"
	"github.com/astaxie/beego/logs"
	"github.com/garyburd/redigo/redis"
	"strconv"
)

type Account struct {
	Id         int    `db:"id" json:"id"`
	AccountId  string `db:"account_id" json:"accountId"`
	Money      int    `db:"money" json:"money"`
	CreateTime int64  `db:"create_time" json:"createTime"`
}

//插入或更新用户金额 充值money传正值，扣费money传负值
func UpdateAccount(accountId string, money int) error {
	acc, err := GetAccount(accountId)
	if err != nil { //数据库不存在用户金额，创建数据
		if e := acc.insertAccount(money); e != nil {
			return e
		}
	} else { //数据库存在用户金额
		if e := acc.updateMoney(money); e != nil {
			return e
		}
	}
	return err
}

//判断用户账号是否存在于数据库
func GetAccount(accountId string) (*Account, error) {
	if acc, err := readFromDb(accountId); err == nil {
		return acc, err
	}
	return &Account{}, errors.New("未查询要用户账号信息")
}

//从redis读取账号数据
func readFromRedis(accountId string) (*Account, error) {
	var acc Account
	conn := data.GetConn()
	defer func() {
		if err := conn.Close(); err != nil {
			panic("redis close error")
		}
	}()
	row, err := redis.Strings(conn.Do("hgetall", accountId))
	if err != nil {
		logs.Debug("redis  rows: %+v", row)
		acc.AccountId = accountId

		if money, err := strconv.Atoi(row[1]); err != nil {
			logs.Error("account read rom redis error:", err)
			return &acc, err
		} else {
			acc.Money = money
		}
	}

	return &acc, err

}

//从db读取账号数据
func readFromDb(accountId string) (*Account, error) {
	//查询数据库
	var acc Account

	db := data.GetDb()
	err := db.QueryRow("select * from c_account where account_id = ?", accountId).Scan(&acc.Id, &acc.AccountId, &acc.Money, &acc.CreateTime)
	if err != nil {
		logs.Error("account read rom db error:", err)
		return &acc, err
	}
	return &acc, err
}

//从db读取所有账号的数据
func readAllFromDb() ([]Account, error) {
	//查询数据库
	var accounts []Account

	db := data.GetDb()
	err := db.Select(&accounts, "select id,account_id,money,create_time from c_account")
	if err != nil {
		logs.Error("account read rom db error:", err)
		return accounts, err
	}

	return accounts, err
}

//写入账号数据到redis
func writeToRedis(acc *Account) {
	conn := data.GetConn()
	defer func() {
		if err := conn.Close(); err != nil {
			panic("redis close error")
		}
	}()
	//将 Account 以hash形式写入redis
	_, e1 := conn.Do("hmset", acc.AccountId, "money", acc.Money)
	//设置过期时间
	_, e2 := conn.Do("expire", acc.Id, 60*60*24*30)

	if e1 != nil || e2 != nil {
		logs.Error(acc.AccountId, "写入失败", e1, e2)
	} else {
		logs.Error(acc.AccountId, "写入成功")
	}

}

//更新用户金额
func (account *Account) updateMoney(money int) error {
	account.Money += money
	db := data.GetDb()
	r, err := db.Exec("update c_account set money =money+?", account.Money)
	if err != nil {
		logs.Error("account update money error:", err)
		return err
	}
	logs.Info("account update money success :%v", r)

	writeToRedis(account)
	return err
}

func (account *Account) insertAccount(money int) error {
	account.Money = money
	db := data.GetDb()
	r, err := db.Exec("insert into c_account(accountId,money)values(?,?)", account.AccountId, account.Money)
	if err != nil {
		logs.Error("account Insert  err:", err)
		return err
	}

	logs.Debug("account Insert  success:", r)
	writeToRedis(account)
	return err
}
