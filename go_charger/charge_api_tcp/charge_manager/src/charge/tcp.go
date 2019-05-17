package charge

import (
	"bytes"
	"encoding/binary"
//	"encoding/hex"
	"errors"
	"log"
	"logs"
)

//发送组协议
func send(c *ChargeClient, cmd CommandId, header []byte, body interface{}) (n int, err error) {
	//	log.Println("send:", body)
	//cmd
	header[6] = byte(cmd)
	header[7] = byte(cmd >> 8)

	//write data
	sendbuf := new(bytes.Buffer)

	err = binary.Write(sendbuf, binary.LittleEndian, header)
	if err != nil {
		log.Println("binary.Write failed:", err)
		return 0, err
	}
	if Encrypt(header) {
		//first encrypt
		tmpbuf := new(bytes.Buffer)
		err = binary.Write(tmpbuf, binary.LittleEndian, body)
		if err != nil {
			log.Println("binary.Write failed:", err)
			return 0, err
		}
		l := uint16(tmpbuf.Len())
		result, err := AesEncrypt(tmpbuf.Bytes(), c.AES[:])
		if err != nil {
			log.Println(err)
			return 0, err
		}
		//second write
		err = binary.Write(sendbuf, binary.LittleEndian, l)
		if err != nil {
			log.Println("binary.Write failed:", err)
			return 0, err
		}
		err = binary.Write(sendbuf, binary.LittleEndian, result)
		if err != nil {
			log.Println("binary.Write failed:", err)
			return 0, err
		}

	} else {
		err = binary.Write(sendbuf, binary.LittleEndian, body)
		if err != nil {
			log.Println("binary.Write failed:", err)
			return 0, err
		}
	}

	//len
	data := sendbuf.Bytes()
	l := uint16(len(data) + 1)
	data[2] = byte(l)
	data[3] = byte(l >> 8)

	//crc
	sum := checkSum(data[6:])
	err = binary.Write(sendbuf, binary.LittleEndian, byte(sum))
	if err != nil {
		log.Println("binary.Write failed:", err)
		return 0, err
	}
	n, err = c.Conn.Write(sendbuf.Bytes())

	if err != nil || n != sendbuf.Len() {
		log.Println("send to tcpserver fail")
	}
	log.Printf("send success len:%d", n)
	//	logs.Warn(hex.EncodeToString(sendbuf.Bytes()))
	return n, err
}

func checkSum(data []byte) uint32 {
	var sum uint32 = 0
	for _, v := range data {
		sum += uint32(v)
		//	log.Println(v,sum)
	}
	return sum
}

func Decode(c *ChargeClient, tmpBuffer []byte) ([]byte, error) {
	var protoSize uint16 = 0
	var cmd uint16

	for {
		if len(tmpBuffer) >= 8 { //enough header
			if !(uint8(tmpBuffer[0]) == FLAG0 && uint8(tmpBuffer[1]) == FLAG1) {
				return nil, errors.New("flag error")
			}
			if len(tmpBuffer) >= int(HEADLEN) {
				cmd = binary.LittleEndian.Uint16(tmpBuffer[6:8])
				protoSize = binary.LittleEndian.Uint16(tmpBuffer[2:4])
			}
			if len(tmpBuffer) >= int(protoSize) {
				datalen := protoSize - HEADLEN - 1
				sum := checkSum(tmpBuffer[6 : protoSize-1])
				logs.Debug("cmd:", cmd, "size:", protoSize, "checksum:", sum)
				if uint8(sum&0XFF) != uint8(tmpBuffer[protoSize-1]) {
					tmpBuffer = tmpBuffer[:0]
					return nil, errors.New("checksum err")
				}
				handler(c, CommandId(cmd), tmpBuffer[0:HEADLEN], tmpBuffer[HEADLEN:datalen+HEADLEN])
				tmpBuffer = tmpBuffer[protoSize:]
				continue
			} else {
				logs.Debug("not enough")
				break
			}
		} else {
			break
		}
	}
	return tmpBuffer, nil
}

func handler(c *ChargeClient, cmd CommandId, header []byte, body []byte) {
	defer func() {
		if err := recover(); err != nil {
			logs.Error("catch panic:", err)
		}
	}()
	for _, v := range handlers {
		if v.cmd == cmd {
			logs.Info("-------------%v(%s)-------------->", v.name, c.Code)
			var err error
			if cmd == C_CHARGE_CERTIFICATION_REQ || cmd == C_SIGN_IN_REQ {
				err = v.handler(c, cmd, header, body)
			} else {
				var data []byte
				if cmd != C_HEATBEAT_REQ && c.State < State_work {
					logs.Info("state(%d) charge(%s) not sign in .....", c.State, c.Code)
					c.State = State_unconn
					return
				}
				if Encrypt(header) {
					if !c.Bencrypt {
						logs.Warn("client not support encrypt,but the request(%v) AES encrypt ", c.Code)
						return
					}

					if data, err = AesDecrypt(body[2:], c.AES[:]); err != nil {
						logs.Error(v.name, err)
						return
					} else {
						err = v.handler(c, cmd, header, data)
					}
				} else {
					//todo check
					err = v.handler(c, cmd, header, body)
				}
			}

			if err != nil {
				logs.Error(v.name, err)
			}
			return
		}
	}
	logs.Warn("unknow cmd:", cmd)
}

func Encrypt(header []byte) bool {
	return header[4]>>7 == 1
}
