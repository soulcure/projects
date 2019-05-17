package charge

import (
	"fmt"
	"log"
	"logs"
	"reflect"
)

func alterSize(data []byte, i interface{}) {
	size := Sizeof(reflect.ValueOf(i))
	len := len(data)
	if len < size {
		panic(fmt.Sprintf("%s size:%d,data size:%d ---->not equal,not parse", reflect.TypeOf(i).Name(), size, len))
	} else if len > size {
		logs.Warn("%s size:%d,data size:%d ---->not equal,data maybe error", reflect.TypeOf(i).Name(), size, len)
	}
}

func bytetostr(data []byte) string {
	for i := 0; i < len(data); i++ {
		if data[i] == 0 {
			return string(data[0:i])
		}
	}
	return string(data)
}
func strtobyte(data []byte, str string) {
	for i := 0; i < len(str) && i < len(data); i++ {
		data[i] = str[i]
	}
}
func display(path string, v reflect.Value) string {
	switch v.Kind() {
	case reflect.Struct:
		for i := 0; i < v.NumField(); i++ {
			fieldpath := fmt.Sprintf("%s.%s", path, v.Type().Field(i).Name)
			display(fieldpath, v.Field(i))
		}
	case reflect.Map:
		for _, key := range v.MapKeys() {
			fieldpath := fmt.Sprintf("%s[%s]", path, key)
			display(fieldpath, v.MapIndex(key))
		}
	/*case reflect.Array,reflect.Slice:
	for i:=0;i<v.Len();i++{
		display(fmt.Sprintf("%s[%d]",path,i),v.Index(i))
	}*/
	case reflect.Array: //only for []byte
		arr := make([]byte, v.Len())
		for i := 0; i < v.Len(); i++ {
			arr[i] = uint8(v.Index(i).Uint())
		}
		return fmt.Sprintf("%s=%s ", path, bytetostr(arr))
	case reflect.Ptr:
		if v.IsNil() {
			log.Printf("%s=nil", path)
		} else {
			display(fmt.Sprintf("*%s", path), v.Elem())
		}
	default:
		return fmt.Sprintf("%s=%v ", path, v)
	}
	return ""
}

func printStruct(i interface{}) {
	v := reflect.ValueOf(i)
	str := v.Type().Name() + "{ "
	switch v.Kind() {
	case reflect.Struct:
		for i := 0; i < v.NumField(); i++ {
			str += display(v.Type().Field(i).Name, v.Field(i))
		}
	default:
		panic("type error")
	}
	str += "}"
	logs.Info(str)
}
