package charge

import (
	"logs"
)

type   ChargeStartFail struct{
	Code    uint32
	Reason  string
}
var chargeStartFails=[]ChargeStartFail{
	{100001 ,"充电机系统故障"},
	{100002,"车辆准备就绪超时"},
	{100003,"桩正在充电中， 不能再启动"},
	{100004,"本地模式，不能启动充电"},
	{100005,"枪口号不对"},
}
func Error(code uint32)string{
	for i,_:=range chargeStartFails {
		if chargeStartFails[i].Code==code{
			return chargeStartFails[i].Reason
		}
	}
	return "nil"
}

type ChargeFinishReason struct{
	Code   uint32
	Reason  string
}

var chargeFinishReasons=[]ChargeFinishReason{
	{0,"正常结束"},
	{1,"接收 BMS 的辨识报文超时"},
	{2,"接收电池充电参数报文超时"},
	{3,"接收 BMS 完成充电准备报文超时"},
	{4,"接收电池充总状态报文超时"},
	{5,"接收电池充电需求报文超时"},
	{6,"接收 BMS 中止充电报文超时"},
	{7,"接收 BMS 充电统计报文超时"},

	{20,"收到 bem 报文停止"},
	{21,"收到 bst 报文停止"},
	{22,"收到 BSM 报文错误停止"},
	{23,"bms 电池达到设定温度"},
	{24,"单体电压过高异常"},
	{25,"需求电流异常"},

	{30,"BST 的 SOC 目标值"},
	{31,"BST 的电压设定值"},
	{32,"BST 单体电压满"},
	{33,"BST_00_6"},
	{34,"BST 绝缘故障"},
	{35,"BST 连接器过温"},
	{36,"BST 元件过温 码不匹配"},
	{37,"BST 连接器故障"},
	{38,"BST 电池组过温"},
	{39,"BST 其他故障"},
	{40,"BST_02_4"},
	{41,"BST_02_6"},
	{42,"BST 电流过大"},
	{43,"BST 电压异常"},

	{200,"用户中止"},
	{201,"系统告警 1"},
	{202,"系统告警 2"},

	{300,"CC1 连接断开"},
	{301,"用户刷卡停止"},
	{302,"紧急停机"},
	{303,"预处理加电失败"},
	{304,"绝缘检测异常"},
	{305,"控制板通讯异常"},
	{306,"充电电量达到设定值"},
	{307,"充电时间达到设定值"},
	{308,"充电金额达到设定值"},
	{309,"电表通讯异常"},
	{310,"充电金额超过用户卡余额"},
	{311,"后台终止"},
	{312,"系统告警中止"},
	{313,"后台通讯中止"},
	{314,"充电电压异常中止"},
	{315,"充电电流异常中止"},
	{316,"BMS 的 SOC 满中止"},
	{317,"电表电量异常增大中止"},
	{318,"电表电量异常变小中止"},
	{319,"VIN 码不匹配"},

	{401,"管理员界面中止"},
	{402,"软件升级"},
	{403,"充电启动超时"},
	{404,"BMS 单体动力蓄电池电压异常"},
	{405,"BMS 整车动力蓄电池荷电状态"},
	{406,"BMS 动力蓄电池充电过电流"},
	{407,"BMS 动力蓄电池温度过高"},
	{408,"BMS 动力蓄电池绝缘状态"},
	{409,"BMS 蓄电池组输出连接器状态"},
	{410,"充电电压超过 BMS 最大允许值"},
	{411,"BMS 其他位状态异常"},
	{412,"BCS 上传电压异常"},

	{2000,"系统其他故障"},
	{2001,"紧急停机故障"},
	{2002,"绝缘故障"},
	{2003,"直流过压"},
	{2004,"直流欠压"},
	{2005,"软启失败"},
	{2006,"输出反接故障"},
	{2007,"接触器异常"},
	{2008,"模块故障"},
	{2009,"电网电压高"},
	{2010,"电网电压低"},
	{2011,"电网频率高"},
	{2012,"电网频率低"},
	{2013,"模块通信异常"},
	{2014,"模块类型不一致"},
	{2015,"系统辅源掉电"},
	{2016,"直流输出断路"},
	{2017,"进风口过温保护"},
	{2018,"进风口低温保护"},
	{2019,"出风口过温保护"},
	{2020,"群充模块过温"},
	{2021,"防雷故障"},
	{2022,"交流接触器异常"},


	{100001,"迪文通信告警"},
	{100002,"读卡器通信告警"},
	{100003,"防雷器故障"},
	{100004,"主开关及熔断器故障"},
	{100005,"紧急停机故障"},
	{100032,"电表 1 通信异常"},
	{100033,"电表 2 通信异常"},
	{101000,"断开连接"},

	{101001,"未准备就绪"},
	{101002,"充电过压"},
	{101003,"充电过流"},
	{101004,"充电欠压"},
	{101005,"用户刷卡停止充电"},
	{101006,"后台停止"},
	{101007,"充电时间达到设定值"},
	{101008,"充电金额达到设定值"},
	{101009,"充电电量达到设定值"},
	{101010,"应用软件升级停止"},
	{101011,"系统掉电停止"},
	{101012,"未知原因"},
	{101013,"用户界面中止"},
	{101014,"金额不足"},
	{101015,"电池充满"},
	{101016,"达到用户设定充电条件停止"},
}

func GetChargeFinishReason(code uint32)string{
	logs.Info("get reason")
	for i,_:=range chargeFinishReasons{
		if code==chargeFinishReasons[i].Code{
			return chargeFinishReasons[i].Reason
		}
	}
	return "未知异常错误码"
}

func GetChargeFinishTip(code uint32)string{
	switch code{
	case 0,101007,101008,101009:
		return "充电正常结束"
	case 101014:
		return "金额不足"
	case 101015:
		return "电池已充满"
	default:
		return "充电异常"
	}
}