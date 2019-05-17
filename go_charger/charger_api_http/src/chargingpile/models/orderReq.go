package models

import (
	"bytes"
	"encoding/xml"
	"errors"
	"fmt"
	"github.com/astaxie/beego"
	"github.com/astaxie/beego/logs"
	"io/ioutil"
	"net/http"
	"strings"
	"time"
)

type OrderReq struct {
	AppId          string `xml:"appid"`
	MchId          string `xml:"mch_id"`
	NonceStr       string `xml:"nonce_str"`
	Sign           string `xml:"sign"`
	Body           string `xml:"body"`
	OutTradeNo     string `xml:"out_trade_no"`
	TotalFee       int    `xml:"total_fee"`
	SpBillCreateIp string `xml:"spbill_create_ip"`
	NotifyUrl      string `xml:"notify_url"`
	TradeType      string `xml:"trade_type"`
	OpenId         string `xml:"openid"`
}

func (orderReq *OrderReq) PayOrderReq(tradeNo string, totalFee int, ip string, openId string) (*ClientPay, error) {
	var clientPay ClientPay

	reqUrl := "https://api.mch.weixin.qq.com/pay/unifiedorder"
	appId := beego.AppConfig.String("weixin::appid")
	mchId := beego.AppConfig.String("weixin::mch_id")
	notifyUrl := beego.AppConfig.String("weixin::notify_url")
	wxKey := beego.AppConfig.String("weixin::wx_key")

	//请求UnifiedOrder的代码
	orderReq.AppId = appId
	orderReq.MchId = mchId
	orderReq.NonceStr = RandStr(32)
	orderReq.Body = "充电桩服务费"

	orderReq.OutTradeNo = tradeNo
	orderReq.TotalFee = totalFee //单位是分
	orderReq.SpBillCreateIp = ip
	orderReq.NotifyUrl = notifyUrl
	orderReq.TradeType = "JSAPI"
	orderReq.OpenId = openId

	logs.Debug("PayOrderReq OutTradeNo:%s TotalFee:%d NonceStr:%s", tradeNo, totalFee, orderReq.NonceStr)

	var m = make(map[string]interface{}, 0)
	m["appid"] = orderReq.AppId
	m["mch_id"] = orderReq.MchId
	m["nonce_str"] = orderReq.NonceStr
	m["body"] = orderReq.Body
	m["out_trade_no"] = orderReq.OutTradeNo
	m["total_fee"] = orderReq.TotalFee
	m["spbill_create_ip"] = orderReq.SpBillCreateIp
	m["notify_url"] = orderReq.NotifyUrl
	m["trade_type"] = orderReq.TradeType
	m["openid"] = orderReq.OpenId

	orderReq.Sign = WxPayCalcSign(m, wxKey) //这个是计算wxpay签名的函数上面已贴出

	bytesReq, err := xml.Marshal(orderReq)
	if err != nil {
		logs.Debug("以xml形式编码发送错误, 原因:", err)
		return nil, err
	}

	strReq := string(bytesReq)
	//wxpay的unifiedorder接口需要http body中xml doc的根节点是<xml></xml>这种，所以这里需要replace一下
	strReq = strings.Replace(strReq, "OrderReq", "xml", -1)

	logs.Debug("xml string:", strReq)
	bytesReq = []byte(strReq)
	//发送unified order请求.
	req, err := http.NewRequest("POST", reqUrl, bytes.NewReader(bytesReq))
	if err != nil {
		logs.Error("wx pay request发生错误，原因:", err)
		return nil, err
	}

	req.Header.Set("Accept", "application/xml")
	//这里的http header的设置是必须设置的.
	req.Header.Set("Content-Type", "application/xml;charset=utf-8")
	c := http.Client{}
	rsp, err := c.Do(req)
	if err != nil {
		logs.Error("请求微信支付统一下单接口发送错误, 原因:", err)
		return nil, err
	}

	body, err := ioutil.ReadAll(rsp.Body)
	if err != nil {
		// handle error
		logs.Error("解析响应内容失败 :%v", err)
		return nil, err
	}
	logs.Debug("响应数据XML:\n", string(body))

	var xmlResp OrderRsp
	err = xml.Unmarshal(body, &xmlResp)
	if err != nil {
		logs.Error("pay resp data  Unmarshal err:%v", err)
		return nil, err
	}

	if strings.ToUpper(xmlResp.ReturnCode) == "SUCCESS" {
		logs.Info("预支付申请成功")

		appId := xmlResp.AppId                      //小程序ID
		timeStamp := time.Now().Unix()              //当前时间戳
		nonceStr := xmlResp.NonceStr                //随机串
		prepayId := "prepay_id=" + xmlResp.PrepayId //商户号  //prepay_id=wx2017033010242291fcfe0db70013231072
		signType := "MD5"                           //签名方式

		// 再次签名
		var resMap = make(map[string]interface{}, 0)
		resMap["appId"] = appId         //小程序ID
		resMap["timeStamp"] = timeStamp //当前时间戳
		resMap["nonceStr"] = nonceStr   //随机串
		resMap["package"] = prepayId    //商户号  //prepay_id=wx2017033010242291fcfe0db70013231072
		resMap["signType"] = signType   //签名方式

		paySign := WxPayCalcSign(resMap, wxKey) //这个是计算wxpay签名的函数上面已贴出
		resMap["paySign"] = paySign

		// 返回5个支付参数及sign 用户进行确认支付
		clientPay.TimeStamp = fmt.Sprintf("%d", timeStamp)
		clientPay.NonceStr = nonceStr
		clientPay.Package = prepayId
		clientPay.SignType = signType
		clientPay.PaySign = paySign

		OrderInsertToDB(openId, tradeNo, totalFee, time.Now().Format("20060102150405"), 0)
		//OrderQueryByTimer(openId, tradeNo, totalFee)  //去掉主动查询支付结果
		//这里已经得到微信支付的prepay id，需要返给客户端，由客户端继续完成支付流程
		return &clientPay, err
	} else {
		logs.Error("预支付申请失败", xmlResp.ReturnMsg)
		return nil, errors.New(xmlResp.ReturnMsg)
	}

}

func OrderInsertToDB(userId string, tradeNo string, money int, orderTime string, status uint8) {
	var order Order
	order.UserId = userId
	order.OrderId = tradeNo
	order.Money = money
	order.OrderTime = orderTime
	order.Status = status
	if err := order.InsertOrder(); err != nil {
		logs.Error("save order to db error", err)
	}
}

func OrderUpdateToDB(userId string, tradeNo string, money int, orderTime string, status uint8) {
	var order Order

	offer := GetGiftValue(money)

	logs.Debug("save c_consumption to db money:", money)
	logs.Debug("save c_consumption to db offer:", offer)

	order.UserId = userId
	order.OrderId = tradeNo
	order.Money = money + offer
	order.OrderTime = orderTime
	order.Status = status
	order.Offer = offer

	if err := order.UpdateOrderTX(); err != nil {
		logs.Error("save c_consumption to db error", err)
	}

}
