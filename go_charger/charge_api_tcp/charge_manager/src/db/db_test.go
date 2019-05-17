package db

import (
	"log"
	"strconv"
	"time"
)

func test(ch chan int64){
	for i:=0;i<1000;i++{
		instance:=Db.GetDb()
		if instance==nil{
			log.Println("get instance fail")
			break
		}
		stmtIns, err :=instance.Db.Prepare("insert into test values (?,?,?)")
		if err != nil {
			panic(err.Error()) // proper error handling instead of panic in your app
		}
		_, err = stmtIns.Exec(i ,"python","http://baidu.com?addr="+strconv.Itoa(i)) // Insert tuples (i, i^2)
		if err != nil {
			panic(err.Error()) // proper error handling instead of panic in your app
		}
		instance.Release()
		stmtIns.Close()
	}
	ch <-time.Now().UnixNano()/1e6
}
func testDb() {

	ch:=make( chan int64)
	begin:=time.Now().UnixNano()/1e6
	for i:=0;i<36;i++{
		go test(ch)
	}
	num:=0
	now:=time.Now().UnixNano()/1e6
	for c:=range ch{
		num++
		log.Println(num,":",c-begin)
		if num==36{
			break
		}
	}
	end:=time.Now().UnixNano()/1e6
	log.Println("finish,millsecond:",end-now)

}

func checkErr(err error) {
	if err != nil {
		panic(err)
	}
}