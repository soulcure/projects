package models

import (
	"bytes"
	"encoding/xml"
	"errors"
	"github.com/astaxie/beego"
	"github.com/astaxie/beego/logs"
	"io/ioutil"
	"net/http"
	"strings"
	"time"
)

type OrderQueryReq struct {
	AppId      string `xml:"appid"`
	MchId      string `xml:"mch_id"`
	NonceStr   string `xml:"nonce_str"`
	Sign       string `xml:"sign"`
	OutTradeNo string `xml:"out_trade_no"`
}

func OrderQueryByTimer(openId string, tradeNo string, totalFee int) {
	go func() {
		queryTimes := 0
		time.Sleep(5 * 1000)
		ticker := time.NewTicker(time.Second * 10)
		for t := range ticker.C {
			queryTimes++
			if queryTimes > 5 {
				ticker.Stop()
				break
			}
			logs.Debug("Tick at", t, queryTimes)

			var or OrderQueryReq
			if res, err := or.PayOrderQueryReq(openId, tradeNo, totalFee); err == nil {
				if res {
					ticker.Stop()
					break
				}
			}
		}
	}()

}

func (orderQueryReq *OrderQueryReq) PayOrderQueryReq(openId string, tradeNo string, totalFee int) (bool, error) {
	res := false

	reqUrl := "https://api.mch.weixin.qq.com/pay/orderquery"
	appId := beego.AppConfig.String("weixin::appid")
	mchId := beego.AppConfig.String("weixin::mch_id")
	wxKey := beego.AppConfig.String("weixin::wx_key")

	//请求UnifiedOrder的代码
	orderQueryReq.AppId = appId
	orderQueryReq.MchId = mchId
	orderQueryReq.OutTradeNo = tradeNo
	orderQueryReq.NonceStr = RandStr(32)

	var m = make(map[string]interface{}, 0)
	m["appid"] = orderQueryReq.AppId
	m["mch_id"] = orderQueryReq.MchId
	m["out_trade_no"] = orderQueryReq.OutTradeNo
	m["nonce_str"] = orderQueryReq.NonceStr

	orderQueryReq.Sign = WxPayCalcSign(m, wxKey) //这个是计算wxpay签名的函数上面已贴出

	bytesReq, err := xml.Marshal(orderQueryReq)
	if err != nil {
		logs.Debug("以xml形式编码发送错误, 原因:", err)
		return res, err
	}

	strReq := string(bytesReq)
	//wxpay的unifiedorder接口需要http body中xml doc的根节点是<xml></xml>这种，所以这里需要replace一下
	strReq = strings.Replace(strReq, "OrderQueryReq", "xml", -1)

	logs.Debug("xml string:", strReq)
	bytesReq = []byte(strReq)
	//发送unified order请求.
	req, err := http.NewRequest("POST", reqUrl, bytes.NewReader(bytesReq))
	if err != nil {
		logs.Error("wx OrderQueryReq request发生错误，原因:", err)
		return res, err
	}

	req.Header.Set("Accept", "application/xml")
	//这里的http header的设置是必须设置的.
	req.Header.Set("Content-Type", "application/xml;charset=utf-8")
	c := http.Client{}
	rsp, err := c.Do(req)
	if err != nil {
		logs.Error("请求查询微信支付结构接口发送错误, 原因:", err)
		return res, err
	}

	body, err := ioutil.ReadAll(rsp.Body)
	if err != nil {
		// handle error
		logs.Error("解析响应内容失败 :%v", err)
		return res, err
	}
	logs.Debug("响应数据XML:\n", string(body))

	var xmlResp OrderRsp
	err = xml.Unmarshal(body, &xmlResp)
	if err != nil {
		logs.Error("pay resp data  Unmarshal err:%v", err)
		return res, err
	}

	if strings.ToUpper(xmlResp.ReturnCode) == "SUCCESS" && strings.ToUpper(xmlResp.TradeState) == "SUCCESS" {
		logs.Info("支付结果查询为支付成功", tradeNo)
		OrderUpdateToDB(openId, tradeNo, totalFee, xmlResp.TimeEnd, 1)
		//这里已经得到微信支付的prepay id，需要返给客户端，由客户端继续完成支付流程
		res = true
		return res, err
	} else {
		logs.Error("支付结果查询为支付失败", tradeNo, xmlResp.ReturnMsg)
		return res, errors.New(xmlResp.ReturnMsg)
	}

}
