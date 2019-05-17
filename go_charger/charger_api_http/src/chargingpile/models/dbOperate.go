package models

import (
	"chargingpile/data"
	"database/sql"
	"encoding/json"
	"fmt"
	"github.com/astaxie/beego/logs"
	_ "github.com/go-sql-driver/mysql"
	"log"
	"time"
)

func QuerySimple(userId string) (*int, error) {
	db := data.GetDb()
	rows, err := db.Query("select status from c_user where user_id = ?", userId)
	if err != nil {
		logs.Error("Db QuerySimple user status error :%v", err)
		return nil, err
	}
	defer rows.Close()
	if rows.Next() {
		var status int
		err := rows.Scan(&status)
		if err != nil {
			return nil, err
		}
		return &status, nil
	}
	return nil, nil
}

func Query(userId string) (*User, error) {
	db := data.GetDb()
	rows, err := db.Query("select c.user_id,c.country_code,c.phone,c.nick_name,c.gender,c.city,c.create_time,c.status, ca.money from c_user c,c_account ca WHERE c.user_id = ? and ca.account_id = ?", userId, userId)
	if err != nil {
		logs.Error("Db Query userInfo error :%v", err)
		return nil, err
	}
	defer rows.Close()
	if rows.Next() {
		var user User
		country_code := sql.NullString{String: "", Valid: false}
		phone := sql.NullString{String: "", Valid: false}
		nick_name := sql.NullString{String: "", Valid: false}
		city := sql.NullString{String: "", Valid: false}
		err := rows.Scan(&user.UserId, &country_code, &phone, &nick_name, &user.Gender, &city, &user.CreateTime, &user.Status, &user.Money) // birth字段在数据库中是空字段
		if err != nil {
			return nil, err
		}
		user.CountryCode = country_code.String
		user.Phone = phone.String
		user.NickName = nick_name.String
		user.City = city.String
		return &user, nil
	}
	return nil, nil
}

func Insert(info map[string]interface{}) (err error) {
	db := data.GetDb()
	conn, err := db.Begin()
	if err != nil {
		logs.Error("Db Transaction Begin Insert error :%v", err)
		return
	}
	defer func() {
		if err != nil {
			conn.Rollback()
			logs.Error("Db Transaction Insert error :%v ,Rollback data", err)
			return
		}
		logs.Info("mysql Transaction Insert c_user&c_account success ")
		conn.Commit()
	}()
	_, err = conn.Exec("insert into c_user(user_id,nick_name,gender,city,create_time,status)values(?,?,?,?,?,?)",
		info["openId"], info["nickName"], info["gender"], info["city"], info["createTime"], info["status"])
	if err != nil {
		logs.Error("Db TransactionInsert Insert c_user error :%v", err)
		return
	}
	_, err = conn.Exec("insert into c_account(account_id,money,create_time)values(?,?,?)", info["openId"], 0, info["createTime"])
	if err != nil {
		logs.Error("Db Transaction Insert c_account error :%v", err)
		return
	}
	return
}

//更新用户注册状态
func Update(userId, phone, countryCode string) (err error) {
	db := data.GetDb()
	updateRes, err := db.Exec("update c_user set country_code =?,phone = ?,status = ? where user_id =?", countryCode, phone, 1, userId)
	if err != nil {
		logs.Error("Db Update  error :%v", err)
		return
	}
	logs.Info("update userId phone success ! %v", updateRes)
	return
}

//查询充电桩位置
func QueryStation(longitude, latitude float64) ([]*Station, error) {
	stations := make([]*Station, 0)
	db := data.GetDb()
	err := db.Select(&stations, "select code,name,address,longitude,latitude,pileCount,status from c_station where latitude > ?-0.5 and latitude < ?+0.5 and longitude > ?-0.5 and longitude < ?+0.5 and status=1 order by ACOS(SIN((? * 3.1415) / 180 ) *SIN((latitude * 3.1415) / 180 ) +COS((? * 3.1415) / 180 ) * COS((latitude * 3.1415) / 180 ) *COS((?* 3.1415) / 180 - (longitude * 3.1415) / 180 ) ) * 6380 asc limit 10",
		latitude, latitude, longitude, longitude, latitude, latitude, longitude)
	if err != nil {
		logs.Error("QueryStation err :%v", err)
		return nil, err
	}
	if err == sql.ErrNoRows {
		log.Printf("not found data")
		return nil, err
	}
	return stations, nil
}

//查询账户记录
func QueryRecord(accountId string, pageNo, pageSize int) []*AccountRecord {
	accountRecord := make([]*AccountRecord, 0)
	db := data.GetDb()
	err := db.Select(&accountRecord, "select account_id,money,time,order_num,remarks from c_consumption where account_id = ? order by time desc limit ?,?", accountId, pageNo, pageSize)
	if err != nil {
		logs.Error("Db QueryRecord  error :%v", err)
		return nil
	}
	if err == sql.ErrNoRows {
		log.Printf("Db QueryRecord not found data")
		return nil
	}
	return accountRecord
}

func QueryRecordByRemark(accountId string, remark, pageNo, pageSize int) []*AccountRecord {
	accountRecord := make([]*AccountRecord, 0)
	db := data.GetDb()
	err := db.Select(&accountRecord, "select account_id,money,time,order_num,remarks from c_consumption where account_id = ? and remarks = ? order by time desc limit ?,?", accountId, remark, pageNo, pageSize)
	if err != nil {
		logs.Error("Db QueryRecordByRemark  error :%v", err)
		return nil
	}
	if err == sql.ErrNoRows {
		log.Printf("Db QueryRecordByRemark not found data")
		return nil
	}
	return accountRecord
}

func QueryChargeRecordDetails(seq, userId string) *ChargeRecordDetails {
	db := data.GetDb() //account_id
	rows, err := db.Query("select order_num,code,num,account_id,begin,end,time,reason,electric,money from user_charge_record where seq = ? and account_id = ? ", seq, userId)
	if err != nil {
		logs.Error("Db QueryRecord  error :%v", err)
		return nil
	}
	defer rows.Close()
	if rows.Next() {
		var chargeRecordDetails ChargeRecordDetails
		err := rows.Scan(&chargeRecordDetails.Seq, &chargeRecordDetails.Code, &chargeRecordDetails.Num, &chargeRecordDetails.AccountId, &chargeRecordDetails.Begin, &chargeRecordDetails.End,
			&chargeRecordDetails.Time, &chargeRecordDetails.Reason, &chargeRecordDetails.Electric, &chargeRecordDetails.Money) // birth字段在数据库中是空字段
		if err != nil {
			return nil
		}
		logs.Info("查询用户账单详情:%+v", chargeRecordDetails)
		return &chargeRecordDetails
	}
	return nil
}

func QueryAdviceData(stationCode, seq, userId string) *AdviceData {
	db := data.GetDb()
	rows, err := db.Query("select cs.name,ucr.order_num,ucr.code,ucr.num,ucr.account_id,ucr.begin,ucr.end,ucr.time,ucr.electric,ucr.money,ac.money from c_station cs,user_charge_record ucr ,c_account ac where cs.code=? and ucr.seq = ? and ucr.account_id =? and ac.account_id = ucr.account_id ", stationCode, seq, userId)
	if err != nil {
		logs.Error("Db QueryAdviceData  error :%v", err)
		return nil
	}
	defer rows.Close()
	if rows.Next() {
		adviceData := AdviceData{}
		err := rows.Scan(&adviceData.ChargingName, &adviceData.OrderNum, &adviceData.Code, &adviceData.Num, &adviceData.AccountId, &adviceData.Begin, &adviceData.End, &adviceData.Time, &adviceData.Electric, &adviceData.Money, &adviceData.AccountMoney) // birth字段在数据库中是空字段
		if err != nil {
			logs.Error("QueryAdviceData rows.Scan is err : %v", err)
			return nil
		}
		logs.Info("QueryAdviceData success :%+v ", adviceData)
		return &adviceData
	}
	return nil
}

func QueryStationName(stationId string) string {
	db := data.GetDb() //account_id
	rows, err := db.Query("select name from c_station where code = ? ", stationId)
	if err != nil {
		logs.Error("Db QueryRecord  error :%v", err)
		return ""
	}
	defer rows.Close()
	if rows.Next() {
		var name string
		err := rows.Scan(&name) // birth字段在数据库中是空字段
		if err != nil {
			return ""
		}
		logs.Info("查询充电站名称:%+v", name)
		return name
	}
	return ""
}

func QueryActivityByStationId(stationId, pageNo, pageSize int) []*Activity {
	activities := make([]*Activity, 0)
	db := data.GetDb() //account_id
	err := db.Select(&activities, "select id,type,title,rule,detail,startTime,endTime,createTime,updateTime,stationId,status from c_activity where stationId =? order by id desc limit ?,?", stationId, pageNo, pageSize)
	if err != nil {
		logs.Error("Db QueryActivityByStationId  error :%v", err)
		return nil
	}
	if err == sql.ErrNoRows {
		log.Printf("Db QueryActivityByStationId not found data")
		return nil
	}
	return activities
}
func QueryActivityByConditions(status, stationId, pageNo, pageSize int) []*Activity {
	activities := make([]*Activity, 0)
	db := data.GetDb() //account_id
	err := db.Select(&activities, "select id,type,title,rule,detail,startTime,endTime,createTime,updateTime,stationId,status from c_activity where status = ? and stationId =? order by id desc limit ?,?", status, stationId, pageNo, pageSize)
	if err != nil {
		logs.Error("Db QueryActivityByStationId  error :%v", err)
		return nil
	}
	if err == sql.ErrNoRows {
		log.Printf("Db QueryActivityByStationId not found data")
		return nil
	}
	return activities
}
func QueryActivityByStatus(status, pageNo, pageSize int) []*Activity {
	activities := make([]*Activity, 0)
	db := data.GetDb() //account_id
	err := db.Select(&activities, "select id,type,title,rule,detail,startTime,endTime,createTime,updateTime,stationId,status from c_activity where status = ?  order by id desc limit ?,?", status, pageNo, pageSize)
	if err != nil {
		logs.Error("Db QueryActivityByStationId  error :%v", err)
		return nil
	}
	if err == sql.ErrNoRows {
		log.Printf("Db QueryActivityByStationId not found data")
		return nil
	}
	return activities
}
func QueryAllActivity(pageNo, pageSize int) []*Activity {
	activities := make([]*Activity, 0)
	db := data.GetDb() //account_id
	err := db.Select(&activities, "select id,type,title,rule,detail,startTime,endTime,createTime,updateTime,stationId,status from c_activity order by id desc limit ?,?", pageNo, pageSize)
	if err != nil {
		logs.Error("Db QueryActivityByStationId  error :%v", err)
		return nil
	}
	if err == sql.ErrNoRows {
		log.Printf("Db QueryActivityByStationId not found data")
		return nil
	}
	return activities
}

func QueryActivity() ([]Activity, error) {
	var acts []Activity
	curTime := time.Now().Unix()

	db := data.GetDb() //account_id
	err := db.Select(&acts, "select id,status,type,title,image,detail,rule,startTime,endTime,createTime,updateTime,discount,stationId,stationName from c_activity  where status = ? and startTime < ?  and endTime > ? order by createTime desc", 1, curTime, curTime)

	if err != nil {
		logs.Error("Db Query Activity  error :%v", err)
		return nil, err
	} else {
		logs.Debug("Db Query Activity  acts :%v", acts)
		activityToRedis(acts)
	}
	return acts, err
}

func QueryActivityById(id int) (*Activity, error) {

	var act Activity
	curTime := time.Now().Unix()

	db := data.GetDb() //account_id
	err := db.QueryRow("select * from c_activity where status = ? and id = ? and startTime < ?  and endTime > ?", 1, id, curTime, curTime).Scan(&act.Id, &act.Status, &act.Type, &act.Title, &act.Image, &act.Detail, &act.Rule, &act.StartTime, &act.EndTime, &act.CreateTime, &act.UpdateTime, &act.Discount, &act.StationId, &act.StationName)

	logs.Debug("Db Query Activity  acts :%v", act)

	if err != nil {
		logs.Error("Db Query Activity  error :%v", err)
		return nil, err
	}

	return &act, err
}

func activityToRedis(acts []Activity) {
	isGift := false
	for _, i := range acts {
		if i.Type == 1 {
			isGift = true
			if rule, err := data.GetString("Rule"); err == nil {
				if len(rule) > 0 && rule == i.Rule {
					logs.Debug("Redis has a Rule !")
				} else {
					if _, err := data.SetString("Rule", i.Rule); err != nil {
						logs.Error("Set Rule to redis error !", err)
					}
					if _, err := data.ExpireKey("Rule", 60*60*12); err != nil {
						logs.Error("Set Rule to redis error !", err)
					}
				}

			} else {
				if _, err := data.SetString("Rule", i.Rule); err != nil {
					logs.Error("Set Rule to redis error !", err)
				}
				if _, err := data.ExpireKey("Rule", 60*60*12); err != nil {
					logs.Error("Set Rule to redis error !", err)
				}
				logs.Debug("Set Rule to redis success !")
			}

		} else if i.Type == 2 {
			key := fmt.Sprintf("Discount_%d", i.StationId)
			if discount, err := data.GetInt(key); err == nil {
				if discount == i.Discount {
					logs.Debug("Redis has a Discount !")
				} else {
					if _, err := data.SetInt(key, i.Discount); err != nil {
						logs.Error("Set Discount to redis error !", err)
					}
					if _, err := data.ExpireKey(key, 60*60*12); err != nil {
						logs.Error("Set Discount to redis error !", err)
					}
					logs.Debug("Set Discount to redis success !")
				}
			} else {
				if _, err := data.SetInt(key, i.Discount); err != nil {
					logs.Error("Set Discount to redis error !", err)
				}
				if _, err := data.ExpireKey(key, 60*60*12); err != nil {
					logs.Error("Set Rule to redis error !", err)
				}
				logs.Debug("Set Discount to redis success !")
			}
		}
	}

	if !isGift {
		if _, err := data.DelKey("Rule"); err != nil {
			logs.Error("Del Rule to redis error !", err)
		}
	}
}

func GetDiscountValue(stationId int) float64 {
	key := fmt.Sprintf("Discount_%d", stationId)
	if discount, err := data.GetInt(key); err == nil {
		return float64(discount) / float64(100)
	} else {
		return 1
	}

}

func GetGiftValue(money int) int {
	gift := 0
	if rule, err := data.GetString("Rule"); err == nil {
		if len(rule) > 0 {
			bytes := []byte(rule)
			var giftList GiftList
			if err := json.Unmarshal(bytes, &giftList); err == nil {
				length := len(giftList.Rules)

				for i := length - 1; i >= 0; i-- {
					value := giftList.Rules[i].Amount
					if money >= value {
						gift = giftList.Rules[i].Gift
						break
					}
				}
			}
		}
	} else {
		logs.Error("Get Rule to redis empty !", err)
	}
	return gift
}
