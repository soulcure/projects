package charge

type Header struct {
	Flag    [2]byte
	Len     uint16
	Version uint8
	Seq     uint8
	Cmd     uint16
}

const HEADLEN uint16 = 8
const FLAG0 uint8 = 0xAA
const FLAG1 uint8 = 0XF5

type CommandId uint16

const (
	S_INTEGER_PARAMETER_REQ CommandId = 1
	C_INTEGER_PARAMETER_RSP CommandId = 2
	S_CHAR_PARAMETER_REQ    CommandId = 3
	C_CHAR_PARAMETER_RSP    CommandId = 4
	S_CONTROL_CMD_REQ       CommandId = 5
	C_CONTROL_CMD_RSP       CommandId = 6
	S_CHARGE_START_REQ      CommandId = 7
	C_CHARGE_START_RSP      CommandId = 8
)

//cmd:1~10
type IntegerParameterReq struct {
	Payload1 uint16
	Payload2 uint16
	Type     uint8
	Addr     uint32
	Num      uint8
	Size     uint16
	//Data        []byte
}

type IntegerParameterRsp struct {
	Payload1 uint16
	Payload2 uint16
	Code     [32]byte
	Type     uint8
	Addr     uint32
	Num      uint8
	Res      uint8
	//	Data         []byte
}

type CharParameterReq struct {
	Payload1 uint16
	Payload2 uint16
	Type     uint8
	Addr     uint32
	Size     uint16
	//Data        []byte
}

type CharParameterRsp struct {
	Payload1 uint16
	Payload2 uint16
	Code     [32]byte
	Type     uint8
	Addr     uint32
	Res      uint8
	//Data         []byte
}

type ControlCmdReq struct {
	Payload1 uint16
	Payload2 uint16
	PointNum uint8
	Addr     uint32
	Num      uint8
	Size     uint16
	//Data     []byte
}

type ControlCmdRsp struct {
	Payload1 uint16
	Payload2 uint16
	Code     [32]byte
	PointNum uint8
	Addr     uint32
	Num      uint8
	Res      uint8
}

type ChargeStartReq struct {
	Payload1        uint16
	Payload2        uint16
	PointNum        uint8
	Type            uint32
	Password        uint32
	Strategy        uint32  //充电策略
	Parameter       uint32  // 充电策略参数
	Timer           [8]byte //预约/定时启动时间
	Timeout         uint8   //预约超时时间
	Account         [32]byte
	Flag            uint8  //断网充电标志
	OfflineElectric uint32 //离线可充电电量
	ChargeSeq       [32]byte
}

type ChargeStartRsp struct {
	Payload1 uint16
	Payload2 uint16
	Code     [32]byte
	GunNum   uint8
	Res      uint32
}

//charge upload data
const (
	S_HEATBEAT_RSP          CommandId = 101
	C_HEATBEAT_REQ          CommandId = 102
	S_CHARGE_STATE_INFO_RSP CommandId = 103
	C_CHARGE_STATE_INFO_REQ CommandId = 104
	S_SIGN_IN_RSP           CommandId = 105
	C_SIGN_IN_REQ           CommandId = 106
	S_ALARM_RSP             CommandId = 107
	C_ALARM_REQ             CommandId = 108
	S_STARTFINISH_RSP       CommandId = 109
	C_STARTFINISH_REQ       CommandId = 110
)

type HeatBeatRsp struct {
	Payload1 uint16
	Payload2 uint16
	Seq      uint16
}

type HeatBeatReq struct {
	Payload1 uint16
	Payload2 uint16
	Code     [32]byte
	Seq      uint16
}

type StateInfoRsp struct {
	Payload1     uint16
	Payload2     uint16
	Code         [32]byte
	GunNum       uint8
	PointNum     uint8
	Type         uint8
	State        uint8
	SOC          uint8
	AlarmCode    uint32
	CarConnState uint8
}

type StateInfoReq struct {
	Payload1     uint16
	Payload2     uint16
	Code         [32]byte
	Num          uint8
	PointNum     uint8
	Type         uint8
	State        uint8
	SOC          uint8
	AlarmCode    uint32
	CarConnState uint8
	Expense      uint32 //充电费用
	Variable1    uint32
	Variable2    uint32
	DCVoltage    uint16 //直流充电电压
	DCCurrent    uint16 //直流充电电流
	BMSVoltage   uint16
	BMSCurrent   uint16
	BMSModel     uint8

	ACVoltageA uint16 //交流 A 相充电电压
	ACVoltageB uint16 //交流 B 相充电电压
	ACVoltageC uint16 //交流 C 相充电电压
	ACCurrentA uint16
	ACCurrentB uint16
	ACCurrentC uint16

	RemainTime uint16 //min
	ChargeTime uint32 // sec充电时长

	ElectricQuantity uint32 //充电电量

	BAmmeter uint32 //充电前电表读数
	CAmmeter uint32 //当前电表读数
	StartWay uint8

	ChargeStrategy uint8

	StragtegyParameter uint32
	Flag               uint8 //预约标志
	Account            [32]byte
	Timeout            uint8 //min
	ChargeBeginTime    [8]byte
	BCardBalance       uint32 //充电前卡余额
	Payload3           uint32
	Power              uint32 //充电功率
	Variable3          uint32
	Variable4          uint32
	Variable5          uint32
	/*	AirOutTemperature     uint8  //出风口温度
		EnvTemperature        uint8
		ChargeTemperature     uint8*/
}

type SignInRsp struct {
	Payload1           uint16
	Payload2           uint16
	Ramdom             uint32
	LoginCertification uint8
	EncryptFlag        uint8 //客户端是否启动rsa加密
	RSAModules         [128]byte
	Exponent           uint32
}

type SignInReq struct {
	Payload1       uint16
	Payload2       uint16
	Code           [32]byte
	Flag           uint8 //客户端是否支持加密
	CVersion       uint32
	Type           uint16
	StartNum       uint32
	Pattern        uint8
	SingInInterval uint16
	RunVariable    uint8
	GunNum         uint8
	HBInterval     uint8
	HBTimeoutNum   uint8
	ChargeNum      uint32
	Time           [8]byte
	Payload3       uint64
	Payload4       uint64
	Payload5       uint64
	//	Random         uint32
	//	SVersion       uint16
}

const (
	S_CHARGE_RECORD_RSP         CommandId = 201
	C_CHARGE_RECORD_REQ         CommandId = 202
	S_ACCOUNT_INFO_RSP          CommandId = 203
	C_ACCOUNT_INFO_REQ          CommandId = 204
	S_ACCOUNT_CERTIFICATION_RSP CommandId = 205
	C_ACCOUNT_CERTIFICATION_REQ CommandId = 206
)

type ChargeRecordRsp struct {
	Payload1 uint16
	PointNum uint8
	Account  [32]byte
}

type ChargeRecordReq struct {
	Payload1        uint16
	Payload2        uint16
	Code            [32]byte
	Type            uint8
	PointNum        uint8
	Account         [32]byte //账户
	ChargeBeginTime [8]byte
	ChargetEndTime  [8]byte
	ChargeTime      uint32
	SOCBegin        uint8
	SOCEnd          uint8
	Reason          uint32

	ElectricQuantity uint32
	BAmmeter         uint32 //充电前电表读数
	AAmmeter         uint32 //充电前电表读数
	Money            uint32 //本次充电金额
	Payload3         uint32

	BCardBalance   uint32 //充电前卡余额
	Index          uint32
	ChargeNum      uint32
	Payload4       uint8
	ChargeStrategy uint8

	StragtegyParameter uint32
	CarVIN             [17]byte
	CarId              [8]byte

	//时间段充电电量
	TimeElectricQuantity [96]byte
	StartWay             uint8 //启动方式
	//	Seq                  [32]byte
}

type AccountInfoRsp struct {
	Payload1         uint16
	Payload2         uint16
	ResCode          uint8
	Balance          uint32
	Payload3         uint16
	Payload4         uint16
	PwCertification  uint8
	VINCertification uint8
	Payload5         uint8
	Payload6         uint8
}

type AccountInfoReq struct {
	Payload1   uint16
	Payload2   uint16
	Code       [32]byte
	CardNumber [32]byte
	Balance    uint32
	IsBlack    uint8
	Password   [32]byte
	CardRandom [48]byte
	M1Seq      uint32
}

type AccountCertificationRsp struct {
	Payload1         uint16
	Payload2         uint16
	ResCode          uint8
	Balance          uint32
	Payload3         uint16
	Payload4         uint16
	PwCertification  uint8
	VINCertification uint8
	Payload5         uint8
	Payload6         uint8
}

type AccountCertificationReq struct {
	Payload1   uint16
	Payload2   uint16
	Code       [32]byte
	CardNumber [32]byte
	Balance    uint32
	IsBlack    uint8
	Password   [32]byte
	CardRandom [48]byte
	M1Seq      uint32
}

//直流
const (
	S_BMS_REPORT_RSP_1 CommandId = 301
	C_BMS_REPORT_REQ_1 CommandId = 302
	S_BMS_REPORT_RSP_2 CommandId = 303
	C_BMS_REPORT_REQ_2 CommandId = 304
)

type BMSReportRsp struct {
	Payload1 uint16
	Payload2 uint16
}
type BMSReportReq1 struct {
	Payload1     uint16
	Payload2     uint16
	PktSeq       uint16
	PointNum     uint16
	Code         [32]byte
	State        uint8
	CarConnState uint8
	ProtoVersion [3]byte
	//BRM info
	BatteryType    uint8
	NominalCurrent uint32 //额定电流
	NominalVoltage uint32 //额定电压
	Manufacturer   uint32
	BatterySeq     uint32
	BatteryDate    [4]byte
	ChargeNum      uint32
	Right          uint8 //产权标识
	Payload3       uint8
	CarVin         [17]byte
	SoftVersion    [8]byte

	//bcp info
	BCPNominalVoltage uint32
	BCPNominalCurrent uint32
	BCPCapacity       uint32
	ChargeMaxVoltage  uint32
	BCPMaxTemperature uint8
	BCPState          uint16
	BCPCurrentVoltage uint32
	ChargePrepare     uint8

	BCLVoltage uint32
	BCLCurrent uint32
	BCLModel   uint8

	BCSVoltage         uint32
	BCSCurrent         uint32
	BCSMaxVoltage      uint32
	BCSNum             uint8
	BCSoc              uint16
	EstimateChargeTime uint32 //预估剩余充电时长
	//BSM
	BSMNum               uint8
	BSMMaxTemperature    uint8
	BSMMaxTemperatureNum uint8
	BSMMinTemperature    uint8
	BSMMinTemperatureNum uint8
	BSMVoltageState      uint8
	BSMSocState          uint8
	BSMCurrentState      uint8
	BSMTemperatureState  uint8
	BSMInsulationState   uint8
	BSMConnState         uint8
	BSMChargeState       uint8

	//MST
	BSTBMSocState             uint8
	BSTBMSVoltageState        uint8
	BSTVoltageState           uint8
	BSTChargeStopState        uint8
	BSTInsulationState        uint8
	BSTOutputTemperatureState uint8 //BST-输出连接器过温故 障
	BSTBMSTemperatureState    uint8
	BSTChargeState            uint8
	BSTTemperatureState       uint8
	BSTRelayState             uint8
	BSTCheckVoltageState      uint8
	BSTOtherTroubleState      uint8
	BSTCurrentState           uint8
	BSTVoltageExpectState     uint8

	//BSD
	BSDSoc            uint16
	BSDMinVoltage     uint32
	BSDMaxVoltage     uint32
	BSDMinTemperature uint8
	BSDMaxTemperature uint8

	//BEM
	BEMSPN2560TimeoutState1         uint8
	BEMSPN2560TimeoutState2         uint8
	BEMTimeSyncTimeoutState2        uint8
	BEMChargeTimeoutState2          uint8
	BEMChargeStateTimeoutState2     uint8
	BEMChargeStopTimeoutState2      uint8
	BEMChargeStatisticTimeoutState2 uint8
	BEMOther                        uint8
}

type BMSReportReq2 struct {
	Payload1       uint16
	Payload2       uint16
	Code           [32]byte
	State          uint8
	CarConnState   uint8
	BRMCarMsg      [64]byte
	VBIMsg         [64]byte
	BCPParameter   [16]byte
	BROState       [8]byte
	BCLRequirement [8]byte
	BCSState       [16]byte
	BSMState       [8]byte
	BSTStopCharge  [8]byte
	BSDStatistic   [8]byte
	BEMMsg         [8]byte
}

const (
	S_QUERY_CHARGE_RECORD  CommandId = 401
	C_REPORT_CHARGE_RECORD CommandId = 402
)

//upgrade
const (
	S_DELETE_REQ           CommandId = 1001
	C_DELETE_RSP           CommandId = 1002
	S_UPGRADE_FILENAME_REQ CommandId = 1003
	C_UPGRADE_FILENAME_RSP CommandId = 1004
	S_UPGRADE_FILESIZE_REQ CommandId = 1005
	C_UPGRADE_FILESIZE_RSP CommandId = 1006
	S_UPGRADE_FILEDATA_REQ CommandId = 1007
	C_UPGRADE_FILEDATA_RSP CommandId = 1008
	S_UPGRADE_END_REQ      CommandId = 1009
	C_UPGRADE_END_RSP      CommandId = 1010
	S_RESTART_REQ          CommandId = 1011
	C_RESTART_RSP          CommandId = 1012
)

//bill
const (
	S_BILLING_QUERY_REQ CommandId = 1101
	C_BILLING_QUERY_RSP CommandId = 1102
	S_BILLING_SET_REQ   CommandId = 1103
	S_BILLING_SET_RSP   CommandId = 1104
)

type Rate struct {
	StartHour uint8  `json:"starthour"`
	StartMin  uint8  `json:"startmin"`
	EndHour   uint8  `json:"endhour"`
	EndMin    uint8  `json:"endmin"`
	Rating    uint32 `json:"rate"`
}
type BillingQueryReq struct {
}
type BillingQueryRsp struct {
	Rating [12]Rate
}
type BillingSetReq struct {
	Rating [12]Rate
}

type BillingSetRsp struct {
	Res uint8
}

//safity
const (
	S_CHARGE_CERTIFICATION_RSP CommandId = 1201
	C_CHARGE_CERTIFICATION_REQ CommandId = 1202
)

type ChargeCertificationRsp struct {
	Payload1 uint16
	Payload2 uint16
	AESFlag  uint32
	AES      [24]byte
}

type ChargeCertificationReq struct {
	Payload1 uint16
	Payload2 uint16
	Password [16]byte
	Ramdom   uint32
	AES      [24]byte
}

type handlerTable struct {
	cmd     CommandId
	handler func(c *ChargeClient, cmd CommandId, header []byte, body []byte) error
	name    string
}

var handlers = []handlerTable{
	//cmd:1~10
	{C_INTEGER_PARAMETER_RSP, integerParameterRsp, "integerParameterRsp"},
	{C_CHAR_PARAMETER_RSP, charParameterRsp, "charParameterRsp"},
	{C_CONTROL_CMD_RSP, controlCmdRsp, "controlCmdRsp"},
	{C_CHARGE_START_RSP, chargeStartRsp, "chargeStartRsp"},

	//cmd:101~110
	{C_HEATBEAT_REQ, heatBeatReq, "heatBeatReq"},
	{C_CHARGE_STATE_INFO_REQ, stateInfoReq, "stateInfoReq"},
	{C_SIGN_IN_REQ, signInReq, "signInReq"},

	//cmd:201~206
	{C_CHARGE_RECORD_REQ, chargeRecordReq, "chargeRecordReq"},
	//	{C_REPORT_CHARGE_RECORD, chargeRecordReq, "chargeRecordReportReq"},
	{C_ACCOUNT_INFO_REQ, accountInfoReq, "accountInfoReq"},
	{C_ACCOUNT_CERTIFICATION_REQ, accountCertificationReq, "accountCertificationReq"},

	//cmd:301~304
	{C_BMS_REPORT_REQ_1, bmsReportReq1, "bmsReportReq1"},
	{C_BMS_REPORT_REQ_2, bmsReportReq2, "bmsReportReq2"},

	//cmd:1101~1103
	{C_BILLING_QUERY_RSP, billingQueryRsp, "billingQueryRsp"},
	{S_BILLING_SET_RSP, billingSetRsp, "billSetRsp"},
	{C_CHARGE_CERTIFICATION_REQ, chargeCertificationReq, "chargeCertificationReq"},
}
