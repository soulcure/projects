package apis

import (
	"chargingpile/models"
	"chargingpile/service"
	"chargingpile/utils"
	"encoding/json"
	"github.com/astaxie/beego"
	"github.com/astaxie/beego/logs"
)

type ScanCodeApi struct {
	beego.Controller
}

//扫二维码
func (scanCodeApi *ScanCodeApi) ScanCodeOrReserve() { //需要一个充电桩id,充电桩状态,用户的id
	bytes := scanCodeApi.Ctx.Input.RequestBody
	logs.Debug("扫码接口被调用")
	scanBean := models.ScanBean{}
	err := json.Unmarshal(bytes, &scanBean)
	if err != nil {
		result := utils.Result{Code: utils.SERVER_ERR, Msg: utils.Msg(utils.SERVER_ERR)}
		utils.Defer(&result, scanCodeApi.Ctx)
		logs.Error("ScanCodeOrReserve json.Unmarshal err :%v", err)
		return
	}
	codeService := service.ScanCodeService(&scanBean)
	defer utils.Defer(codeService, scanCodeApi.Ctx)
	return
}
