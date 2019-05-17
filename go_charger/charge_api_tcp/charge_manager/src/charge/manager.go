package charge

import (
	"log"
	"net/http"
	"util"
	//"encoding/json"
	"bytes"
	"encoding/binary"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"logs"
	"time"
)

var routes map[string]func(http.ResponseWriter, *http.Request)

func MgrServerStart() {
	routes = make(map[string]func(http.ResponseWriter, *http.Request))
	addr := util.CFG.Section("manager").Key("addr").String()
	log.Println("manager server listen addr:", addr)
	/*setRoute("/mgr/bill/set",billSet)
	setRoute("/mgr/bill/get",billGet)*/

	http.HandleFunc("/mgr/bill/set", billSet)
	http.HandleFunc("/mgr/bill/get", billGet)
	http.HandleFunc("/mgr/interger", intergerSet)
	http.HandleFunc("/mgr/string", stringSet)
	http.HandleFunc("/mgr/control", controlCmd)
	//http.HandleFunc("/mgr/state", controlCmd)
	err := http.ListenAndServe(addr, nil)
	if err != nil {
		log.Fatal("ListenAndServe: ", err)
	}
}

func setRoute(pattern string, handler func(w http.ResponseWriter, r *http.Request)) {
	routes[pattern] = handler
}

func mgr(w http.ResponseWriter, r *http.Request) {
	path := r.URL.Path
	handler, ok := routes[path]
	if !ok {
		log.Println("not find route")
		return
	}
	//auth  .....
	handler(w, r)
}

func billSet(w http.ResponseWriter, r *http.Request) {
	if r.Method != "POST" {
		http.Error(w, "Must use post  method", http.StatusMethodNotAllowed)
		return
	}
	if s, err := ioutil.ReadAll(r.Body); err != nil {
		logs.Error(err)
		httpError(w, charge__pareameter_error, "parameter error")
		return
	} else {
		res := struct {
			Code  string
			Rates []Rate
		}{}
		var req BillingSetReq
		err := json.Unmarshal(s, &res)
		if err != nil {
			logs.Error(s, err)
			httpError(w, charge__pareameter_error, "parameter error")
			return
		}
		logs.Info(res)
		client := Charge.FindCharge(res.Code)
		if client == nil {
			logs.Warn("charge (%s) not sign in\n", res.Code)
			httpError(w, charge_pile_unused, "charge:"+res.Code+" not sign in")
			return
		}
		logs.Info(" start billSet  for  charge(%s) ", res.Code)
		for i := 0; i < len(res.Rates); i++ {
			req.Rating[i] = res.Rates[i]
		}
		header, _ := newHeader()
		send(client, S_BILLING_SET_REQ, header, &req)
		data, ok := waitTime(client, 8*time.Second)
		if !ok {
			fmt.Fprint(w, string(data))
		} else {
			logs.Warn("billSet timeout")
			httpError(w, charge_timeout, "timeout")
		}
	}
}

func billGet(w http.ResponseWriter, r *http.Request) {
	if r.Method != "POST" {
		http.Error(w, "Must use post  method", http.StatusMethodNotAllowed)
		return
	}
	if s, err := ioutil.ReadAll(r.Body); err != nil {
		logs.Error(err)
		httpError(w, charge__pareameter_error, "parameter error")
		return
	} else {
		res := struct {
			Code string
		}{}
		var req BillingQueryReq
		err := json.Unmarshal(s, &res)
		if err != nil {
			logs.Error(s, err)
			httpError(w, charge__pareameter_error, "parameter error")
			return
		}
		client := Charge.FindCharge(res.Code)
		if client == nil {
			logs.Warn("charge (%s) not sign in\n", res.Code)
			httpError(w, charge_pile_unused, "charge:"+res.Code+" not sign in")
			return
		}
		logs.Info(" start billGet  for  charge(%s) \n", res.Code)

		header, _ := newHeader()
		send(client, S_BILLING_QUERY_REQ, header, &req)
		data, ok := waitTime(client, 8*time.Second)
		if !ok {
			fmt.Fprint(w, string(data))
		} else {
			logs.Warn("billGet timeout")
			httpError(w, charge_timeout, "timeout")
		}
	}
}

func intergerSet(w http.ResponseWriter, r *http.Request) {
	if r.Method != "POST" {
		http.Error(w, "Must use post  method", http.StatusMethodNotAllowed)
		return
	}
	if s, err := ioutil.ReadAll(r.Body); err != nil {
		logs.Error(err)
		httpError(w, charge__pareameter_error, "parameter error")
		return
	} else {
		res := struct {
			Code string
			Type uint8
			Addr uint32
			Data []uint32
			Num  uint8
		}{}
		var req IntegerParameterReq
		err := json.Unmarshal(s, &res)
		if err != nil {
			logs.Error(s, err)
			httpError(w, charge__pareameter_error, "parameter error")
			return
		}
		logs.Info(res)
		client := Charge.FindCharge(res.Code)
		if client == nil {
			logs.Warn("charge (%s) not sign in\n", res.Code)
			httpError(w, charge_pile_unused, "charge:"+res.Code+" not sign in")
			return
		}
		logs.Info(" start intergerSet  for  charge(%s) \n", res.Code)
		req.Type = res.Type
		req.Addr = res.Addr
		req.Num = res.Num
		tmpbuf := new(bytes.Buffer)
		err = binary.Write(tmpbuf, binary.LittleEndian, &req)
		if err != nil {
			logs.Error("binary.Write failed:", err)
			return
		}
		if req.Type == 1 {
			req.Size = uint16(4 * len(res.Data))
			err = binary.Write(tmpbuf, binary.LittleEndian, res.Data)
			if err != nil {
				logs.Error("binary.Write failed:", err)
				return
			}
		}
		header, _ := newHeader()
		send(client, S_INTEGER_PARAMETER_REQ, header, tmpbuf.Bytes())
		data, ok := waitTime(client, 8*time.Second)
		if !ok {
			fmt.Fprint(w, string(data))
		} else {
			logs.Warn("intergerSet timeout")
			httpError(w, charge_timeout, "timeout")
		}
	}
}

func stringSet(w http.ResponseWriter, r *http.Request) {
	if r.Method != "POST" {
		http.Error(w, "Must use post  method", http.StatusMethodNotAllowed)
		return
	}

	if s, err := ioutil.ReadAll(r.Body); err != nil {
		logs.Error(err)
		httpError(w, charge__pareameter_error, "parameter error")
		return
	} else {
		log.Println(string(s))
		res := struct {
			Code string
			Type uint8
			Addr uint32
			Data string
		}{}
		var req CharParameterReq
		err := json.Unmarshal(s, &res)
		if err != nil {
			logs.Error(s, err)
			httpError(w, charge__pareameter_error, "parameter error")
			return
		}
		client := Charge.FindCharge(res.Code)
		if client == nil {
			logs.Warn("charge (%s) not sign in", res.Code)
			httpError(w, charge_pile_unused, "charge:"+res.Code+" not sign in")
			return
		}
		logs.Info(" start stringSet  for  charge ", res.Code)
		req.Type = res.Type
		req.Addr = res.Addr
		req.Size = uint16(len(res.Data))

		tmpbuf := new(bytes.Buffer)
		err = binary.Write(tmpbuf, binary.LittleEndian, &req)
		if err != nil {
			logs.Error("binary.Write failed:", err)
			return
		}
		if req.Type == 1 {
			err = binary.Write(tmpbuf, binary.LittleEndian, []byte(res.Data))
			if err != nil {
				logs.Error("binary.Write failed:", err)
				return
			}
		}

		header, _ := newHeader()
		send(client, S_CHAR_PARAMETER_REQ, header, tmpbuf.Bytes())
		data, ok := waitTime(client, 8*time.Second)
		if !ok {
			fmt.Fprint(w, string(data))
		} else {
			logs.Warn("stringSet timeout")
			httpError(w, charge_timeout, "timeout")
		}
	}
}

func controlCmd(w http.ResponseWriter, r *http.Request) {
	if r.Method != "POST" {
		http.Error(w, "Must use post  method", http.StatusMethodNotAllowed)
		return
	}
	if s, err := ioutil.ReadAll(r.Body); err != nil {
		logs.Error(err)
		httpError(w, charge__pareameter_error, "parameter error")
		return
	} else {
		res := struct {
			Code     string
			Ponitnum uint8
			Addr     uint32
			Data     []uint32
			Num      uint8
		}{}
		var req ControlCmdReq
		err := json.Unmarshal(s, &res)
		if err != nil {
			logs.Error(s, err)
			httpError(w, charge__pareameter_error, "parameter error")
			return
		}
		logs.Info(res)
		client := Charge.FindCharge(res.Code)
		if client == nil {
			logs.Warn("charge (%s) not sign in", res.Code)
			httpError(w, charge_pile_unused, "charge:"+res.Code+" not sign in")
			return
		}
		logs.Info(" start controlCmd  for  charge(%s)", res.Code)
		req.PointNum = res.Ponitnum
		req.Addr = res.Addr
		req.Num = res.Num
		req.Size = uint16(4 * len(res.Data))
		tmpbuf := new(bytes.Buffer)
		err = binary.Write(tmpbuf, binary.LittleEndian, &req)
		if err != nil {
			logs.Error("binary.Write failed:", err)
			return
		}
		err = binary.Write(tmpbuf, binary.LittleEndian, res.Data)
		if err != nil {
			logs.Error("binary.Write failed:", err)
			return
		}
		header, _ := newHeader()
		send(client, S_CONTROL_CMD_REQ, header, tmpbuf.Bytes())
		data, ok := waitTime(client, 8*time.Second)
		if !ok {
			fmt.Fprint(w, string(data))
		} else {
			logs.Warn("controlCmd timeout")
			httpError(w, charge_timeout, "timeout")
		}
	}
}

func stopCharge(client *ChargeClient, PointNum uint8) {
	var req ControlCmdReq
	req.PointNum = 1
	req.Addr = 2
	req.Num = 1
	req.Size = 4
	data := uint32(0x55)
	tmpbuf := new(bytes.Buffer)
	err := binary.Write(tmpbuf, binary.LittleEndian, &req)
	if err != nil {
		logs.Error("binary.Write failed:", err)
		return
	}
	err = binary.Write(tmpbuf, binary.LittleEndian, data)
	if err != nil {
		logs.Error("binary.Write failed:", err)
		return
	}
	header, _ := newHeader()
	send(client, S_CONTROL_CMD_REQ, header, tmpbuf.Bytes())
}
