package apis

import (
	"chargingpile/models"
	"chargingpile/utils"
	"fmt"
	"github.com/astaxie/beego"
)

type TestApi struct {
	beego.Controller
}

func (activity *TestApi) GetTest() {
	result := utils.Result{Code: utils.PARAM_ERR, Msg: utils.Msg(utils.PARAM_ERR)}
	defer utils.Defer(&result, activity.Ctx)

	dis := models.GetDiscountValue(1)
	gift := models.GetGiftValue(1)
	result.Code = utils.OK
	result.Msg = utils.Msg(utils.OK)
	result.Data = fmt.Sprintf("test %f:%d", dis, gift)
	return

}
