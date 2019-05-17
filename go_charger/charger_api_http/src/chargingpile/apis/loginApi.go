package apis

import (
	"chargingpile/models"
	"chargingpile/service"
	"chargingpile/utils"
	"github.com/astaxie/beego"
	"github.com/astaxie/beego/logs"
	"github.com/satori/go.uuid"
)

type LoginApi struct {
	beego.Controller
}

func (loginApi *LoginApi) Login() { //授权登录
	ip := loginApi.Ctx.Request.RemoteAddr
	logs.Info("connection ip:%v request login !", ip)
	headers := loginApi.Ctx.Request.Header
	logs.Info("headers :", headers)
	result := utils.ReGistResult{Code: utils.OK, Msg: utils.Msg(utils.OK)}
	defer utils.DeferRegist(&result, loginApi.Ctx)

	if token, ok := headers["Token"]; ok {
		//token := headers["token"]
		if session := models.Exists(token[0], false); session {
			result.Code = utils.RE_LOGIN
			result.Msg = utils.Msg(utils.RE_LOGIN)
			return
		} else {
			result.Code = utils.TOKEN_ERR
			result.Msg = utils.Msg(utils.TOKEN_ERR)
			return
		}
	}
	if _, ok := headers["Js-Code"]; !ok {
		result.Code = utils.PARAM_ERR
		result.Msg = utils.Msg(utils.PARAM_ERR)
		logs.Warning("Login Missing header parameter :Js_code!")
		return
	}
	if _, ok := headers["X-Wx-Encrypted-Data"]; !ok {
		result.Code = utils.PARAM_ERR
		result.Msg = utils.Msg(utils.PARAM_ERR)
		logs.Warning("Login Missing header parameter : X-Wx-Encrypted-Data !")
		return
	}
	if _, ok := headers["X-Wx-Iv"]; !ok {
		result.Code = utils.PARAM_ERR
		result.Msg = utils.Msg(utils.PARAM_ERR)
		logs.Warning("Login Missing header parameter : X-Wx-Iv!")
		return
	}

	jsCode := headers["Js-Code"]                    //有可能没有值在
	encryptedData := headers["X-Wx-Encrypted-Data"] //解密用户资料用
	iv := headers["X-Wx-Iv"]                        //解密用户资料用
	token := uuid.Must(uuid.NewV4())
	//调取微信接口获取用户基础信息
	user, err := service.LoginService(jsCode[0], encryptedData[0], iv[0], token.String())
	if err != nil {
		result.Code = utils.LOGIN_ERR
		result.Msg = utils.Msg(utils.LOGIN_ERR)
		logs.Error("Login err :%v", user)
		return
	}
	result.Token = token.String()
	result.Data = &user
	logs.Info("login success ,json.Marshal(&result)= %v", result)
	return
}

func (loginApi *LoginApi) Logout() { //微信授权基础信息
	ip := loginApi.Ctx.Request.RemoteAddr
	headers := loginApi.Ctx.Request.Header

	result := utils.Result{Code: utils.OK, Msg: utils.Msg(utils.OK)}
	defer utils.Defer(&result, loginApi.Ctx)
	if token, ok := headers["Token"]; !ok {
		result.Code = utils.NOT_LOGIN
		result.Msg = utils.Msg(utils.NOT_LOGIN)
		logs.Warn(" ip:%v connection logout request token : [%v] is null ! ", ip, token)
		return
	}
	b := models.Exists(headers["Token"][0], true)
	if !b {
		result.Code = utils.NOT_LOGIN
		result.Msg = utils.Msg(utils.NOT_LOGIN)
		logs.Warn(" ip:%v connection logout request token : [%v] is not exist!! ", ip, headers["Token"][0])
		return
	}
	return
}
