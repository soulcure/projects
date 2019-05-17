package apis

import (
	"chargingpile/models"
	"chargingpile/utils"
	"encoding/json"
	"github.com/astaxie/beego"
	"github.com/astaxie/beego/logs"
)

type ActivityApi struct {
	beego.Controller
}

type ActivityReq struct {
	Id int `json:"id"`
}

func (activity *ActivityApi) GetActivity() {
	result := utils.Result{Code: utils.PARAM_ERR, Msg: utils.Msg(utils.PARAM_ERR)}
	defer utils.Defer(&result, activity.Ctx)

	activities, err := models.QueryActivity()
	if err != nil {
		result.Code = utils.PARAM_ERR
		result.Msg = utils.Msg(utils.PARAM_ERR)
		return
	}
	result.Code = utils.OK
	result.Msg = utils.Msg(utils.OK)
	result.Data = activities
	return

}

func (activity *ActivityApi) ActDetail() {
	result := utils.Result{Code: utils.PARAM_ERR, Msg: utils.Msg(utils.PARAM_ERR)}
	defer utils.Defer(&result, activity.Ctx)

	body := activity.Ctx.Input.RequestBody

	logs.Debug("activity body:", string(body))

	var ar ActivityReq
	if err := json.Unmarshal(body, &ar); err != nil {
		result.Code = utils.PARAM_ERR
		result.Msg = utils.Msg(utils.PARAM_ERR)
		logs.Error("json 序列号错误 !", err)
	} else {
		act, err := models.QueryActivityById(ar.Id)
		if err != nil {
			result.Code = utils.PARAM_ERR
			result.Msg = utils.Msg(utils.PARAM_ERR)
			return
		}
		result.Code = utils.OK
		result.Msg = utils.Msg(utils.OK)
		result.Data = act
		return
	}

}
