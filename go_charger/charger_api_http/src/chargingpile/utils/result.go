package utils

import (
	"crypto/md5"
	"encoding/hex"
	"encoding/json"
	"fmt"
	"github.com/astaxie/beego/context"
	"github.com/astaxie/beego/logs"
	"sort"
	"strconv"
	"strings"
)

const (
	OK                 = 0
	CHARGE_TIMEOUT     = 1     //充电桩响应超时
	CHARGE_PILE_UNUSED = 2     // 充电桩故障不可充电
	CHARGE_NOT_FREE    = 3     //无空闲桩可预约
	CHARGE_FAIL        = -1    //请求参数错误
	READY_TO_START     = 4000  //准备启动
	CHARGING           = 4001  //充电中
	LOGIN_ERR          = -4001 //登录失败
	RE_LOGIN           = -4002 //重复登录
	NOT_LOGIN          = -4003 //未登录
	TOKEN_ERR          = -4004 //token失效
	PARAM_ERR          = -4005 //请求参数异常
	AUTHO_ERR          = -4006 //获取手机号码失败
	NOT_REGIST         = -4007 //未注册,无手机号
	RE_REGIST          = -4008 //重复注册,已有手机号码了
	NOT_ENOUGH         = -4101 //余额不足
	NOT_BE_USED        = -4102 //被预约不可用
	USER_UNUSED        = -4103 //用户未使用充电桩
	UNKNOW_ERR         = -4201 //未知异常
	SERVER_ERR         = -4202 //服务器异常
	WX_API_ERR         = -4301 //调用第三方的
	WX_PAY_ERR         = -4302 //付款失败
)

var msg = map[int]string{
	OK:                 "请求成功",
	CHARGE_TIMEOUT:     "充电桩响应超时",
	CHARGE_PILE_UNUSED: "充电桩故障不可充电",
	CHARGE_NOT_FREE:    "无空闲桩可预约",
	CHARGE_FAIL:        "请求参数错误",
	READY_TO_START:     "准备启动",
	CHARGING:           "充电中",
	LOGIN_ERR:          "登录失败",
	RE_LOGIN:           "重复登录",
	NOT_LOGIN:          "未登录",
	TOKEN_ERR:          "token失效",
	PARAM_ERR:          "请求参数异常",
	AUTHO_ERR:          "获取手机号码失败",
	NOT_REGIST:         "未注册",
	RE_REGIST:          "重复注册",
	NOT_ENOUGH:         "余额不足",
	NOT_BE_USED:        "被预约不可用",
	USER_UNUSED:        "未使用充电桩",
	UNKNOW_ERR:         "未知异常",
	SERVER_ERR:         "服务器异常",
	WX_API_ERR:         "微信服务器异常",
	WX_PAY_ERR:         "付款失败",
}

type ReGistResult struct {
	Code  int         `json:"code"`
	Msg   string      `json:"msg"`
	Token string      `json:"token,omitempty"`
	Data  interface{} `json:"data,omitempty"`
}

type Result struct {
	Code int         `json:"code"`
	Msg  string      `json:"msg"`
	Data interface{} `json:"data,omitempty" `
}

//充电桩返回的数据
type RespResult struct {
	Code int    `json:"code"`
	Data string `json:"data,omitempty"`
}

func Msg(code int) string {
	str, ok := msg[code]
	if ok {
		return str
	}
	return msg[UNKNOW_ERR]
}

func Defer(result *Result, ctx *context.Context) {
	bytes, _ := json.Marshal(&result)
	ctx.ResponseWriter.Write(bytes)
}
func DeferRegist(result *ReGistResult, ctx *context.Context) {
	bytes, _ := json.Marshal(&result)
	ctx.ResponseWriter.Write(bytes)
}

//微信支付计算签名的函数
func WXPayCalcSign(mReq map[string]interface{}, key string) (sign string) {
	//key设置路径：微信商户平台(pay.weixin.qq.com)-->账户设置-->API安全-->密钥设置
	fmt.Println("微信支付签名计算, API KEY:", key)
	//STEP 1, 对key进行升序排序.
	sorted_keys := make([]string, 0)
	for k, _ := range mReq {
		sorted_keys = append(sorted_keys, k)
	}

	sort.Strings(sorted_keys)

	//STEP2, 对key=value的键值对用&连接起来，略过空值
	var signStrings string
	for _, k := range sorted_keys {
		fmt.Printf("k=%v, v=%v\n", k, mReq[k])
		value := fmt.Sprintf("%v", mReq[k])
		if value != "" {
			signStrings = signStrings + k + "=" + value + "&"
		}
	}

	//STEP3, 在键值对的最后加上key=API_KEY
	if key != "" {
		signStrings = signStrings + "key=" + key
	}

	//STEP4, 进行MD5签名并且将所有字符转为大写.
	md5Ctx := md5.New()
	md5Ctx.Write([]byte(signStrings))
	cipherStr := md5Ctx.Sum(nil)
	upperSign := strings.ToUpper(hex.EncodeToString(cipherStr))
	return upperSign
}

func TimeConversion(time int) string {
	timeStr := ""
	hour := 0
	minute := 0
	second := 0
	if time <= 0 {
		logs.Error("时间格式异常!")
		return ""
	} else {
		if time < 60 {
			second = time
			timeStr = strconv.Itoa(second) + "秒"
		} else {
			minute = time / 60
			if minute < 60 {
				second = time % 60
				timeStr = strconv.Itoa(minute) + "分" + strconv.Itoa(second) + "秒"
			} else {
				hour = minute / 60
				if hour > 24 {

				}
				minute = minute % 60
				second = time - hour*3600 - minute*60
				timeStr = strconv.Itoa(hour) + "小时" + strconv.Itoa(minute) + "分" + strconv.Itoa(second) + "秒"
			}
		}

	}
	return timeStr
}
