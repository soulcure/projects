package apis

import (
	"chargingpile/models"
	"chargingpile/utils"
	"encoding/json"
	"github.com/astaxie/beego"
	"github.com/astaxie/beego/logs"
	"strings"
)

type ChargerApi struct {
	beego.Controller
}

type ChargerReq struct {
	JsCode   string `json:"js_code"`
	TotalFee int    `json:"total_fee"`
}

func (accountApi *ChargerApi) Recharge() { //充值
	result := models.ProtocolRsp{Code: utils.OK, Msg: utils.Msg(utils.OK)}

	addr := accountApi.Ctx.Request.RemoteAddr
	ipAndPort := strings.SplitN(addr, ":", 2)
	var ip string
	if ipAndPort != nil && len(ipAndPort) == 2 {
		ip = ipAndPort[0]
	}

	body := accountApi.Ctx.Input.RequestBody
	defer result.ResponseWriter(accountApi.Ctx)

	logs.Debug("charger body:", string(body))
	var cr ChargerReq
	if err := json.Unmarshal(body, &cr); err != nil {
		result.Code = utils.PARAM_ERR
		result.Msg = utils.Msg(utils.PARAM_ERR)
		logs.Error("json 序列号错误 !", err)
	} else {
		if len(cr.JsCode) == 0 {
			result.Code = utils.PARAM_ERR
			result.Msg = utils.Msg(utils.PARAM_ERR)
			logs.Error("传入参数Js_code异常 !", err)
			return
		}

		if cr.TotalFee < 0 {
			result.Code = utils.PARAM_ERR
			result.Msg = utils.Msg(utils.PARAM_ERR)
			logs.Error("传入参数金额异常 !", err)
			return
		}

		wxApi, err := models.ReqWxApi(cr.JsCode)
		if err != nil {
			result.Code = utils.WX_API_ERR
			result.Msg = utils.Msg(utils.WX_API_ERR)
			return
		}

		tradeNo := models.GetOrderNum()
		totalFee := cr.TotalFee

		var orderReq models.OrderReq
		if jsonObj, err := orderReq.PayOrderReq(tradeNo, totalFee, ip, wxApi.Openid); err != nil {
			result.Code = utils.PARAM_ERR
			result.Msg = utils.Msg(utils.PARAM_ERR)
			logs.Error(err)
		} else {
			result.Code = utils.OK
			result.Msg = utils.Msg(utils.OK)
			result.Data = jsonObj
		}
	}

}
