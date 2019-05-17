package models

import (
	"chargingpile/data"
	"database/sql"
	"github.com/astaxie/beego/logs"
	"time"
)

type Order struct {
	UserId    string `db:"user_id" json:"userId"`
	OrderId   string `db:"order_id" json:"orderId"`
	Money     int    `db:"money" json:"money"`
	OrderTime string `db:"order_time" json:"order_time"`
	Status    uint8  `db:"status" json:"status"`
	Offer     int    `db:"discount" json:"offer"` //充值赠送金额
}

func (order *Order) InsertOrder() error {
	logs.Debug("插入订单信息", order.OrderId)
	db := data.GetDb()
	r, err := db.Exec("insert into c_order(user_id,order_id,money,order_time,status)values(?,?,?,?,?)",
		order.UserId, order.OrderId, order.Money, order.OrderTime, order.Status)
	if err != nil {
		logs.Error("mysql Insert order err :%v", err)
		return err
	}
	logs.Info("mysql Insert order success :%v", r)
	return err
}

func (order *Order) UpdateOrder() error {
	logs.Debug("更新订单信息", order.OrderId)
	db := data.GetDb()
	r, err := db.Exec("update c_order set Status = ? where order_id = ? and Status =0", order.Status, order.OrderId)
	if err != nil {
		logs.Error("mysql update order err :%v", err)
		return err
	}
	logs.Info("mysql update order success :%v", r)
	return err
}

//更新用户金额
func (order *Order) UpdateMoney() error {
	db := data.GetDb()
	r, err := db.Exec("update c_account set money = money+? where account_id = ?", order.Money, order.UserId)
	if err != nil {
		logs.Error("account update money error:", err)
		return err
	}
	logs.Info("account update money success :%v", r)
	return err
}

func (order *Order) InsertConsumption() error {
	logs.Debug("插入消费信息", order.OrderId)
	db := data.GetDb()
	r, err := db.Exec("insert into c_consumption(account_id,money,time,order_num,remarks)values(?,?,?,?,?)",
		order.UserId, order.Money, order.OrderTime, order.OrderId, order.Status)
	if err != nil {
		logs.Error("mysql Insert consumption err :%v", err)
		return err
	}
	logs.Info("mysql Insert consumption success :%v", r)
	return err
}

func (order *Order) UpdateOrderTX() error {
	logs.Debug("更新订单信息", order.OrderId)

	db := data.GetDb()
	tx, e := db.Begin()
	if e != nil {
		return e
	}

	local, err := time.LoadLocation("Local") //服务器设置的时区
	if err != nil {
		return err
	}

	times, err := time.ParseInLocation("20060102150405", order.OrderTime, local)
	if err != nil {
		return err
	}
	timestamp := times.Unix()

	r, err := tx.Exec("update c_order set Status = ? where order_id = ? and Status =0", order.Status, order.OrderId)
	if err != nil {
		txRollback(tx)
		return err
	} else {
		rowAffected, err := r.RowsAffected()
		if err != nil {
			txRollback(tx)
			logs.Error("update c_order error:", rowAffected)
			return err
		} else {
			logs.Debug("update c_order success:", rowAffected)
		}
	}

	r, err = tx.Exec("update c_account set money = money+? where account_id = ?", order.Money, order.UserId)
	if err != nil {
		txRollback(tx)
		return err
	} else {
		rowAffected, err := r.RowsAffected()
		if err != nil {
			txRollback(tx)
			logs.Error("update c_account error:", rowAffected)
			return err
		} else {
			logs.Debug("update c_account success:", rowAffected)
		}
	}

	r, err = tx.Exec("insert into c_consumption(account_id,money,time,order_num,remarks,discount)values(?,?,?,?,?,?)",
		order.UserId, order.Money, timestamp, order.OrderId, order.Status, order.Offer)
	if err != nil {
		txRollback(tx)
		return err
	} else {
		rowAffected, err := r.RowsAffected()
		if err != nil {
			txRollback(tx)
			logs.Error("insert  c_consumption error:", rowAffected)
			return err
		} else {
			logs.Debug("insert  c_consumption success:", rowAffected)
		}
	}

	err = tx.Commit()
	if err != nil {
		txRollback(tx)
		return err
	}
	return err
}

func txRollback(tx *sql.Tx) {
	e := tx.Rollback()
	if e != nil {
		logs.Error("tx.Rollback() Error:" + e.Error())
	}
}
