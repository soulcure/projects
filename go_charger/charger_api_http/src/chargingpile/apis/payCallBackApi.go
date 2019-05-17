package apis

import (
	"chargingpile/models"
	"encoding/xml"
	"github.com/astaxie/beego"
	"github.com/astaxie/beego/logs"
	"net/http"
	"strings"
)

type PayCallBackApi struct {
	beego.Controller
}

type WXPayNotifyReq struct {
	ReturnCode    string `xml:"return_code"`
	ReturnMsg     string `xml:"return_msg"`
	AppId         string `xml:"appid"`
	MchId         string `xml:"mch_id"`
	NonceStr      string `xml:"nonce_str"`
	Sign          string `xml:"sign"`
	ResultCode    string `xml:"result_code"`
	Openid        string `xml:"openid"`
	IsSubscribe   string `xml:"is_subscribe"`
	TradeType     string `xml:"trade_type"`
	BankType      string `xml:"bank_type"`
	TotalFee      int    `xml:"total_fee"`
	FeeType       string `xml:"fee_type"`
	CashFee       int    `xml:"cash_fee"`
	CashFeeType   string `xml:"cash_fee_type"`
	TransactionId string `xml:"transaction_id"`
	OutTradeNo    string `xml:"out_trade_no"`
	Attach        string `xml:"attach"`
	TimeEnd       string `xml:"time_end"`
}

type WXPayNotifyRsp struct {
	ReturnCode string `xml:"return_code"`
	ReturnMsg  string `xml:"return_msg"`
}

func (callback *PayCallBackApi) PayCallBack() { //支付回调
	logs.Debug("支付回调接口被调用了-------------------!")

	key := beego.AppConfig.String("weixin::wx_key")

	rspWriter := callback.Ctx.ResponseWriter
	body := callback.Ctx.Input.RequestBody

	var mr WXPayNotifyReq
	if err := xml.Unmarshal(body, &mr); err != nil {
		logs.Error(err)
		return
	}
	logs.Debug("微信支付结果 PayCallBack：%v", mr)

	var reqMap map[string]interface{}
	reqMap = make(map[string]interface{}, 0)

	reqMap["return_code"] = mr.ReturnCode
	reqMap["return_msg"] = mr.ReturnMsg
	reqMap["appid"] = mr.AppId
	reqMap["mch_id"] = mr.MchId
	reqMap["nonce_str"] = mr.NonceStr
	reqMap["result_code"] = mr.ResultCode
	reqMap["openid"] = mr.Openid
	reqMap["is_subscribe"] = mr.IsSubscribe
	reqMap["trade_type"] = mr.TradeType
	reqMap["bank_type"] = mr.BankType
	reqMap["total_fee"] = mr.TotalFee
	reqMap["fee_type"] = mr.FeeType
	reqMap["cash_fee"] = mr.CashFee
	reqMap["cash_fee_type"] = mr.CashFeeType
	reqMap["transaction_id"] = mr.TransactionId
	reqMap["out_trade_no"] = mr.OutTradeNo
	reqMap["attach"] = mr.Attach
	reqMap["time_end"] = mr.TimeEnd

	var resp WXPayNotifyRsp
	//进行签名校验
	if wxPayVerifySign(reqMap, key, mr.Sign) {
		//这里就可以更新我们的后台数据库了，其他业务逻辑同理。
		resp.ReturnCode = "SUCCESS"
		resp.ReturnMsg = "OK"
		models.OrderUpdateToDB(mr.Openid, mr.OutTradeNo, mr.TotalFee, mr.TimeEnd, 1)
	} else {
		resp.ReturnCode = "FAIL"
		resp.ReturnMsg = "failed to verify sign, please retry!"
	}

	//结果返回，微信要求如果成功需要返回return_code "SUCCESS"
	bytes, _err := xml.Marshal(resp)
	strResp := strings.Replace(string(bytes), "WXPayNotifyResp", "xml", -1)
	if _err != nil {
		logs.Error("xml编码失败，原因：", _err)
		return
	}
	rspWriter.WriteHeader(http.StatusOK)
	if _, err := rspWriter.Write([]byte(strResp)); err != nil {
		logs.Error("xml编码失败，原因：", _err)
	}

	return
}

//微信支付签名验证函数
func wxPayVerifySign(needVerifyM map[string]interface{}, key string, sign string) bool {
	signCalc := models.WxPayCalcSign(needVerifyM, key)
	if sign == signCalc {
		logs.Debug("签名校验通过!", signCalc)
		return true
	}

	logs.Error("签名校验失败!", sign, signCalc)
	return false
}
