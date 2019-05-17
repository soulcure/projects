package service

import (
	"chargingpile/models"
	"encoding/json"
	"github.com/astaxie/beego/logs"
)

func QueryCharging(token string) map[string]string {
	bytes := models.Get(token, 2)
	tokenMap := make(map[string]string)
	if bytes != nil {
		json.Unmarshal(bytes, &tokenMap)
	}
	//ChargingStatus := models.ChargingStatus{}
	if openId, ok := tokenMap["openId"]; ok {
		statusMap := models.HGet("us_"+openId, 1)
		if statusMap != nil {
			logs.Info("user:%v nickName:%v 查询充电状态 :%+v", openId, tokenMap["nickName"], statusMap)
			return statusMap
		}
	}
	return nil
}
