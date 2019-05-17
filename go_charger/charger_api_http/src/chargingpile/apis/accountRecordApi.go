package apis

import (
	"chargingpile/models"
	"chargingpile/service"
	"chargingpile/utils"
	"encoding/json"
	"github.com/astaxie/beego"
	"github.com/astaxie/beego/logs"
)

type AccountRecordApi struct {
	beego.Controller
}

//账户记录
func (accountRecord *AccountRecordApi) GetRecord() {
	headers := accountRecord.Ctx.Request.Header
	token := headers["Token"][0]
	bytes := models.Get(token, 2)
	result := utils.Result{Code: utils.OK, Msg: utils.Msg(utils.OK)}
	defer utils.Defer(&result, accountRecord.Ctx)
	if bytes == nil {
		result.Code = utils.SERVER_ERR
		result.Msg = utils.Msg(utils.SERVER_ERR)
		logs.Error("GetRecord get token is :%v", bytes)
		return
	}
	cacheMap := make(map[string]string)
	json.Unmarshal(bytes, &cacheMap)
	requestBody := accountRecord.Ctx.Input.RequestBody
	remarkMap := make(map[string]int)
	err := json.Unmarshal(requestBody, &remarkMap)
	if err != nil {
		result.Code = utils.PARAM_ERR
		result.Msg = utils.Msg(utils.PARAM_ERR)
		logs.Error("GetRecord json.Unmarshal is err:%v", err)
		return
	}
	remark := remarkMap["remark"]
	pageNo := remarkMap["pageNo"]
	pageSize := remarkMap["pageSize"]
	account := service.QueryRecord(cacheMap["openId"], remark, pageNo, pageSize)
	if account != nil {
		result.Data = account
		return
	}
	return

}

//获取账单详情
func (accountRecord *AccountRecordApi) GetUserChargeRecord() {
	headers := accountRecord.Ctx.Request.Header
	token := headers["Token"][0]
	bytes := models.Get(token, 2)
	result := utils.Result{Code: utils.OK, Msg: utils.Msg(utils.OK)}
	defer utils.Defer(&result, accountRecord.Ctx)
	if bytes == nil {
		result.Code = utils.SERVER_ERR
		result.Msg = utils.Msg(utils.SERVER_ERR)
		logs.Error("GetUserChargeRecord error can not get session !")
		return
	}
	cacheMap := make(map[string]string)
	json.Unmarshal(bytes, &cacheMap)
	requestBody := accountRecord.Ctx.Input.RequestBody
	reqMap := make(map[string]string)
	err := json.Unmarshal(requestBody, &reqMap)
	if err != nil {
		result.Code = utils.PARAM_ERR
		result.Msg = utils.Msg(utils.PARAM_ERR)
		logs.Error("GetUserChargeRecord json.Unmarshal error:%v", err)
		return
	}
	details := service.GetUserChargeRecordService(reqMap["seq"], cacheMap["openId"])
	if details != nil {
		result.Data = details
		return
	}
	return
}
