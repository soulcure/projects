package charge

import (
	"encoding/json"
	"io/ioutil"
	"logs"
	"net/http"
	"strings"
	"util"
)

type Notice_state uint16

const (
	Notice_state_charge_prepare Notice_state = 1
	Notice_state_chargeing      Notice_state = 2
	Notice_state_charge_finish  Notice_state = 3
	Notice_state_charge_troube  Notice_state = 4
)

type ErrorMsg struct {
	ErrorTime int64  `json:"errorTime"`
	Msg       string `json:"msg"`
}

type Notice struct {
	StationCode string       `json:"stationCode"`
	Code        string       `json:"code"`
	Seq         string       `json:"seq"`
	Account     string       `json:"account"`
	State       Notice_state `json:"state"`
	Msg         ErrorMsg
}

var addr string

func init() {
	addr = util.CFG.Section("umgr").Key("url").String()
}

func notify(notice *Notice) {
	data, err := json.Marshal(notice)
	if err != nil {
		logs.Error("json marshal fail:", err)
		return
	}
	post(string(data), addr)
}

func post(data, addr string) {
	//表单形式提交
	logs.Info(addr)
	request, err := http.NewRequest("POST", addr, strings.NewReader(data))

	request.Header.Set("Content-Type", "application/json;charset=UTF-8")
	//request.Header.Set("Content-Type", "application/x-www-form-urlencoded")
	client := http.Client{}
	resp, err := client.Do(request)
	if err != nil {
		logs.Error(err.Error())
		return
	}
	respBytes, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		logs.Error(err.Error())
		return
	}

	//str := (*string)(unsafe.Pointer(&respBytes))
	type Res struct {
		Res int
		Msg string
	}
	var res Res
	json.Unmarshal(respBytes, &res)
	if res.Res != 0 {
		logs.Error("send user system fail:", res.Msg)
	}
}
