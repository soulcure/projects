package apis

import (
	"chargingpile/models"
	"chargingpile/service"
	"chargingpile/utils"
	"encoding/json"
	"github.com/astaxie/beego"
	"github.com/astaxie/beego/logs"
)

type RegistApi struct {
	beego.Controller
}

func (registApi *RegistApi) WXRegist() { //真正的注册手机号码
	headers := registApi.Ctx.Request.Header
	logs.Info("WXRegist headers : %v", headers)
	result := utils.Result{Code: utils.OK, Msg: utils.Msg(utils.OK)}
	defer utils.Defer(&result, registApi.Ctx)
	bytes := models.Get(headers["Token"][0], 2)
	if bytes == nil {
		result.Code = utils.NOT_LOGIN
		result.Msg = utils.Msg(utils.NOT_LOGIN)
		return
	}
	cacheMap := make(map[string]string)
	err := json.Unmarshal(bytes, &cacheMap)
	simple, _ := models.QuerySimple(cacheMap["openId"])
	if simple != nil {
		if *simple == 1 {
			result.Code = utils.RE_REGIST
			result.Msg = utils.Msg(utils.RE_REGIST)
			logs.Warn("WXRegist query user re_regist stat=%v ", *simple)
			return
		}
	}

	if encryptedData, ok := headers["X-Wx-Encrypted-Data"]; !ok {
		result.Code = utils.PARAM_ERR
		result.Msg = utils.Msg(utils.PARAM_ERR)
		logs.Error("WXRegist header param err ! encryptedData : %v", encryptedData)
		return
	}

	if iv, ok := headers["X-Wx-Iv"]; !ok {
		result.Code = utils.PARAM_ERR
		result.Msg = utils.Msg(utils.PARAM_ERR)
		logs.Error("WXRegist header param err ! iv : %v", iv)
		return
	}

	if js_code, ok := headers["Js-Code"]; !ok {
		result.Code = utils.PARAM_ERR
		result.Msg = utils.Msg(utils.PARAM_ERR)
		logs.Error("WXRegist header param err ! Js-code : %v", js_code)
		return
	}
	//解密获取手机
	user, err := service.DecryptPhone(headers["Js-Code"][0], headers["X-Wx-Encrypted-Data"][0], headers["X-Wx-Iv"][0], cacheMap["openId"], headers["Token"][0])
	if err != nil {
		result.Code = utils.AUTHO_ERR
		result.Msg = utils.Msg(utils.AUTHO_ERR)
		logs.Error("WXRegist header param err : %v  ", err)
		return
	} else if user == nil {
		result.Code = utils.RE_LOGIN
		result.Msg = utils.Msg(utils.RE_LOGIN)
		logs.Warn("WXRegist DecryptPhone relogin")
		return
	}
	result.Data = user
	return
}

func (registApi *RegistApi) RegistPhone() {
	headers := registApi.Ctx.Request.Header
	logs.Info("RegistPhone headers : %v", headers)
	result := utils.Result{Code: utils.OK, Msg: utils.Msg(utils.OK)}
	defer utils.Defer(&result, registApi.Ctx)
	bytes := models.Get(headers["Token"][0], 2)
	if bytes == nil {
		result.Code = utils.NOT_LOGIN
		result.Msg = utils.Msg(utils.NOT_LOGIN)
	}
	cacheMap := make(map[string]string)
	json.Unmarshal(bytes, &cacheMap)
	simple, _ := models.QuerySimple(cacheMap["openId"])
	if simple != nil {
		if *simple == 1 {
			result.Code = utils.RE_REGIST
			result.Msg = utils.Msg(utils.RE_REGIST)
			logs.Warn("RegistPhone query user[%v] re_regist stat=%v ", cacheMap["openId"], *simple)
			return
		}
	}
	//go
	service.RegistPhoneService(cacheMap, &result)
	return

}
