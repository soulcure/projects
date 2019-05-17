package apis

import (
	"chargingpile/models"
	"chargingpile/utils"
	"encoding/json"
	"github.com/astaxie/beego"
	"github.com/astaxie/beego/logs"
)

type BalanceApi struct {
	beego.Controller
}

type BalanceReq struct {
	OpenId string `json:"openId"` //用户id
}

type BalanceRsp struct {
	Money int `json:"money"`
}

func (balanceApi *BalanceApi) Balance() { //余额
	result := models.ProtocolRsp{Code: utils.OK, Msg: utils.Msg(utils.OK)}
	body := balanceApi.Ctx.Input.RequestBody

	defer result.ResponseWriter(balanceApi.Ctx)

	logs.Debug("charger body:", string(body))
	var br BalanceReq
	if err := json.Unmarshal(body, &br); err != nil {
		result.Code = utils.PARAM_ERR
		result.Msg = utils.Msg(utils.PARAM_ERR)
		logs.Error("json 序列号错误 !", err)

	} else {
		if len(br.OpenId) == 0 {
			result.Code = utils.PARAM_ERR
			result.Msg = utils.Msg(utils.PARAM_ERR)
			logs.Error("传入参数Js_code异常 !", err)
			return
		}

		if acc, err := models.GetAccount(br.OpenId); err != nil {
			result.Code = utils.PARAM_ERR
			result.Msg = utils.Msg(utils.PARAM_ERR)
			logs.Error("传入参数Js_code异常 !", err)
		} else {
			var balanceRsp BalanceRsp
			balanceRsp.Money = acc.Money
			result.Code = utils.OK
			result.Msg = utils.Msg(utils.OK)
			result.Data = balanceRsp
		}

	}

}
