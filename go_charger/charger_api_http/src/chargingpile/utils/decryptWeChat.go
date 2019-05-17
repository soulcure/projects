package utils

import (
	"encoding/json"
	"github.com/astaxie/beego/logs"
	"github.com/xlstudio/wxbizdatacrypt"
)

//解密微信返回数据
func DecryptWeChatData(appid, sessionKey, encryptedData, iv string) map[string]interface{} {
	pc := wxbizdatacrypt.WxBizDataCrypt{AppID: appid, SessionKey: sessionKey}
	jsonStr, err := pc.Decrypt(encryptedData, iv, true)
	if err != nil {
		logs.Error("DecryptWeChatData error :%v", err)
		return nil
	}
	logs.Info("jsonStr: %v", jsonStr)
	mapResult := make(map[string]interface{})
	switch jsonStr.(type) {
	case string:
		json.Unmarshal([]byte(jsonStr.(string)), &mapResult)
		break
	default:
		logs.Error("DecryptWeChatData can't Conversion jsonStr")
		return nil
	}
	return mapResult
}
