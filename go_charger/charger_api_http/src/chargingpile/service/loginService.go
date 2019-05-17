package service

import (
	"chargingpile/models"
	"chargingpile/utils"
	"encoding/json"
	"github.com/astaxie/beego"
	"github.com/astaxie/beego/logs"
	"strconv"
	"time"
)

var appId string
var secret string

func init() {
	appId = beego.AppConfig.String("weixin::appid")
	secret = beego.AppConfig.String("weixin::secret")
}

func LoginService(jsCode, encryptedData, iv, token string) (*models.User, error) {
	encryptMap := make(map[string]interface{})
	url := "https://api.weixin.qq.com/sns/jscode2session?appid=" + appId + "&secret=" + secret + "&js_code=" + jsCode + "&grant_type=authorization_codere"
	err := utils.ClientWxApi(url, encryptMap) //微信获取的基础信息
	if err != nil {
		logs.Info("LoginService ClientWxApi encryptMap :%v ,error:%v", encryptMap, err)
		return nil, err
	}
	dataMap := utils.DecryptWeChatData(appId, encryptMap["session_key"].(string), encryptedData, iv) //解密
	userInfo, err := models.Query(dataMap["openId"].(string))
	if err != nil {
		logs.Error("LoginService query userInfo err :%v", err)
		return nil, err
	}
	if userInfo != nil {
		cacheMap := make(map[string]string)
		cacheMap["openId"] = userInfo.UserId
		cacheMap["nickName"] = userInfo.NickName
		cacheMap["status"] = strconv.Itoa(userInfo.Status)
		bytes, _ := json.Marshal(cacheMap)
		models.Set(token, bytes, 2, 60*60*24*15)
		logs.Info("user login success :%+v", userInfo)
		return userInfo, nil
	}
	dataMap["createTime"] = time.Now().Unix()
	dataMap["status"] = 0

	err = models.Insert(dataMap)
	if err != nil {
		logs.Error("login method insert user err :%v", err)
		return nil, err
	}
	cacheMap := make(map[string]string)
	cacheMap["openId"] = dataMap["openId"].(string)
	cacheMap["nickName"] = dataMap["nickName"].(string)
	bytes, _ := json.Marshal(cacheMap)
	models.Set(token, bytes, 2, 60*60*24*15)

	var u models.User
	u.UserId = dataMap["openId"].(string)
	u.NickName = dataMap["nickName"].(string)
	if dataMap["gender"] == 1 {
		u.Gender = 1
	} else {
		u.Gender = 0
	}
	u.CreateTime = dataMap["createTime"].(int64)
	u.City = dataMap["city"].(string)
	u.Status = dataMap["status"].(int)
	u.Money = 0
	logs.Info("new user login success ! userInfo:%+v", u)
	return &u, nil
}

/*
func CheckUser(token string, bool bool) (*models.User, error) {
	existsSession := redis.ExistsSession(token, false)
	if existsSession && bool {
		bytes, err := redis.GetSession(token)
		if err != nil {
			return nil, err
		}
		userMap := make(map[string]string)
		json.Unmarshal(bytes, &userMap)
		user, err := models.Query(userMap["openid"])
		if err != nil {
			return nil, err
		}
		return user, nil
	}
	return nil, nil
}*/
