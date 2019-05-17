package utils

import (
	"bytes"
	"chargingpile/models"
	"encoding/json"
	"github.com/astaxie/beego"
	"github.com/astaxie/beego/logs"
	"io/ioutil"
	"net/http"
	"time"
)

var url string

func init() {
	url = beego.AppConfig.String("innerapi::ip") + "/charge/start"
}
func HttpPileApi(scanCode *models.ScanBean) *RespResult {
	c := &http.Client{
		Timeout: 5 * time.Second,
	}
	data, err := json.Marshal(scanCode)
	if err != nil {
		logs.Error("HttpPileApi json Marshal err:%v", err)
		return nil
	}
	reader := bytes.NewReader(data)
	resp, err := c.Post(url, "application/json", reader)
	if err != nil {
		logs.Error("HttpPileApi Post err:%v", err)
		respResult := RespResult{CHARGE_TIMEOUT, "充电桩响应超时"}
		return &respResult
	}
	defer resp.Body.Close()
	body, _ := ioutil.ReadAll(resp.Body)
	respResult := RespResult{}
	json.Unmarshal(body, &respResult)
	return &respResult
}
