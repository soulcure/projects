package charge

import (
	"container/list"
	"fmt"
	"log"
	"logs"
	"net"
	"net/http"
	"sync"
	"time"
)

const Version uint8 = 0x01

type ConnState uint16

const (
	State_unconn ConnState = 0 //disconnect charge
	State_conned ConnState = 1 //charge have conned server
	State_signin ConnState = 2 //charge haven't sign in server
	State_auth   ConnState = 3
	State_work   ConnState = 4
)

type WorkState uint8

const (
	//桩实际状态
	Work_state_free           WorkState = 0
	Work_state_prepare_charge WorkState = 1
	Work_state_charging       WorkState = 2
	Work_state_charge_finish  WorkState = 3
	Work_state_start_fail     WorkState = 4
	Work_state_ppointment     WorkState = 5
	Work_state_trouble        WorkState = 6
)

func (s WorkState) String() string {
	switch s {
	case Work_state_free:
		return "free"
	case Work_state_prepare_charge:
		return "prepare charge"
	case Work_state_charging:
		return "charging"
	case Work_state_charge_finish:
		return "charge finish"
	case Work_state_start_fail:
		return "start fail"
	case Work_state_ppointment:
		return "ppointment"
	case Work_state_trouble:
		return "trouble"
	}
	return ""
}

type ChargeStation struct {
	Code         string
	ChargeClient *list.List
	Ppointment   int //预约数
}
type ChargeStations struct {
	Stations map[string]*ChargeStation
	sync.RWMutex
}

type ChargePoint struct {
	//	Charge  *ChargeClient
	State WorkState
	//	Num      uint8

	//Account string //账户
	Occupy bool  // 用户设置及时充电 但是充电桩还是空闲这段时间锁定充电桩
	Expire int64 //最多占用时长
	CarState  uint8
}

type ChargeClient struct {
	Id int64

	//charge info
	Code     string
	PointNum uint8
	Points   []ChargePoint

	//station
	StationCode string

	//security
	Bencrypt bool
	AES      [24]byte

	//net info
	Addr  string
	Conn  net.Conn
	State ConnState

	//connect manager
	SigninTime int64

	Seq uint16
	w   http.ResponseWriter
	res chan []byte //use for notify web
	t   int64
	heatBeatNum int
}

type ChargeClients struct {
	charges map[string]*ChargeClient
	sync.RWMutex
}

var Charge *ChargeClients   //record all chargepile
var Station *ChargeStations //record all station

func init() {
	Charge = &ChargeClients{
		charges: make(map[string]*ChargeClient),
	}
	Station = &ChargeStations{
		Stations: make(map[string]*ChargeStation),
	}
}

func NewChargeClient(conn net.Conn) *ChargeClient {
	client := &ChargeClient{
		Conn:     conn,
		State:    State_signin,
		Bencrypt: false,
		res:      make(chan []byte),
	}
	return client
}

func (c *ChargeClients) AddCharge(code string, client *ChargeClient) {
	c.Lock()
	defer c.Unlock()
	log.Println("add charge:", code)
	c.charges[code] = client
}
func (c *ChargeClients) FindCharge(code string) *ChargeClient {
	c.Lock()
	defer c.Unlock()
	charge, ok := c.charges[code]
	if !ok {
		return nil
	}
	return charge
}
func (c *ChargeClients) RemoveCharge(code string,client *ChargeClient) {
	c.Lock()
	defer c.Unlock()

	cl, ok := c.charges[code]
	if !ok {
		return
	}
	if cl==client{
		delete(c.charges, code)
	}

}

//flag: opt 代表是http请求操作状态还是充电桩上报状态 1:http操作
func (c *ChargeClient) SetState(num uint8, state WorkState) {
	c.AlertPointNum(num)
	if c.Points[num].Occupy && time.Now().Unix() > c.Points[num].Expire {
		c.Points[num].Occupy = false
	} else if c.Points[num].Occupy && state != Work_state_free { //如果充电桩状态已经改变，不用锁定
		c.Points[num].Occupy = false
	}
	c.Points[num].State = state
}

//flag: opt 代表是http请求操作状态还是充电桩上报状态 1:http操作
func (c *ChargeClient) SetCarState(num uint8, state uint8) {
	c.AlertPointNum(num)

	c.Points[num].CarState = state
}

//flag: opt 代表是http请求操作状态还是充电桩上报状态 1:http操作
func (c *ChargeClient) GetCarState(num uint8 ) uint8{
	c.AlertPointNum(num)

	return c.Points[num].CarState
}

func (c *ChargeClient) Available(num uint8) bool {
	c.AlertPointNum(num)
	return true
	if !c.Points[num].Occupy && c.Points[num].State == Work_state_free {
		return true
	} else {
		logs.Warn("charge(%s) not available ,state(%s)", c.Code, c.Points[num].State)
		return false
	}
}

func (c *ChargeClient) SetOccupy(num uint8, occupy bool) bool {
	c.AlertPointNum(num)
	if c.Points[num].State != Work_state_free {
		logs.Warn("charge can be used only it is free")
		return false
	} else {
		c.Points[num].Occupy = occupy
		if occupy {
			c.Points[num].Expire = time.Now().Unix() + 60
		} else {
			c.Points[num].Expire = 0
		}
		return true
	}
}

func (c *ChargeClient) AlertPointNum(num uint8) {
	if num > c.PointNum {
		panic(fmt.Sprintf("the  point num(%d) big than charge actual point num(%d) ", num, c.PointNum))
	}
}

//station
func (s *ChargeStations) AddStation(code string) {
	s.Lock()
	defer s.Unlock()
	station := ChargeStation{
		Code:         code,
		ChargeClient: list.New(),
	}
	s.Stations[code] = &station
}

func (s *ChargeStations) AddCharge(stationCode string, charge *ChargeClient) {
	s.Lock()
	defer s.Unlock()
	station, ok := s.Stations[stationCode]
	if !ok {
		station = &ChargeStation{
			Code:         stationCode,
			ChargeClient: list.New(),
		}
		s.Stations[stationCode] = station
	}
	station.ChargeClient.PushBack(charge)
}

func (s *ChargeStations) GetFreeCharge(stationCode string) (*ChargeStation, int) {
	num := 0
	station, ok := s.Stations[stationCode]
	if !ok {
		return nil, 0
	} else {
		for it := station.ChargeClient.Front(); it != nil; it.Next() {
			client := it.Value.(*ChargeClient)
			for i := uint8(0); i < client.PointNum; i++ {
				if client.Points[i].State == Work_state_free {
					num++
				}
			}
		}
	}
	return station, num
}

//todo
func (s *ChargeStations) CanPpointment(stationCode string) bool {
	s.Lock()
	defer s.Unlock()
	station, num := s.GetFreeCharge(stationCode)
	if station != nil && num > station.Ppointment {
		station.Ppointment++
		return true
	}
	return false
}

//todo:according charge code get station id
//code :NO| 8 BYTE station code|6 byte seq
func GetStationCode(code string) string {
	return code[2:10]
}
