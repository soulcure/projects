package util

import (
	"github.com/go-ini/ini"
	"log"
	"os"
)

var CFG *ini.File
func init(){
	CfgInit()
}
func CfgInit(){
	cfg, err :=ini.Load("config.ini")
	if err != nil {
		log.Println("config parse fail",err)
		os.Exit(2)
		return
	}
	CFG=cfg
}
