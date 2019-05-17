package service

import (
	"chargingpile/models"
	"chargingpile/utils"
	"github.com/astaxie/beego/logs"
)

func DecryptPhone(js_code, encryptedData, iv, userId, token string) (*models.User, error) {
	url := "https://api.weixin.qq.com/sns/jscode2session?appid=" + appId + "&secret=" + secret + "&js_code=" + js_code + "&grant_type=authorization_codere"
	encryptMap := make(map[string]interface{})
	err := utils.ClientWxApi(url, encryptMap)
	if err != nil {
		logs.Error("DecryptPhone 调用ClientWxApi 错误 :%v", err)
		return nil, err
	}
	phoneMap := utils.DecryptWeChatData(appId, encryptMap["session_key"].(string), encryptedData, iv)
	err = models.Update(userId, phoneMap["phoneNumber"].(string), phoneMap["countryCode"].(string))
	if err != nil {
		logs.Error("DecryptPhone update db err :%v", err)
		return nil, err
	}
	var user models.User
	user.UserId = userId
	user.Status = 1
	user.Phone = phoneMap["phoneNumber"].(string)
	user.CountryCode = phoneMap["countryCode"].(string)

	models.GetChange(token, "1")
	logs.Info("DecryptPhone success ! user data:%+v", user)
	return &user, nil
}

func RegistPhoneService(reqMap map[string]string, result *utils.Result) *utils.Result {
	//models.Update(reqMap["userId"], )
	return result
}
