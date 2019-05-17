package main

import "net"
import (
	"bytes"
	"charge"
	"encoding/binary"
	"log"
	"logs"
	"util"
)

var app_code string = "cgjim"
var app_token string = "027be0b626e4414d9ea614ecab4aaa8e"
var url_str string = "https://impush-cgj.colourlife.com/api/business/sendIM"

//var url_str string="http://127.0.0.1:8070/test"
func init() {
	app_code = util.CFG.Section("auth").Key("app_code").String()
	app_token = util.CFG.Section("auth").Key("app_token").String()
	url_str = util.CFG.Section("auth").Key("url").String()

}

func startServer(addr string) error {
	fd, err := net.Listen("tcp", addr)
	if err != nil {
		return err
	}
	log.Println("tcp  listen addr:", addr)
	for {
		conn, err := fd.Accept()
		if err != nil {
			log.Print(err) // e.g., connection aborted
			continue
		}
		go handleConn(conn) // handle one connection at a time
	}
}

func handleConn(c net.Conn) error {
	defer c.Close()
	logs.Info("client connect....")
	tmpBuffer := make([]byte, 0)
	buf := make([]byte, 1024*8) //packet len must less than 8k,
	client := charge.NewChargeClient(c)
	for client.State != charge.State_unconn {
		buflen, err := c.Read(buf)
		logs.Debug("read:", buflen)
		if err != nil {
			logs.Info("client is close ...", err)
			break
		}
		tmpBuffer = append(tmpBuffer, buf[:buflen]...)
		//	log.Println(tmpBuffer)
		//	logs.Warn(hex.EncodeToString(tmpBuffer))
		/*var s string
		for i := 0; i < len(tmpBuffer); i++ {
			str := fmt.Sprintf("0x%02x,", tmpBuffer[i])
			s += strings.ToUpper(str)
		}*/
		//logs.Debug(s)
		if tmpBuffer, err = charge.Decode(client, tmpBuffer); err != nil {
			logs.Error(err, "close the socket")
			break
		}
	}
	logs.Info("charge (%s) close connect....\n", client.Code)
	charge.Charge.RemoveCharge(client.Code, client)
	return nil
}
func test() {
	buf := new(bytes.Buffer)
	var req charge.ChargeRecordReq
	binary.Write(buf, binary.LittleEndian, req)
	log.Println(buf.Len())
}

func main() {
	charge.TchargeRecordReq()
	var err error = nil
	go charge.HttpServerStart()
	go charge.MgrServerStart()
	addr := util.CFG.Section("server").Key("addr").String()
	log.Println("addr:", addr)
	//	go Timer()
	err = startServer(addr)
	if err != nil {
		log.Println(err)
	}
	log.Println("server exist")
}
