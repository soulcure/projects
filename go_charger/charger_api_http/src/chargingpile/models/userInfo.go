package models

type UserInfo struct {
	//用户基础信息
	AvatarURL string `json:"avatarUrl"`
	City      string `json:"city"`
	Country   string `json:"country"`
	Gender    int    `json:"gender"`
	Language  string `json:"language"`
	NickName  string `json:"nickName"`
	OpenID    string `json:"openId"`
	Province  string `json:"province"`

	//用户手机号
	CountryCode     string `json:"countryCode"`
	PhoneNumber     string `json:"phoneNumber"`
	PurePhoneNumber string `json:"purePhoneNumber"`

	Watermark struct {
		Appid     string `json:"appid"`
		Timestamp int    `json:"timestamp"`
	} `json:"watermark"`
}
