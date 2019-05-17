package util

import (
	"time"
	"fmt"
)

type BCDTime struct{
	Year    uint16
	Mon     uint8
	Day     uint8
	Hour    uint8
	Min     uint8
	Sec     uint8
}
func GetTime(time [8]byte) *BCDTime{
	bcd:=&BCDTime{}
	bcd.Year=((uint16(time[0])>>4)*10+ uint16(time[0]) & 0x0f)*100+(uint16(time[1])>>4)*10+ (uint16(time[1]) & 0x0f)
	bcd.Mon=(time[2]>>4)*10+ (time[2] & 0x0f)
	bcd.Day=(time[3]>>4)*10+ (time[3] & 0x0f)
	bcd.Hour=(time[4]>>4)*10+ (time[4] & 0x0f)
	bcd.Min=(time[5]>>4)*10+ (time[5] & 0x0f)
	bcd.Sec=(time[6]>>4)*10+ (time[6] & 0x0f)
	return bcd
}

func SetTime(bcd *BCDTime)[]byte{
	val:=make([]byte,8)
	val[0]=uint8ToByte(uint8(bcd.Year/100))
	val[1]=uint8ToByte(uint8(bcd.Year%100))
	val[2]=uint8ToByte(bcd.Mon)
	val[3]=uint8ToByte(bcd.Day)
	val[4]=uint8ToByte(bcd.Hour)
	val[5]=uint8ToByte(bcd.Min)
	val[6]=uint8ToByte(bcd.Sec)
	val[7]=byte(0xff)
	return val
}

func uint8ToByte(num uint8) byte{
	var val byte
	mod:=num%10
	val=(num/10)<<4 | mod
	return val
}

func FormatBcdTime(tm [8]byte ) string{
	bcd:=GetTime(tm)
	time.Now().Year()
	return fmt.Sprintf("%04d-%02d-%02d %02d:%02d:%02d",bcd.Year,bcd.Mon,bcd.Day,bcd.Hour,bcd.Min,bcd.Sec)
}

func BcdTimeToUnix(tm[8]byte)int64{
	toBeCharge := FormatBcdTime(tm)
	timeLayout := "2006-01-02 15:04:05"
	loc, _ := time.LoadLocation("Local")
	theTime, _ := time.ParseInLocation(timeLayout, toBeCharge, loc)
	sr := theTime.Unix()
	return sr
}