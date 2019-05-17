package apis

import (
	"chargingpile/service"
	"chargingpile/utils"
	"github.com/astaxie/beego"
)

type ChargingStatusApi struct {
	beego.Controller
}

//查询充电状态
func (chargingStatusApi *ChargingStatusApi) GetChargingStatus() {
	//redis 1号库查询是否在充电
	headers := chargingStatusApi.Ctx.Request.Header
	result := utils.ReGistResult{Code: utils.OK, Msg: utils.Msg(utils.OK)}
	defer utils.DeferRegist(&result, chargingStatusApi.Ctx)

	if _, ok := headers["Token"]; !ok {
		result.Code = utils.PARAM_ERR
		result.Msg = utils.Msg(utils.PARAM_ERR)
		return
	}
	chargingMap := service.QueryCharging(headers["Token"][0])
	if chargingMap == nil {
		result.Code = utils.USER_UNUSED
		result.Msg = utils.Msg(utils.USER_UNUSED)
		return
	}
	result.Data = chargingMap
	return
}
