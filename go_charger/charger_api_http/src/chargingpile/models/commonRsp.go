package models

import (
	"encoding/json"
	"github.com/astaxie/beego/context"
	"github.com/astaxie/beego/logs"
)

type ProtocolRsp struct {
	Code int         `json:"code"`
	Msg  string      `json:"msg"`
	Data interface{} `json:"data,omitempty"`
}

func (data *ProtocolRsp) ResponseWriter(ctx *context.Context) {
	if bytes, err := json.Marshal(data); err != nil {
		logs.Error(err)
	} else {
		if _, err := ctx.ResponseWriter.Write(bytes); err != nil {
			logs.Error(err)
		}
	}
}
