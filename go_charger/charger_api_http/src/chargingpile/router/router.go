package router

import (
	"chargingpile/apis"
	"chargingpile/models"
	"chargingpile/utils"
	"encoding/json"
	"github.com/astaxie/beego"
	"github.com/astaxie/beego/context"
	"github.com/astaxie/beego/logs"
)

func init() {
	logs.Info("router init run !")
	var Interceptor = func(ctx *context.Context) {
		headers := ctx.Request.Header
		result := utils.Result{Code: utils.NOT_LOGIN, Msg: utils.Msg(utils.NOT_LOGIN)}
		if _, ok := headers["Token"]; !ok {
			utils.Defer(&result, ctx)
			return
		}
		if ok := models.Exists(headers["Token"][0], false); !ok {
			utils.Defer(&result, ctx)
			return
		}
	}

	var Interceptor2 = func(ctx *context.Context) {
		headers := ctx.Request.Header
		/*ip := headers["x-forwarded-for"][0]
		if len(ip) == 0 {
			ip = headers["Proxy-Client-IP"][0]
		}
		if len(ip) == 0 {
			ip = headers["WL-Proxy-Client-IP"][0]
		}
		if len(ip) == 0 {
			ip = ctx.Request.RemoteAddr
		}
		logs.Info("ip :%v interview server!", ip)*/
		result := utils.Result{Code: utils.NOT_REGIST, Msg: utils.Msg(utils.NOT_REGIST)}
		if _, ok := headers["Token"]; !ok {
			utils.Defer(&result, ctx)
			return
		}
		bytes := models.Get(headers["Token"][0], 2)
		if bytes == nil {
			logs.Error("Interceptor2 redis can not get token :%v ,user is not login", bytes)
			utils.Defer(&result, ctx)
			return
		}
		cacheMap := make(map[string]string)
		json.Unmarshal(bytes, &cacheMap)
		if status, ok := cacheMap["status"]; ok {
			if "1" != status {
				logs.Error("Interceptor2 query user[%v] ,id[%v]  phone status[%v]", cacheMap["nickName"], cacheMap["openId"], status)
				utils.Defer(&result, ctx)
				return
			}
		}
		logs.Info("user[%v] ,id[%v]  phone status[%v]", cacheMap["nickName"], cacheMap["openId"], cacheMap["status"])
	}

	beego.Router("/api/login", &apis.LoginApi{}, "*:Login")                   //登陆1
	beego.Router("/pay/callback", &apis.PayCallBackApi{}, "POST:PayCallBack") //微信支付回调
	//beego.Router("/api/activity", &apis.ActivityApi{}, "*:GetActivity")  //for test
	//beego.Router("/api/test", &apis.TestApi{}, "*:GetTest")
	//beego.Router("/api/actDetail", &apis.ActivityApi{}, "*:ActDetail")     //获取活动详情

	//拦截是否登录
	beego.InsertFilter("/api/open/*", beego.BeforeRouter, Interceptor, false)   //过滤器
	beego.Router("/api/open/logout", &apis.LoginApi{}, "*:Logout")              //登出1
	beego.Router("/api/open/regist", &apis.RegistApi{}, "*:WXRegist")           //注册1
	beego.Router("/api/open/registPhone", &apis.RegistApi{}, "*:RegistPhone")   //绑定手机
	beego.Router("/api/open/getPosition", &apis.PlaceApi{}, "*:GetPosition")    //获取位置1
	beego.Router("/api/open/getActivity", &apis.ActivityApi{}, "*:GetActivity") //获取所有的活动列表
	beego.Router("/api/open/actDetail", &apis.ActivityApi{}, "*:ActDetail")     //获取活动详情

	//拦截是否已绑定手机号
	beego.InsertFilter("/api/inner/*", beego.BeforeRouter, Interceptor2, false)                    //过滤器
	beego.Router("/api/inner/scanCode", &apis.ScanCodeApi{}, "*:ScanCodeOrReserve")                //扫码或预约
	beego.Router("/api/inner/accountRecord", &apis.AccountRecordApi{}, "*:GetRecord")              //账户记录
	beego.Router("/api/inner/userChargeRecord", &apis.AccountRecordApi{}, "*:GetUserChargeRecord") //获取账单详情
	beego.Router("/api/inner/charging", &apis.ChargingStatusApi{}, "*:GetChargingStatus")          //查询充电状态

	beego.Router("/api/inner/charger", &apis.ChargerApi{}, "POST:Recharge") //充值
	beego.Router("/api/inner/balance", &apis.BalanceApi{}, "POST:Balance")  //查询余额

	beego.Router("/charge/notice", &apis.NoticeApi{}, "*:CallBackNotice") //充电桩回调通知

}
