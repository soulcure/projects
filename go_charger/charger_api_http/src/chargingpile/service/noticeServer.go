package service

import (
	"chargingpile/models"
	"chargingpile/utils"
	"encoding/json"
	"errors"
	"github.com/astaxie/beego"
	"github.com/astaxie/beego/logs"
	"strconv"
	"time"
)

var templateSuccessId string
var templateErrorId string

func init() {
	templateSuccessId = beego.AppConfig.String("weixin::templateSuccessId")
	templateErrorId = beego.AppConfig.String("weixin::templateErrorId")
}

func NoticeService(advice *models.Advice) error {
	sendTemplate := models.SendTemplate{}
	sendTemplate.Touser = advice.Account
	stat := advice.State
	if 3 == stat {
		adviceData := models.QueryAdviceData(advice.StationCode, advice.Seq, advice.Account)
		if adviceData == nil {
			logs.Warn("NoticeService models QueryAdviceData advice.StationCode[%v], advice.Seq[%v], advice.Account[%v]) is nil", advice.StationCode, advice.Seq, advice.Account)
			return errors.New("NoticeService models QueryAdviceData advice.StationCode[%v], advice.Seq[%v], advice.Account[%v]) is nil")
		}
		sendTemplate.TemplateId = templateSuccessId
		noticeData := models.Keyword{}
		noticeData.Keyword1 = models.Value{adviceData.OrderNum}
		noticeData.Keyword2 = models.Value{adviceData.ChargingName}
		noticeData.Keyword3 = models.Value{adviceData.Code}
		noticeData.Keyword4 = models.Value{time.Unix(adviceData.Begin, 0).Format("2006-01-02 15:04:05")}
		noticeData.Keyword5 = models.Value{time.Unix(adviceData.End, 0).Format("2006-01-02 15:04:05")}
		noticeData.Keyword6 = models.Value{utils.TimeConversion(adviceData.Time)}
		noticeData.Keyword7 = models.Value{strconv.FormatFloat(float64(adviceData.Electric)/100, 'f', 2, 32) + "度"}
		noticeData.Keyword8 = models.Value{strconv.FormatFloat(adviceData.Money/100, 'f', -1, 64) + "元"}
		noticeData.Keyword9 = models.Value{strconv.FormatFloat(adviceData.AccountMoney/100, 'f', 2, 64) + "元"} //账户余额
		noticeData.Keyword10 = models.Value{advice.ErrorMsg.Msg}
		sendTemplate.Data = noticeData
	} else if 4 == stat {
		stationName := models.QueryStationName(advice.StationCode)
		if stationName == "" {
			logs.Error("警告:查询站点返回空串!")
		}
		sendTemplate.TemplateId = templateErrorId
		noticeData := models.Keyword{}
		noticeData.Keyword1 = models.Value{advice.Code}                                                           //充电站的站名
		noticeData.Keyword2 = models.Value{stationName}                                                           //充电桩的编码
		noticeData.Keyword3 = models.Value{time.Unix(advice.ErrorMsg.ErrorTime, 0).Format("2006-01-02 15:04:05")} //发生异常的时间
		noticeData.Keyword4 = models.Value{advice.ErrorMsg.Msg}                                                   //异常详情
		sendTemplate.Data = noticeData
	}
	//现在redis获取accessToken没有再去调用接口
	dataMap := models.GetAccessToken("accessToken")    //在redis获取accessToken和form_id
	sendTemplateResult := make(map[string]interface{}) //发送通知结果集
	if dataMap != nil {
		sendTemplate.FormId = dataMap["formId"]  //传进来
		data, err := json.Marshal(&sendTemplate) //转成json字节码
		if err != nil {
			logs.Error("NoticeService sendTemplate json Marshal data err:%v", err)
			return err
		}
		url2 := "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + dataMap["accessToken"]
		err = utils.SendTemplate(url2, data, sendTemplateResult) //发送通知
		if err != nil || "0" != strconv.FormatFloat(sendTemplateResult["errcode"].(float64), 'f', 0, 64) {
			logs.Error("First time: NoticeService sendTemplate error:%v ,errorMsg :%+v", err, sendTemplateResult)
			return err
		}
		logs.Info("First time: NoticeService sendTemplate success ! data:%+v", sendTemplate)
		return nil //没问题退出
	}
	url := "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appId + "&secret=" + secret
	tokenMap := make(map[string]interface{})
	err := utils.ClientWxApi(url, tokenMap) //第一次尝试获取accessToken
	if err != nil {
		return err
	}
	if _, ok := tokenMap["errcode"]; ok { //获取accesstoken
		for count := 0; -1 == tokenMap["errcode"]; count++ { //获取不到5秒后再次获取
			time.Sleep(time.Second * 5)
			utils.ClientWxApi(url, tokenMap)
			if count == 3 { //3次后停止获取
				logs.Error("NoticeService Unable to get accessToken ,WeChat server error ,resp error mag is WeChat Busy server")
				return errors.New("WeChat server error ,resp error mag is WeChat Busy server")
			}
		}
		logs.Error("警告: 请求获取accessToken失败,商家账户信息异常!")
		return errors.New("请求获取accessToken失败,商家账户信息异常!")
	}
	if _, ok := tokenMap["access_token"]; ok {
		accessToken := tokenMap["access_token"].(string)
		url3 := "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + accessToken
		formId := models.SetAndGet(tokenMap["access_token"].(string), 7, 7200)
		sendTemplate.FormId = formId
		data, err := json.Marshal(&sendTemplate)
		err = utils.SendTemplate(url3, data, sendTemplateResult) //发送通知
		if err != nil || "0" != strconv.FormatFloat(sendTemplateResult["errcode"].(float64), 'f', 0, 64) {
			logs.Error("Second time : NoticeService sendTemlpate error ,errorMsg :%+v", sendTemplateResult)
			return err
		}
		logs.Info("Second time NoticeService sendTemplate success ! data:%+v", sendTemplateResult)
		return nil
	} else {
		logs.Error("WeChat server resp error msg :%v", tokenMap)
		return errors.New("WeChat server resp error msg")
	}
	return nil
}
