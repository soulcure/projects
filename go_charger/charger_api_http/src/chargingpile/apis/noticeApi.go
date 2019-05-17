package apis

import (
	"chargingpile/models"
	"chargingpile/service"
	"chargingpile/utils"
	"encoding/json"
	"github.com/astaxie/beego"
	"github.com/astaxie/beego/logs"
)

type NoticeApi struct {
	beego.Controller
}

func (noticeApi *NoticeApi) CallBackNotice() {
	bytes := noticeApi.Ctx.Input.RequestBody
	result := utils.Result{Code: utils.OK, Msg: utils.Msg(utils.OK)}
	defer utils.Defer(&result, noticeApi.Ctx)
	advice := models.Advice{}
	err := json.Unmarshal(bytes, &advice)
	if err != nil {
		result.Code = utils.PARAM_ERR
		result.Msg = utils.Msg(utils.PARAM_ERR)
		logs.Error("CallBackNotice json.Unmarshal error:%v ", err)
		return
	}
	//别的状态不发送消息,由用户使用小程序查看状态
	if 3 == advice.State || 4 == advice.State {
		err = service.NoticeService(&advice)
		if err != nil {
			result.Code = utils.WX_API_ERR
			result.Msg = utils.Msg(utils.WX_API_ERR)
			logs.Error("CallBackNotice WX_API_ERR")
			return
		}
		return
	}
	result.Code = utils.PARAM_ERR
	result.Msg = utils.Msg(utils.PARAM_ERR)
	logs.Error("CallBackNotice param error:%+v ", advice)
	return
}
