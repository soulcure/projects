package models

import "database/sql"

//用户的基础信息
type User struct {
	UserId      string `db:"user_id" json:"userId"`
	CountryCode string `db:"country_code" json:"countryCode,omitempty" `
	Phone       string `db:"phone" json:"phone,omitempty"`             //电话号码
	NickName    string `db:"nick_name" json:"nickName,omitempty"`      //别名
	Gender      int    `db:"gender" json:"gender,omitempty"`           //性别
	City        string `db:"city" json:"city,omitempty" `              //所属地区
	CreateTime  int64  `db:"create_time" son:"createTime,omitempty" `  //创建时间
	Status      int    `db:"status" json:"status,omitempty" `          //是否绑定手机
	Money       int    `db:"account_amount" json:"account,omitempty" ` //账户余额
}

//扫码 前段传入实体映射 ,扫码转发送给充电桩的映射
type ScanBean struct {
	StationCode  string `json:"stationCode"`         //充电站id
	Code         string `json:"code,omitempty"`      //充电桩id
	UserId       string `json:"userId,omitempty"`    //用户id=accoutid
	PointNum     int    `json:"pointNum,omitempty"`  //枪口
	Type         int    `json:"type,omitempty"`      //0:及时充电 1:定时启动充电 2:预约充电
	Timer        int64  `json:"timer,omitempty"`     //为0
	Strategy     int    `json:"strategy,omitempty"`  //充电策略0,充满(及时充电,充到没钱或者充满电或者自己扒开结束) 1,时间控制定时(例如充电2小时)
	Parameter    int    `json:"parameter,omitempty"` //时间参数或者金钱,根据充电策略
	Money        int    `json:"money,omitempty"`
	ScanSubmitId string `json:"scanSubmitId,omitempty"` //formId
}

//查询充电桩地理位置用的
type Station struct {
	Code      string  `db:"code" json:"code,omitempty" `           //站名id
	Name      string  `db:"name" json:"name,omitempty" `           //站名
	Address   string  `db:"address" json:"address,omitempty" `     //地址
	Longitude float64 `db:"longitude" json:"longitude,omitempty" ` //经度float
	Latitude  float64 `db:"latitude" json:"latitude,omitempty" `   //纬度float
	PileCount int     `db:"pileCount" json:"pileCount,omitempty" ` //充电桩数量int
	Status    int     `db:"status" json:"status,omitempty" `       //状态（0 停止运营，1 正常运营）int
	//CreateTime time.Time `db:"createTime" json:"createTime" omitempty`
	//UpdateTime time.Time `db:"updateTime" json:"updateTime" omitempty`
}

//收费记录详情
type ChargeRecordDetails struct {
	Seq       string `db:"order_num" json:"seq"`       //充电记录ID
	Code      string `db:"code" json:"code,omitempty"` //充电桩编号
	Num       int    `db:"num" json:"num,omitempty"`   //枪口
	AccountId string `db:"account_id" json:"userId"`
	Begin     int    `db:"begin" json:"begin"`                 //充值只有开始时间
	End       int    `db:"end" json:"end"`                     //充值没有结束时间
	Time      int    `db:"time" json:"time"`                   //充电时长
	Reason    int    `db:"reason" json:"reason,omitempty"`     //结束原因
	Electric  int    `db:"electric" json:"electric,omitempty"` //充电电量
	Money     int    `db:"money" json:"money"`                 //当前消费金额
}

//金额变动
type AccountRecord struct {
	AccountId string `db:"account_id" json:"userId"`
	Money     int    `db:"money" json:"money"`        //当前触发金额(非账户余额)
	Time      int    `db:"time" json:"time"`          //发生时间时间
	OrderNum  string `db:"order_num" json:"orderNum"` //充电记录ID
	Remarks   int    `db:"remarks" json:"remarks"`    //0:消费,1充值
}

type Value struct {
	Value interface{} `json:"value"`
}

type Keyword struct {
	Keyword1  Value `json:"keyword1,omitempty"`
	Keyword2  Value `json:"keyword2,omitempty"`
	Keyword3  Value `json:"keyword3,omitempty"`
	Keyword4  Value `json:"keyword4,omitempty"`
	Keyword5  Value `json:"keyword5,omitempty"`
	Keyword6  Value `json:"keyword6,omitempty"`
	Keyword7  Value `json:"keyword7,omitempty"`
	Keyword8  Value `json:"keyword8,omitempty"`
	Keyword9  Value `json:"keyword9,omitempty"`
	Keyword10 Value `json:"keyword10,omitempty"`
}

//发送通知用的模板
type SendTemplate struct {
	Touser     string  `json:"touser"` //openId
	TemplateId string  `json:"template_id"`
	FormId     string  `json:"form_id"`
	Page       int     `json:"page,omitempty"`
	Data       Keyword `json:"data"`
}

//接收充电桩通知
type MsgData struct {
	ErrorTime int64  `json:"errorTime"` //异常时间
	Msg       string `json:"msg"`       //异常内容
}
type Advice struct {
	Seq         string  `json:"seq"`            //结账ID或异常ID
	Account     string  `json:"account"`        //用户accountId
	StationCode string  `json:"stationCode"`    //充电站id
	Code        string  `json:"code,omitempty"` //充电桩id
	State       int     `json:"state"`          //状态
	ErrorMsg    MsgData `json:"errorMsg"`       //异常信息
}

//发送给用户的通知模板信息,方便数据库映射
type AdviceData struct {
	ChargingName string  `db:"charging_name" json:"chargingName"`  //充电站名称
	OrderNum     string  `db:"order_num" json:"0rderNum"`          //充电记录ID
	Code         string  `db:"code" json:"code,omitempty"`         //充电桩编号
	Num          int     `db:"num" json:"num,omitempty"`           //枪口
	AccountId    string  `db:"account_id" json:"userId"`           //用户id
	Begin        int64   `db:"begin" json:"begin"`                 //充值只有开始时间
	End          int64   `db:"end" json:"end"`                     //充值没有结束时间
	Time         int     `db:"time" json:"time"`                   //充电时长
	Reason       int     `db:"reason" json:"reason,omitempty"`     //结束原因
	Electric     int     `db:"electric" json:"electric,omitempty"` //充电电量
	Money        float64 `db:"money" json:"money"`                 //当前消费金额
	AccountMoney float64 `db:"money" json:"accountMoney"`          //用户账户余额
}

type ChargingStatus struct {
	State    int    `json:"state"` //充电桩状态
	CarState int    `json:"carstate"`
	Money    int    `json:"money"`     //使用了多少钱
	Time     int    `json:"time"`      //充电时长
	Code     string `json:"code"`      //充电桩编码
	PointNum int    `json:"pointnum"`  //枪口号
	Electric int    `jsonn:"electric"` //充电电量
}

type Activity struct {
	Id     int            `db:"id" json:"id"`                           //主键
	Status int            `db:"status" json:"status"`                   //状态（0 无效，1 有效）
	Type   int            `db:"type" json:"type"`                       //活动类型（1 充值优惠，2 服务费折扣）
	Title  string         `db:"title" json:"title"`                     //活动标题
	Detail string         `db:"detail" json:"detail"`                   //活动详情
	Image  sql.NullString `db:"image,omitempty" json:"image,omitempty"` //活动图片

	Rule string `db:"rule,omitempty" json:"rule,omitempty"` //优惠规则（{amount:充值金额,gift:赠送金额,discount:折扣}）

	StartTime  int `db:"startTime" json:"startTime"`   //开始时间
	EndTime    int `db:"endTime" json:"endTime"`       //结束时间
	CreateTime int `db:"createTime" json:"createTime"` //创建时间
	UpdateTime int `db:"updateTime" json:"updateTime"` //更新时间

	Discount    int            `db:"discount,omitempty" json:"discount,omitempty"`       //服务费折扣
	StationId   int            `db:"stationId,omitempty" json:"stationId,omitempty"`     //充电站ID
	StationName sql.NullString `db:"stationName,omitempty" json:"stationName,omitempty"` //充电站点名称
}

type Rule struct {
	Amount int `json:"amount" redis:"amount,omitempty"` //充值金额
	Gift   int `json:"gift" redis:"gift,omitempty"`     //赠送金额
}

type GiftList struct {
	Rules []Rule `json:"rules" redis:"Rule,omitempty"`
}
