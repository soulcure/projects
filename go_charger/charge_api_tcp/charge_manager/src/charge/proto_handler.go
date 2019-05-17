package charge

import (
	"bytes"
	"db"
	"encoding/binary"
	"encoding/json"
	"errors"
	"fmt"
	"log"
	"logs"
	"math"
	"time"
	"util"
	"strconv"
)

type HttpResponse struct {
	Res  uint8       `json:"res"`
	Data interface{} `json:"data"`
}

func httpResponse(c *ChargeClient, data []byte) {
	if time.Now().Unix() < c.t {
		c.res <- data
	} else {
		logs.Warn("timeout recv charge response ")
	}
}

//cmd:1~10
func integerParameterRsp(c *ChargeClient, cmd CommandId, header []byte, body []byte) error {
	var rsp IntegerParameterRsp
	alterSize(body, rsp)
	read := bytes.NewReader(body)
	err := binary.Read(read, binary.LittleEndian, &rsp)
	if err != nil {
		return errors.New("parse IntegerParameterRsp fail:" + err.Error())
	}
	arr := make([]uint32, rsp.Num)
	if rsp.Type == 0 && read.Len() > 0 {
		err = binary.Read(read, binary.LittleEndian, arr)
		if err != nil {
			return errors.New("parse IntegerParameterRsp data fail:" + err.Error())
		}
	}
	logs.Info("data:", arr)
	if rsp.Res == 0 {
		logs.Info("charge(%s) integerParameter(%d) success", c.Code, rsp.Type)
	} else {
		logs.Info("charge(%s) integerParameter fail(%d)", c.Code, rsp.Type)
	}
	type result struct {
		Code string   `json:"code"`
		Type uint8    `json:"type"`
		Addr uint32   `json:"addr"`
		Num  uint8    `json:"num"`
		Data []uint32 `json:"data"`
	}
	var res result
	res.Code = bytetostr(rsp.Code[:])
	res.Type = rsp.Type
	res.Addr = rsp.Addr
	res.Num = rsp.Num
	res.Data = arr
	response := HttpResponse{rsp.Res, res}
	data, err := json.Marshal(response)
	if err != nil {
		return errors.New("json Marshal fail")
	}
	httpResponse(c, data)
	return nil
}

func charParameterRsp(c *ChargeClient, cmd CommandId, header []byte, body []byte) error {
	var rsp CharParameterRsp
	alterSize(body, rsp)
	read := bytes.NewReader(body)
	err := binary.Read(read, binary.LittleEndian, &rsp)
	if err != nil {
		return errors.New("parse CharParameterRsp fail:" + err.Error())
	}
	data := make([]byte, read.Len())
	if rsp.Type == 0 && read.Len() > 0 {
		err = binary.Read(read, binary.LittleEndian, data)
		if err != nil {
			return errors.New("parse charParameterRsp data fail:" + err.Error())
		}
	}
	logs.Info("data:", string(data))
	if rsp.Res == 0 {
		logs.Info("charge(%s) charParameter(%d) success", c.Code, rsp.Type)
	} else {
		logs.Info("charge(%s) charParameter fail(%d)", c.Code, rsp.Type)
	}
	type result struct {
		Code string `json:"code"`
		Type uint8  `json:"type"`
		Addr uint32 `json:"addr"`
		Data string `json:"data"`
	}
	var res result
	res.Code = bytetostr(rsp.Code[:])
	res.Type = rsp.Type
	res.Addr = rsp.Addr
	res.Data = string(data)
	response := HttpResponse{rsp.Res, res}
	data, err = json.Marshal(response)
	if err != nil {
		return errors.New("json Marshal fail")
	}
	httpResponse(c, data)

	return nil
}

func controlCmdRsp(c *ChargeClient, cmd CommandId, header []byte, body []byte) error {
	var rsp ControlCmdRsp
	alterSize(body, rsp)
	read := bytes.NewReader(body)
	err := binary.Read(read, binary.LittleEndian, &rsp)
	if err != nil {
		return errors.New("parse ControlCmdRsp fail:" + err.Error())
	}
	if rsp.Res == 0 {
		logs.Info("charge(%s) controlCmd success", c.Code)
	} else {
		logs.Info("charge(%s) controlCmd fail", c.Code)
	}
	type result struct {
		Code     string `json:"code"`
		Pointnum uint8  `json:"pointnum"`
		Addr     uint32 `json:"addr"`
		Num      uint8  `json:"num"`
		Res      uint8  `json:res"`
	}
	var res result
	res.Code = bytetostr(rsp.Code[:])
	res.Pointnum = rsp.PointNum
	res.Addr = rsp.Addr
	res.Num = rsp.Num
	res.Res = rsp.Res
	data, err := json.Marshal(res)
	if err != nil {
		return errors.New("json Marshal fail")
	}
	httpResponse(c, data)
	return nil
}

func chargeStartRsp(c *ChargeClient, cmd CommandId, header []byte, body []byte) error {
	var rsp ChargeStartRsp
	alterSize(body, rsp)
	read := bytes.NewReader(body)
	err := binary.Read(read, binary.LittleEndian, &rsp)
	if err != nil {
		return errors.New("parse signInReq fail:" + err.Error())
	}
	printStruct(rsp)
	if rsp.Res == 0 {
		logs.Info("charge(%s:%d) start success\n", c.Code, rsp.GunNum)
	} else {
		logs.Info("charge(%s:%d) start fail(%d)\n", c.Code, rsp.GunNum, rsp.Res)
		c.SetOccupy(rsp.GunNum, false)
	}
	type result struct {
		Code uint32 `json:"code"`
		Data string `json:msg"`
	}
	info := "success"
	if rsp.Res != 0 {
		info = "fail"
	}
	res := result{
		rsp.Res,
		info,
	}
	data, err := json.Marshal(res)
	if err != nil {
		return errors.New("json Marshal fail")
	}
	httpResponse(c, data)
	//TODO who to tell other server the result,although res is 0(succes) ,but the charge  couldn't starta charge success.wait cmd=104 result

	return nil
}

//cmd:101~110
func heatBeatReq(c *ChargeClient, cmd CommandId, header []byte, body []byte) error {
	if c.State < State_work {
		return nil
		c.heatBeatNum++
		if c.heatBeatNum > 6 {
			return nil
		}
	}
	time.Now().Unix()
	var req HeatBeatReq
	alterSize(body, req)
	read := bytes.NewReader(body)
	err := binary.Read(read, binary.LittleEndian, &req)
	if err != nil {
		return errors.New("parse HeatBeatReq fail:" + err.Error())
	}
	//printStruct(req)
	//auth and encrypt check
	rsp := &HeatBeatRsp{}
	c.Seq++
	if c.Seq == math.MaxUint16 {
		c.Seq = 0
	}
	rsp.Seq = c.Seq
	send(c, S_HEATBEAT_RSP, header, rsp)
	return nil
}

func stateInfoReq(c *ChargeClient, cmd CommandId, header []byte, body []byte) error {
	var req StateInfoReq
	alterSize(body, req)
	read := bytes.NewReader(body)

	err := binary.Read(read, binary.LittleEndian, &req)
	if err != nil {
		return errors.New("parse stateInfoReq fail:" + err.Error())
	}
	if req.PointNum == 0 {
		req.PointNum = 1
	}
	printStruct(req)
	logs.Info(util.FormatBcdTime(req.ChargeBeginTime))
	logs.Info("code state:", req.State)
	//save charge state to redis
	key := fmt.Sprintf("cs_%s", c.StationCode)
	field := fmt.Sprintf("%s_%d", c.Code, req.PointNum)
	state := struct {
		ChargeState uint8
		CarState    uint8
	}{req.State, req.CarConnState}
	data, err := json.Marshal(state)
	if err != nil {
		return errors.New("Marshal state fail:" + err.Error())
	}
	db.RedisClient[db.Charge_state_info].HSet(key, field, string(data))
	db.RedisClient[db.Charge_state_info].Expire(key,2*time.Minute)

	//user charge record
	//todo  delete user state
	//if req.State>=uint8(Work_state_prepare_charge) && req.State<=uint8(Work_state_ppointment){
	account := bytetostr(req.Account[:])
	user := Accounts.GetUser(account)
	if  WorkState(req.State) == Work_state_charging {
		if user != nil {
			if uint32(user.Money) < req.Expense {
				logs.Error("user(%s) balance(%d) is insufficient,will stop charge", account, user.Money)
				stopCharge(c, req.PointNum)
				time.Sleep(200 * time.Millisecond)
			}
		} else {
			logs.Error("not find user(%s) charging,but already is charging,will auto stop charging", account)
			//stopCharge(c,req.PointNum)
			//	return nil
		}
	}

	key = fmt.Sprintf("us_%s", bytetostr(req.Account[:]))
	vars := make(map[string]interface{})
	vars["state"] = req.State
	vars["carstate"] = req.CarConnState
	vars["money"] = req.Expense
	vars["electric"] = req.ElectricQuantity
	vars["time"] = req.ChargeTime
	vars["code"] = c.Code
	vars["pointnum"] = req.PointNum
	vars["soc"] = req.SOC
	vars["remainTime"] = req.RemainTime
	vars["record_time"] = time.Now().Unix()

	if WorkState(req.State) == Work_state_free {
		key := fmt.Sprintf("us_%s", bytetostr(req.Account[:]))
		db.RedisClient[db.User_charge_state].Del(key)
	} else {
		db.RedisClient[db.User_charge_state].HMSet(key, vars)
	}

	if WorkState(req.State) == Work_state_trouble {
		logs.Warn("charge(%s) pointNum(%d) trouble", c.Code, req.PointNum)
	}
	c.SetState(req.PointNum, WorkState(req.State))
	c.SetCarState(req.PointNum, req.CarConnState)
	rsp := &StateInfoRsp{}
	send(c, S_CHARGE_STATE_INFO_RSP, header, rsp)

	return nil
}

func timeSync(c *ChargeClient) {
	var req CharParameterReq
	req.Type = 1
	req.Addr = 2
	req.Size = 8
	var err error
	tmpbuf := new(bytes.Buffer)
	err = binary.Write(tmpbuf, binary.LittleEndian, &req)
	if err != nil {
		log.Println("binary.Write failed:", err)
		return
	}
	now := time.Now()
	bcd := util.BCDTime{
		uint16(now.Year()),
		uint8(now.Month()),
		uint8(now.Day()),
		uint8(now.Hour()),
		uint8(now.Minute()),
		uint8(now.Second()),
	}
	data := util.SetTime(&bcd)
	err = binary.Write(tmpbuf, binary.LittleEndian, data)
	if err != nil {
		log.Println("binary.Write failed:", err)
		return
	}
	header, _ := newHeader()
	send(c, S_CHAR_PARAMETER_REQ, header, tmpbuf.Bytes())
}
func signInReq(c *ChargeClient, cmd CommandId, header []byte, body []byte) error {
	if Encrypt(header) {
		c.State = State_unconn
		return errors.New("signInReq could't encrypt")
	}
	var req SignInReq
	alterSize(body, req)
	read := bytes.NewReader(body)
	err := binary.Read(read, binary.LittleEndian, &req)
	if err != nil {
		return errors.New("parse signInReq fail:" + err.Error())
	}
	printStruct(req)
	//auth and encrypt check
	rsp := &SignInRsp{}
	//rsp.Ramdom = req.Random
	rsp.LoginCertification = 0
	rsp.EncryptFlag = 0
	flag := req.Flag

	c.Code = bytetostr(req.Code[:])
	c.PointNum = req.GunNum
	c.Points = make([]ChargePoint, c.PointNum+1)
	c.StationCode = GetStationCode(c.Code)
	c.SigninTime = time.Now().UnixNano()
	Charge.AddCharge(c.Code, c)
	//server encrypt data only  server need auth and user client support encrypt
	if auth() {
		rsp.LoginCertification = 1
		if flag == 1 { //客户端支持加密
			rsp.EncryptFlag = 1
			rsp.RSAModules = modules
			rsp.Exponent = exponent
			c.Bencrypt = true
		}
		c.State = State_auth
	} else {
		c.State = State_work
		Station.AddCharge(c.StationCode, c)
	}
	send(c, S_SIGN_IN_RSP, header, rsp)

	//check time
	tm := util.BcdTimeToUnix(req.Time)
	t := time.Now().Unix()
	logs.Info(util.FormatBcdTime(req.Time))
	if math.Abs(float64(tm-t)) > float64(5) {
		logs.Info("charge time:%s,begin sync time for charge(%s)\n", util.FormatBcdTime(req.Time), c.Code)
		time.Sleep(20 * time.Millisecond)
		//		timeSync(c)
		return nil
	}
	return nil
}

//cmd:201~206
func chargeRecordReq(c *ChargeClient, cmd CommandId, header []byte, body []byte) error {
	var req ChargeRecordReq
	alterSize(body, req)
	read := bytes.NewReader(body)
	err := binary.Read(read, binary.LittleEndian, &req)
	if err != nil {
		return errors.New("parse chargeRecordReq fail:" + err.Error())
	}
	if req.PointNum == 0 {
		req.PointNum = 1
	}
	printStruct(req)

	//check the record whether is exist
	account := bytetostr(req.Account[:])
	if account[0] == 0 {
		logs.Error("chargeRecordReq account is null")
		//todo
		return nil
	}
	//don't delete user state until charge statinfo turn to free
	/*key := fmt.Sprintf("us_%s", bytetostr(req.Account[:]))
	db.RedisClient[db.User_charge_state].Del(key)*/
	Accounts.DeleteUser(account)

	start := util.BcdTimeToUnix(req.ChargeBeginTime)
	end := util.BcdTimeToUnix(req.ChargetEndTime)
	key := fmt.Sprintf("%s_%v_%v", account, start, end)

	instance := db.DbPool.GetDb()
	if instance == nil {
		logs.Error("get db instance fail")
		return errors.New("get db instance fail")
	}
	defer instance.Release()
	//1: check the record whether exist
	stmtOut, err := instance.Db.Prepare("SELECT seq FROM user_charge_record WHERE seq = ?")
	if err != nil {
		return err
	}
	defer stmtOut.Close()
	var test string
	err = stmtOut.QueryRow(key).Scan(&test) // WHERE number = 13
	if err == nil {
		logs.Info("charge record(%s) already store in mysql", key)
		rsp := &ChargeRecordRsp{}
		rsp.PointNum = req.PointNum
		rsp.Account = req.Account
		send(c, S_CHARGE_RECORD_RSP, header, rsp)
		return nil
	}

	var discountMoney uint32
	if db.RedisClient[db.Charge_state_info].Exists("Discount_"+GetStationCode(c.Code)).Val()!=0{
		discount,err:=strconv.Atoi(db.RedisClient[db.Charge_state_info].Get("Discount_"+GetStationCode(c.Code)).Val())
		if err!=nil{
			logs.Warn("parse fail:",err)
			discount=100
		}
		//get charge rate
		stmtOut, err = instance.Db.Prepare("SELECT rate  FROM c_pile  WHERE code = ?")
		if err != nil {
			return err
		}
		defer stmtOut.Close()
		var rate uint32
		err = stmtOut.QueryRow(c.Code).Scan(&rate) // WHERE number = 13
		if err == nil {
			logs.Error("get charge(%s)  rate fail", c.Code)
			return nil
		}
		//discount every 30 minute
		discountMoney=(req.ChargeTime/1800)*(rate/2)*uint32(100-discount)/100
	}

	logs.Info("bill:%s start record", key)
	orderNum := fmt.Sprintf("ch%d", time.Now().Unix())
	//2: insert the record
	tx, err := instance.Db.Begin()
	if err != nil {
		return errors.New("transaction begin fail")
	}
	stmtIns, err := tx.Prepare("INSERT user_charge_record SET seq=?,code=?,num=?,type=?,account_id=?,begin=?,end=?,time=?,reason=?,detail=?,electric=?,money=?,strategy=?,parameter=?,carid=?,way=?,electricInfo=?,order_num=?") // ? = placeholder
	if err != nil {
		tx.Rollback()
		return errors.New("Prepare insert into user_charge_record fail:" + err.Error()) // proper error handling instead of panic in your app
	}
	defer stmtIns.Close() // Close the statement when we leave main() / the program terminates
	detail := fmt.Sprintf("%d:%s", req.Reason, GetChargeFinishReason(req.Reason))
	_, err = stmtIns.Exec(key, req.Code[:], req.PointNum, req.Type, account, start, end, req.ChargeTime, req.Reason, detail, req.ElectricQuantity, req.Money, req.ChargeStrategy, req.StragtegyParameter, req.CarId[:], req.StartWay, req.TimeElectricQuantity[:], orderNum)
	if err != nil {
		tx.Rollback()
		return errors.New("insert into users_charge_record fail：" + err.Error()) // proper error handling instead of panic in your app
	}

	if _, err := tx.Exec("insert into c_consumption (account_id,money,time,order_num,remarks,discount) values(?,?,?,?,?,?)", account, req.Money-discountMoney, time.Now().Unix(), orderNum, 0,discountMoney); err != nil {
		if err := tx.Rollback(); err != nil {
			log.Println(err)
		}
		return errors.New("insert into c_consumption  fail：" + err.Error()) // proper error handling instead of panic in your app
	}
	if _, err := tx.Exec("update  c_account set money=money-? where account_id=?", req.Money-discountMoney, account); err != nil {
		tx.Rollback()
		return errors.New("update c_account  fail：" + err.Error()) // proper error handling instead of panic in your app
	}
	tx.Commit()
	logs.Info("bill:%s record success,finish reason:%s", key, detail)

	notice := &Notice{
		StationCode: c.StationCode,
		Code:        c.Code,
		Seq:         key,
		Account:     bytetostr(req.Account[:]),
		State:       Notice_state_charge_finish,
		Msg: ErrorMsg{
			time.Now().Unix(),
			GetChargeFinishTip(req.Reason),
		},
	}
	notify(notice)

	rsp := &ChargeRecordRsp{}
	rsp.PointNum = req.PointNum
	rsp.Account = req.Account
	send(c, S_CHARGE_RECORD_RSP, header, rsp)
	return nil
}

func accountInfoReq(c *ChargeClient, cmd CommandId, header []byte, body []byte) error {
	read := bytes.NewReader(body)
	var req AccountInfoReq
	err := binary.Read(read, binary.LittleEndian, &req)
	if err != nil {
		return errors.New("parse accountInfoReq fail:" + err.Error())
	}
	rsp := &AccountInfoRsp{}
	send(c, S_ACCOUNT_INFO_RSP, header, rsp)
	return nil
}

func accountCertificationReq(c *ChargeClient, cmd CommandId, header []byte, body []byte) error {
	read := bytes.NewReader(body)
	var req AccountCertificationReq
	err := binary.Read(read, binary.LittleEndian, &req)
	if err != nil {
		return errors.New("parse accountCertificationReq fail:" + err.Error())
	}
	rsp := &AccountCertificationRsp{}
	send(c, S_ACCOUNT_INFO_RSP, header, rsp)
	return nil
}

//cmd:1101~1103
func billingQueryRsp(c *ChargeClient, cmd CommandId, header []byte, body []byte) error {
	var rsp BillingQueryRsp
	alterSize(body, rsp)
	read := bytes.NewReader(body)
	err := binary.Read(read, binary.LittleEndian, &rsp)
	if err != nil {
		return errors.New("parse billingQueryRsp fail:" + err.Error())
	}

	res := struct {
		Units [12]Rate `json:"units"`
	}{rsp.Rating}
	response := HttpResponse{0, res}
	data, err := json.Marshal(response)
	if err != nil {
		logs.Error("json Marshal fail")
		return errors.New("json Marshal fail")
	}
	logs.Info("rateing:", string(data))
	httpResponse(c, data)
	return nil
}
func billingSetRsp(c *ChargeClient, cmd CommandId, header []byte, body []byte) error {
	read := bytes.NewReader(body)
	var rsp BillingSetRsp
	err := binary.Read(read, binary.LittleEndian, &rsp)
	if err != nil {
		return errors.New("parse billingSetRsp fail:" + err.Error())
	}
	info := "success"
	if rsp.Res != 0 {
		info = "fail"
	}
	logs.Info("charge(%s) billingSet  %s\n", c.Code, info)

	res := struct {
		Code string
		Data string
	}{c.Code, info}
	response := HttpResponse{0, res}
	data, err := json.Marshal(response)
	if err != nil {
		log.Println("json Marshal fail")
		return errors.New("json Marshal fail")
	}
	httpResponse(c, data)
	return nil
}

/* todo
func billingSetRsp(c *ChargeClient, cmd CommandId, header []byte, body []byte) error{
	log.Println("-------------billingSetRsp-------------->")
	read:=bytes.NewReader(body)
	var  req AccountCertificationReq
	err:=binary.Read(read,binary.LittleEndian,&req)
	if err != nil {
		return errors.New("parse billingSetRsp fail:"+err.Error())
	}
	rsp:=&BillingSetRsp{}

	send(c,S_ACCOUNT_INFO_RSP,header,rsp)
	return nil
}*/

//验证充电桩密码
func chargeCertificationReq(c *ChargeClient, cmd CommandId, header []byte, body []byte) error {
	var err error
	encrypt := Encrypt(header)
	if c.Bencrypt == true {
		if !encrypt {
			c.State = State_unconn
			return errors.New("client support RSA encrypt,but certification request not encrypt")
		}
		if body, err = RsaDecrypt(body[2:]); err != nil {
			return errors.New("rsa decrypt fail:" + err.Error())
		}
	} else {
		if encrypt {
			c.State = State_unconn
			return errors.New("client not support RSA encrypt,but certification request  encrypt")
		}
	}

	read := bytes.NewReader(body)
	var req ChargeCertificationReq
	err = binary.Read(read, binary.LittleEndian, &req)
	if err != nil {
		return errors.New("parse ChargeCertificationReq fail:" + err.Error())
	}

	//password check
	password := bytetostr(req.Password[:])
	instance := db.DbPool.GetDb()
	if instance == nil {
		logs.Error("get db instance fail")
		return errors.New("get db instance fail")
	}
	defer instance.Release()
	stmtOut, err := instance.Db.Prepare("SELECT password FROM c_pile WHERE code = ?")
	if err != nil {
		return err
	}
	defer stmtOut.Close()
	var passwd string // we "scan" the result in here

	err = stmtOut.QueryRow(c.Code).Scan(&passwd) // WHERE number = 13
	if err != nil {
		return err
	}
	//log.Printf("charge code(%s) password is: %s\n", c.Code,passwd)
	if password != passwd {
		//msg:=fmt.Sprintf("charge code(%s) password(%s) error ",c.Code,password)
		c.State = State_unconn //close client socket
		//	return errors.New(msg)
	}

	//rsp
	rsp := &ChargeCertificationRsp{}
	rsp.AESFlag = 0 //default not encrypt
	if c.Bencrypt { //client support encrypt
		//only user client encrypt code
		c.AES = req.AES
		rsp.AESFlag = 1
		logs.Debug("AES:", c.AES)
		//TODO: server supply aes code not support
	}
	c.State = State_work
	header[4] &= 0x7f
	send(c, S_CHARGE_CERTIFICATION_RSP, header, rsp)
	return nil
}

//cmd:301~304
func bmsReportReq1(c *ChargeClient, cmd CommandId, header []byte, body []byte) error {
	var req BMSReportReq1
	alterSize(body, req)
	read := bytes.NewReader(body)
	err := binary.Read(read, binary.LittleEndian, &req)
	if err != nil {
		return errors.New("parse bmsReportReq1 fail:" + err.Error())
	}
	printStruct(req)
	logs.Info("code:%s state:%d,pointNum:%d ,carConnState:%d", bytetostr(req.Code[:]), req.State, req.PointNum, req.CarConnState)
	rsp := &BMSReportRsp{}
	send(c, C_BMS_REPORT_REQ_1, header, rsp)
	return nil
}

func bmsReportReq2(c *ChargeClient, cmd CommandId, header []byte, body []byte) error {
	var req BMSReportReq2
	alterSize(body, req)
	read := bytes.NewReader(body)
	err := binary.Read(read, binary.LittleEndian, &req)
	if err != nil {
		return errors.New("parse bmsReportReq2 fail:" + err.Error())
	}
	printStruct(req)
	logs.Info("code:%s state:%d,pointNum:%d ,carConnState:%d", bytetostr(req.Code[:]), req.State, req.CarConnState)
	rsp := &BMSReportRsp{}
	send(c, C_BMS_REPORT_REQ_1, header, rsp)
	return nil
}
