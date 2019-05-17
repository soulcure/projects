package utils

import (
	"bytes"
	"encoding/json"
	"github.com/astaxie/beego/logs"
	"io/ioutil"
	"net/http"
	"time"
)

//获取微信的session_id ,openid
func ClientWxApi(url string, wxRespMap map[string]interface{}) error {
	c := &http.Client{
		Timeout: 5 * time.Second,
	}
	resp, err := c.Get(url)
	if err != nil {
		logs.Error("Http ClientWxApi get url:%v err :%v", url, err)
		return err
	}
	body := resp.Body
	defer resp.Body.Close()
	data, err := ioutil.ReadAll(body)
	if err != nil {
		logs.Error("Http ClientWxApi ioutil read all err :%v", err)
		return err
	}
	err = json.Unmarshal(data, &wxRespMap)

	if err != nil {
		logs.Error("Http ClientWxApi Json Unmarshal err :%v", err)
		return err
	}
	logs.Info("ClientWxApi Get resp :%v", wxRespMap)
	return nil
}

func SendTemplate(url string, data []byte, respMap map[string]interface{}) error {
	//需要post提交
	c := &http.Client{
		Timeout: 5 * time.Second,
	}
	reader := bytes.NewReader(data)
	resp, err := c.Post(url, "application/json", reader)
	if err != nil {
		logs.Error("sendTemlpate Post url:%v  err:%v", url, err)
		return err
	}
	defer resp.Body.Close()
	body, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		logs.Error("sendTemlpate Resp ioutil.ReadAll err :%v", err)
		return err
	}
	json.Unmarshal(body, &respMap)
	if err != nil {
		logs.Error("sendTemlpate Resp Json Unmarshal err :%v", err)
		return err
	}
	logs.Info("推送通知结果:%v", respMap)
	return nil
}
