package charge

import (
	"errors"
	"net/http"
	"reflect"
	"strconv"
	"util"

	"bytes"
	"encoding/binary"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"logs"
	"time"
	"log"
)

type chargeStragety uint16

const (
	charge_stragety_in_time    chargeStragety = 0
	charge_stragety_timer      chargeStragety = 1
	charge_stragety_ppointment chargeStragety = 2
)

type chargeStart struct {
	StationCode string
	Code        string
	Pointnum    uint8
	Type        uint32
	Strategy    uint32
	Parameter   uint32
	Timer       int
	UserId      string
	Money       int
}

type ChargeCode int16

const (
	charge_success           ChargeCode = 0 //成功
	charge_timeout           ChargeCode = 1 //充电桩请求超时
	charge_pile_unused       ChargeCode = 2 // 充电桩故障不可充电
	charge_not_free          ChargeCode = 3 //无空闲桩可预约
	charge_car_unconn        ChargeCode = 4
	charge__pareameter_error ChargeCode = -1 //请求参数错误
)

func HttpServerStart() {
	addr := util.CFG.Section("http").Key("addr").String()
	log.Println("http listen addr:", addr)
	mux := http.NewServeMux()
	mux.HandleFunc("/charge/start", start)
	err := http.ListenAndServe(addr, mux)
	if err != nil {
		logs.Error("ListenAndServe: ", err)
	}
}

func start(w http.ResponseWriter, r *http.Request) {

	if r.Method != "POST" {
		http.Error(w, "Must use post  method", http.StatusMethodNotAllowed)
		return
	}
	if s, err := ioutil.ReadAll(r.Body); err != nil {
		httpError(w, charge__pareameter_error, "parameter error")
		return
	} else {
		logs.Info(string(s))
		var res chargeStart
		var req ChargeStartReq
		err := json.Unmarshal(s, &res)
		if err != nil {
			httpError(w, charge__pareameter_error, "parameter error")
			return
		}
		log.Printf("%+v", res)
		if res.Type == uint32(charge_stragety_ppointment) {
			cp := Station.CanPpointment(res.StationCode)
			if !cp {
				res := make(map[string]interface{})
				res["code"] = -1
				res["data"] = "ppointment fail,not enought charge"
				data, err := json.Marshal(res)
				if err != nil {
					logs.Error("json fail:", err)
					return
				} else {
					fmt.Fprint(w, data)
				}
			}
		} else if res.Type == 3 {
			client := Charge.FindCharge(res.Code)
			if client == nil {
				logs.Warn("charge:%s:%d not sign in\n", res.Code, res.Pointnum)
				httpError(w, charge_pile_unused, "charge:"+res.Code+" not sign in")
				return
			}
			logs.Info("stop charge for charge:%s:%d ", res.Code, res.Pointnum)
			stopCharge(client, req.PointNum)

		} else if res.Type == uint32(charge_stragety_in_time) {
			strtobyte(req.Account[:], res.UserId)
			logs.Debug(bytetostr(req.Account[:]))
			client := Charge.FindCharge(res.Code)
			if client == nil {
				logs.Warn("charge (%s) not sign in\n", res.Code)
				httpError(w, charge_pile_unused, "charge:"+res.Code+" not sign in")
				return
			}
			logs.Info("charge(%s) num(%d) start charge for user(%s)\n", res.Code, res.Pointnum, res.UserId)

			req.PointNum = res.Pointnum
			req.Type = res.Type
			req.Strategy = res.Strategy
			req.Parameter = res.Parameter
			fmt.Sprintf("%s%d", res.Code, time.Now().Unix())
			strtobyte(req.ChargeSeq[:], fmt.Sprintf("%s%d", res.Code, time.Now().Unix()))
			//	req.Timer=res.Timer
			if client.State != State_work {
				logs.Warn("charge(%s) state(%d)  sign in fail", client.Code, client.State)
				httpError(w, charge_pile_unused, "charge:"+res.Code+"  sign in fail")
				return
			}
			if req.PointNum > client.PointNum {
				logs.Warn("charge (%s) point num(%d) not exist\n", res.Code, res.Pointnum)
				httpError(w, charge_pile_unused, " point num not exist")
				return
			}
			//check whether is available
			if !(client.Available(req.PointNum) && client.SetOccupy(req.PointNum, true)) {
				httpError(w, charge_not_free, " point num not available")
				return
			}
			//todo charge work correct until state report,so not report charge unused
			if client.GetCarState(req.PointNum) == 0 {
				logs.Info("charge:%s:%d car not connect", res.Code, res.Pointnum)
				httpError(w, charge_car_unconn, " car not connect")
				return
			}
			if Accounts.GetUser(res.UserId) != nil {
				logs.Warn("user (%s) already charging,not support multi user charge", res.UserId)
				httpError(w, charge_pile_unused, " point num not exist")
				return
			}
			user := &User{res.UserId, res.Money}
			Accounts.AddUser(user)

			logs.Info("send charge start request")
			header, _ := newHeader()
			send(client, S_CHARGE_START_REQ, header, &req)
			data, ok := waitTime(client, 8*time.Second)
			if !ok {
				rsp := struct {
					Code int
					Data string
				}{}
				if err := json.Unmarshal(data, &rsp); err != nil {
					Accounts.DeleteUser(res.UserId)
				} else {
					if rsp.Code != 0 {
						Accounts.DeleteUser(res.UserId)
					}
				}
				fmt.Fprint(w, string(data))
			} else {
				logs.Info("start charge timeout")
				client.SetOccupy(req.PointNum, false)
				Accounts.DeleteUser(res.UserId)
				httpError(w, charge_timeout, "timeout")
			}
		}
	}
	return
}

func httpError(w http.ResponseWriter, code ChargeCode, msg string) {
	res := make(map[string]interface{})
	res["code"] = code
	res["data"] = msg
	data, err := json.Marshal(res)
	if err != nil {
		logs.Error("json fail:", err)
		return
	}
	fmt.Fprint(w, string(data))
}
func getValue(r *http.Request, key string, i interface{}) error {
	r.ParseForm()
	val := r.Form.Get(key)
	if val == "" {
		return errors.New(key + " field is missing")
	}
	logs.Debug(val)
	v := reflect.ValueOf(i).Elem()
	switch v.Kind() {
	case reflect.Uint8, reflect.Uint16, reflect.Uint32:
		n, err := strconv.Atoi(val)
		if err != nil {
			return err
		}
		v.SetUint(uint64(n))
	case reflect.Array:
		if len(val) > v.Len() {
			return errors.New(key + " field  string value length is too much")
		}
		for k := range val {
			v.Index(k).SetUint(uint64(val[k]))
		}

	case reflect.String:
		v.SetString(val)
	}
	return nil
}

func newHeader() ([]byte, error) {
	//log.Print(ulpByte)
	header := Header{}
	header.Flag[0] = FLAG0
	header.Flag[1] = FLAG1
	header.Seq = 0
	header.Version |= Version

	buf := new(bytes.Buffer)
	var err error
	err = binary.Write(buf, binary.LittleEndian, header)
	if err != nil {
		logs.Error("binary.Write failed:", err)
		return nil, err
	}
	return buf.Bytes(), nil
}

//-1 :timeout 0:ok
func waitTime(c *ChargeClient, t time.Duration) ([]byte, bool) {
	c.t=time.Now().Unix()+ int64(t/1e9)-1
	timer := time.NewTimer(t)
	for {
		select {
		case <-timer.C:
			return nil, true
		case data := <-c.res:
			return data, false
		}
	}
}
