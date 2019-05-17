package service

import (
	"chargingpile/utils"
	"encoding/xml"
	"errors"
	"fmt"
	"github.com/astaxie/beego"
	"github.com/astaxie/beego/logs"
	"io/ioutil"
	"math/rand"
	"net/http"
	"strconv"
	"strings"
	"time"
)

type WXPayResp struct {
	Return_code string `xml:"return_code"`
	Return_msg  string `xml:"return_msg"`
	Nonce_str   string `xml:"nonce_str"`
	Prepay_id   string `xml:"prepay_id"`
}

func ClinentWXPay(ip string, mapReq map[string]string) (err error) {
	unify_order_req := "https://api.mch.weixin.qq.com/pay/unifiedorder"
	appid := beego.AppConfig.String("weixin::appid")
	mch_id := beego.AppConfig.String("weixin::mch_id")
	wx_key := beego.AppConfig.String("weixin::wx_key")
	notify_url := beego.AppConfig.String("weixin::notify_url")

	rand.Seed(time.Now().Unix())
	nonceStr := time.Now().Format("20060102150405") + string(rand.Intn(10000))

	var reqMap = make(map[string]interface{}, 0)

	rand.Seed(time.Now().Unix())
	orderNo := "CDZ" + time.Now().Format("20060102150405") + string(rand.Intn(1000))
	total_fee, _ := strconv.Atoi(mapReq["total_fee"]) //接受到的金额是元

	reqMap["appid"] = appid               //微信小程序appid
	reqMap["body"] = mapReq["body"]       //商品描述
	reqMap["mch_id"] = mch_id             //商户号
	reqMap["nonce_str"] = nonceStr        //随机数
	reqMap["notify_url"] = notify_url     //回调的通知地址
	reqMap["openid"] = mapReq["openId"]   //用户标识 openid用户的
	reqMap["out_trade_no"] = orderNo      //订单号
	reqMap["spbill_create_ip"] = ip       //用户端ip   //订单生成的机器 IP
	reqMap["total_fee"] = total_fee * 100 //订单总金额，单位为分
	reqMap["trade_type"] = "JSAPI"        //trade_type=JSAPI时（即公众号支付），此参数必传，此参数为微信用户在商户对应appid下的唯一标识
	reqMap["sign"] = utils.WXPayCalcSign(reqMap, wx_key)

	reqBytes, err := xml.Marshal(reqMap)
	if err != nil {
		logs.Error("以xml形式编码发送错误, 原因: %v", err)
		return
	}

	reqStr := strings.Replace(string(reqBytes), "UnifyOrderReq", "xml", -1)

	//发送unified order请求.
	req, err := http.NewRequest("POST", unify_order_req, strings.NewReader(reqStr))
	if err != nil {
		logs.Error("New Http Request发生错误，原因: %v", err)
		return
	}
	//这里的http header的设置是必须设置的.
	req.Header.Set("Content-Type", "application/xml;charset=utf-8")

	client := http.Client{}
	resp, err := client.Do(req)
	defer resp.Body.Close()
	if err != nil {
		logs.Error("请求微信支付统一下单接口发送错误, 原因:%v", err)
		return
	}
	body, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		// handle error
		logs.Error("解析响应内容失败 :%v", err)
		return
	}
	fmt.Println("响应数据", string(body))

	var xmlResp WXPayResp
	err = xml.Unmarshal(body, &xmlResp)
	if err != nil {
		logs.Error("pay resp data  Unmarshal err:%v", err)
		return
	}
	if strings.ToUpper(xmlResp.Return_code) == "SUCCESS" {
		logs.Info("预支付申请成功")
		// 再次签名
		var resMap2 = make(map[string]interface{}, 0)
		resMap2["appId"] = appid
		resMap2["nonceStr"] = xmlResp.Nonce_str               //商品描述
		resMap2["package"] = "prepay_id=" + xmlResp.Prepay_id //商户号
		resMap2["signType"] = "MD5"                           //签名类型
		resMap2["timeStamp"] = time.Now().Unix()              //当前时间戳

		resMap2["paySign"] = utils.WXPayCalcSign(resMap2, wx_key)
		// 返回5个支付参数及sign 用户进行确认支付

		logs.Info("支付参数", resMap2)
		//TODO 未做数据库处理

		return
	} else {

		return errors.New("couldn't convert number")
	}

}
