package util

import "log"

func testTime(){
	bcd:=BCDTime{2000,10,30,9,2,10}
	data:=SetTime(&bcd)

	res,_:=GetTime(data)

	if *res==bcd{
		log.Println("success")
	}else{
		log.Println("fail")
	}
	log.Println(*res)
}
