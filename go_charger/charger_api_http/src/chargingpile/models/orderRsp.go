package models

type OrderRsp struct {
	ReturnCode string `xml:"return_code"`
	ReturnMsg  string `xml:"return_msg"`
	AppId      string `xml:"appid"`
	MchId      string `xml:"mch_id"`
	NonceStr   string `xml:"nonce_str"`
	OpenId     string `xml:"openid"`
	Sign       string `xml:"sign"`
	ResultCode string `xml:"result_code"`
	PrepayId   string `xml:"prepay_id"`
	TradeType  string `xml:"trade_type"`
	TradeState string `xml:"trade_state"`
	OutTradeNo string `xml:"out_trade_no"`
	TimeEnd    string `xml:"time_end"`
	TotalFee   int    `xml:"total_fee"`
}
