package service

import (
	"chargingpile/models"
	"chargingpile/utils"
	"github.com/astaxie/beego/logs"
)

func ScanCodeService(scanBean *models.ScanBean) *utils.Result {
	userId := scanBean.UserId //用户id
	result := utils.Result{Code: utils.OK, Msg: utils.Msg(utils.OK)}
	user, err := models.Query(userId)
	if err != nil {
		return nil
	}
	if user.Money <= 0 {
		result.Code = utils.NOT_ENOUGH
		result.Msg = utils.Msg(utils.NOT_ENOUGH)
		logs.Warn("user:%+v account is not money !", userId)
		return &result
	}
	if len(scanBean.ScanSubmitId) == 0 { //不能影响正常的业务逻辑
		logs.Warn("扫码传参中无法获取ScanSubmitId ,往后将无法发送微信通知!")
	} else {
		models.LPush("form_id", scanBean.ScanSubmitId, 7)
	}

	scanBean.Money = user.Money
	respResult := utils.HttpPileApi(scanBean)
	if respResult.Code != 0 && respResult != nil {
		result.Code = respResult.Code
		result.Msg = utils.Msg(respResult.Code)
		return &result
	} else if respResult == nil {
		result.Code = utils.SERVER_ERR
		result.Msg = utils.Msg(utils.SERVER_ERR)
		return &result
	}
	logs.Info("user :%+v ScanCodeService success ! ", user)
	return &result
}
