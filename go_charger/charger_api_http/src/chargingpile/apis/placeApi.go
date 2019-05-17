package apis

import (
	"chargingpile/service"
	"chargingpile/utils"
	"encoding/json"
	"github.com/astaxie/beego"
	"github.com/astaxie/beego/logs"
)

type PlaceApi struct {
	beego.Controller
}

//获取充电桩坐标地理位置
func (p *PlaceApi) GetPosition() {
	bytes := p.Ctx.Input.RequestBody
	result := utils.Result{Code: utils.OK, Msg: utils.Msg(utils.OK)}
	defer utils.Defer(&result, p.Ctx)
	reqMap := make(map[string]float64)
	err := json.Unmarshal(bytes, &reqMap)
	if err != nil {
		logs.Error("GetPosition json Unmarshal request body err :%v", err)
		result.Code = utils.PARAM_ERR
		result.Msg = utils.Msg(utils.PARAM_ERR)
		return
	}
	logs.Debug("获取充电桩位置 传参是:%v", reqMap)
	stations := service.SearchStation(reqMap)
	result.Data = stations
	logs.Debug("返回充电桩地理位置信息:%+v", stations)
	return
}
