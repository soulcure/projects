package charge

import (
	"errors"
	"log"
	"db"
	"time"
	"util"
	"fmt"
	"reflect"
	"unsafe"
)
func SetTime(tm []byte){
	now:=time.Now()
	bcd:=util.BCDTime{
		uint16(now.Year()),
		uint8(now.Month()),
		uint8(now.Day()),
		uint8(now.Hour()),
		uint8(now.Minute()),
		uint8(now.Second()),
	}
	data:=util.SetTime(&bcd)
	copy(tm,data)
}
func TchargeRecordReq()error {
	var tt ChargeStartReq
	log.Println("len:",Sizeof(reflect.ValueOf(tt)))
	var req ChargeRecordReq
	var info StateInfoReq
	var sign SignInReq
	log.Println(Sizeof(reflect.ValueOf(req)))
	log.Println(Sizeof(reflect.ValueOf(info)))
	log.Println(Sizeof(reflect.ValueOf(sign)))
	printStruct(sign)
	strtobyte(req.Code[:], "N010116980100003")
	req.PointNum = 2
//	strtobyte(req.Seq[:], "12345678900")
	strtobyte(req.Account[:], "o9o2u4lNQnp7hEFv9i6J2dAUjj2g")
	req.Money = 100
	req.ChargeStrategy = 0
	req.StragtegyParameter = 0
	req.Type = 0
	SetTime(req.ChargeBeginTime[:])
	time.Sleep(time.Second * 3)
	SetTime(req.ChargetEndTime[:])
	req.ChargeTime = 3
	//check the record whether is exist
	account := bytetostr(req.Account[:])
	start := util.BcdTimeToUnix(req.ChargeBeginTime)
	end := util.BcdTimeToUnix(req.ChargetEndTime)
	key := fmt.Sprintf("%s_%v_%v", account, start, end)
	log.Printf("bill:%s start record\n", key)
	/*	n,err:=db.RedisClient[db.User_charge_index].Exists(key).Result()
		if err!=nil{
			log.Println("redis:",err)
			//todo warning
			return err
		}*/

	//record to db
	instance := db.DbPool.GetDb()
	if instance == nil {
		log.Println("get dbinstance fail")
		return errors.New("get dbinstance fail")
	}
	stmtOut, err := instance.Db.Prepare("SELECT seq FROM user_charge_record WHERE seq = ?")
	if err != nil {
		return err
	}
	defer stmtOut.Close()
	var test string
	err = stmtOut.QueryRow("o9o2u4lNQnp7hEFv9i6J2dAUjj2g_1547282108_15472821").Scan(&test) // WHERE number = 13
	if err != nil {
		log.Printf("charge record(%s) already store in mysql", key)
		return nil
	}else{
		log.Println("not exist")
	}

	tx,err:=instance.Db.Begin()
	if err!=nil{
		return errors.New("transaction begin fail")
	}
	stmtIns, err := tx.Prepare("INSERT user_charge_record SET seq=?,code=?,num=?,type=?,account_id=?,begin=?,end=?,time=?,reason=?,electric=?,money=?,strategy=?,parameter=?,carid=?,way=?,electricInfo=?,order_num=?") // ? = placeholder
	if err != nil {
		tx.Rollback()
		return errors.New("Prepare insert into user_charge_record fail:"+err.Error()) // proper error handling instead of panic in your app
	}
	defer stmtIns.Close() // Close the statement when we leave main() / the program terminates

	_, err = stmtIns.Exec(key, req.Code[:] ,req.PointNum,req.Type,account,start,end,req.ChargeTime,req.Reason,req.ElectricQuantity,req.Money,req.ChargeStrategy,req.StragtegyParameter,req.CarId[:],req.StartWay,req.TimeElectricQuantity[:],fmt.Sprintf("ch%d",time.Now().Unix()))
	if err != nil {
		tx.Rollback()
		return errors.New("insert into users_charge_record fail："+err.Error()) // proper error handling instead of panic in your app
	}

	if _,err:=tx.Exec("insert into c_consumption (account_id,money,time,seq,remarks) values(?,?,?,?,?)",account,req.Money,time.Now().Unix(),key,0);err!=nil{
		if err:=tx.Rollback();err!=nil{
			log.Println(err)
		}
		return errors.New("insert into c_consumption  fail："+err.Error()) // proper error handling instead of panic in your app
	}
	if _,err:=tx.Exec("update  c_account set money=money-? where account_id=?",req.Money,account);err!=nil{
		tx.Rollback()
		return errors.New("update c_account  fail："+err.Error()) // proper error handling instead of panic in your app
	}
	//tx.Rollback()
	tx.Commit()
	//record to redis
	/*	if err:=db.RedisClient[db.User_charge_record].RPush("user_charge_record",string(body)).Err();err!=nil{
			return errors.New("user_charge_record rpush redis fail")
		}
		if err:=db.RedisClient[db.User_charge_index].Set(key,"",0).Err();err!=nil{
			return errors.New("user_charge_index set redis fail"+key)
		}*/

	log.Printf("bill:%s record succes\n",key)


	return nil
}

func Sizeof(v reflect.Value) int{
	size:=0
	switch v.Kind(){
	case reflect.Struct:
		for i:=0;i<v.NumField();i++ {
			size+=Sizeof(v.Field(i))
		}
		return size
	case reflect.Uint8:
		return int(unsafe.Sizeof(uint8(0)))
	case reflect.Uint16:
		return int(unsafe.Sizeof(uint16(0)))
	case reflect.Uint32:
		return int(unsafe.Sizeof(uint32(0)))
	case reflect.Uint64:
		return int(unsafe.Sizeof(uint64(0)))
	case reflect.Array:
		return v.Len()
	default:
		panic(fmt.Sprintf("invalid type:%v",v.Type()))
	}
	return 0
}

