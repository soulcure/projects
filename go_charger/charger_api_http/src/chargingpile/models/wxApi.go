package models

import (
	"encoding/json"
	"github.com/astaxie/beego"
	"github.com/astaxie/beego/logs"
	"io/ioutil"
	"net/http"
)

type WxApi struct {
	Openid     string `json:"openid"`      //用户唯一标识
	SessionKey string `json:"session_key"` //会话密钥
}

/**
 * 获取微信用户信息和用户手机号码
 * 小程序登录时获取jsCode
 * 临时登录凭证code只能使用一次
 */
func ReqWxApi(jsCode string) (*WxApi, error) {
	var result WxApi

	appId := beego.AppConfig.String("weixin::appid")   //小程序ID
	secret := beego.AppConfig.String("weixin::secret") //小程序密钥

	url := "https://api.weixin.qq.com/sns/jscode2session?appid=" + appId + "&secret=" + secret + "&js_code=" + jsCode + "&grant_type=authorization_codere"
	logs.Debug("get WxApi url:", url)

	rsp, err := http.Get(url)
	if err != nil {
		return &result, err
	}
	body := rsp.Body

	defer func() {
		if err := body.Close(); err != nil {
			logs.Error("http io close err :", err)
		}
	}()

	data, err := ioutil.ReadAll(body)
	if err != nil {
		return &result, err
	}

	if err := json.Unmarshal(data, &result); err != nil {
		logs.Error("resultMap err :%v", err)
	}
	return &result, err

}
