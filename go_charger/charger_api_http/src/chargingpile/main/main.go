package main

import (
	_ "chargingpile/data"
	_ "chargingpile/router"
	_ "chargingpile/service"
	_ "chargingpile/utils"
	"github.com/astaxie/beego"
)

func main() {
	beego.Run()
}
