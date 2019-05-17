package service

import (
	"chargingpile/models"
	"github.com/astaxie/beego/logs"
)

func SearchStation(reqMap map[string]float64) []*models.Station {
	//longitude 经度,latitude 纬度
	stations, err := models.QueryStation(reqMap["longitude"], reqMap["latitude"])
	if err != nil {
		logs.Error("queryStation err :%v", err)
		return nil
	}
	logs.Info("user SearchStation success ! data:%v", stations)
	return stations
}
