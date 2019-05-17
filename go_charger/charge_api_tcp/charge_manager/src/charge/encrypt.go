package charge

import (
	"util"
	"log"
	"crypto/rsa"
	"crypto/rand"
	"crypto/x509"
	"encoding/pem"
	"os"
	"bytes"
	"crypto/aes"
	"crypto/cipher"
	"time"
	mrand "math/rand"
	"errors"
)

const RSABIT int=1024
var(
	bauth   bool=false
	bEncrypt bool=false
	derStream []byte  //私密
	modules [RSABIT/8]byte  //模数
	exponent  uint32

	privblock *pem.Block
	pubblock  *pem.Block
)
func test(){
	src :="adbcde"
	data,err := RsaEncrypt([]byte(src))
	if err != nil {
		panic(err)
	}
	origData, err := RsaDecrypt(data)
	if err != nil {
		panic(err)
	}
	if src==string(origData){
		log.Println("success")
	}else{
		log.Println("fail")
	}
}
func init(){
	bauth ,_= util.CFG.Section("security").Key("auth").Bool()
	bEncrypt,_= util.CFG.Section("security").Key("encrypt").Bool()
	if bEncrypt{
		if err := GenRsaKey(RSABIT); err != nil {
			log.Fatal("GenRsaKey fail！")
			os.Exit(0)
		}
		log.Println("GenRsaKey success！")
	}
	test()
}
func checkEncrypt() bool{
	return bEncrypt
}

func auth() bool{
	return bauth
}

func GenRsaKey(bits int) error {
	// 生成私钥文件
	privateKey, err := rsa.GenerateKey(rand.Reader, bits)
	if err != nil {
		return err
	}
	derStream = x509.MarshalPKCS1PrivateKey(privateKey)
	block := &pem.Block{
		Type:  "私钥",
		Bytes: derStream,
	}
	file, err := os.Create("private.pem")
	if err != nil {
		return err
	}
	err = pem.Encode(file, block)
	if err != nil {
		return err
	}
	privblock=block
	// 生成公钥文件
	publicKey := &privateKey.PublicKey
	data:=publicKey.N.Bytes() //bigendian

	l:=len(data)
	if l!=RSABIT/8{
		return errors.New("GenRsaKey fail")
	}
	for i,v:=range data{
		modules[i]=v
	}
	exponent=uint32(publicKey.E)
	log.Println("modules:",modules,"ex:",exponent)
	log.Println(publicKey.N)
	derPkix, err := x509.MarshalPKIXPublicKey(publicKey)
	if err != nil {
		return err
	}
	block = &pem.Block{
		Type:  "公钥",
		Bytes: derPkix,
	}
	file, err = os.Create("public.pem")
	if err != nil {
		return err
	}
	err = pem.Encode(file, block)
	if err != nil {
		return err
	}
	pubblock=block
	return nil
}

// 加密
func RsaEncrypt(origData []byte) ([]byte, error) {
	pubInterface, err := x509.ParsePKIXPublicKey(pubblock.Bytes)
	if err != nil {
		return nil, err
	}
	pub := pubInterface.(*rsa.PublicKey)
	return rsa.EncryptPKCS1v15(rand.Reader, pub, origData)
}

// 解密
func RsaDecrypt(ciphertext []byte) ([]byte, error) {
	priv, err := x509.ParsePKCS1PrivateKey(privblock.Bytes)
	if err != nil {
		return nil, err
	}
	return rsa.DecryptPKCS1v15(rand.Reader, priv, ciphertext)
}



func AesEncrypt(origData, key []byte) ([]byte, error) {
	block, err := aes.NewCipher(key)
	if err != nil {
		return nil, err
	}
	blockSize := block.BlockSize()
	origData = PKCS5Padding(origData, blockSize)
	// origData = ZeroPadding(origData, block.BlockSize())
	blockMode := cipher.NewCBCEncrypter(block, key[:blockSize])
	crypted := make([]byte, len(origData))
	// 根据CryptBlocks方法的说明，如下方式初始化crypted也可以
	// crypted := origData
	blockMode.CryptBlocks(crypted, origData)
	return crypted, nil
}

func AesDecrypt(crypted, key []byte) ([]byte, error) {
	block, err := aes.NewCipher(key)
	if err != nil {
		return nil, err
	}
	blockSize := block.BlockSize()
	blockMode := cipher.NewCBCDecrypter(block, key[:blockSize])
	origData := make([]byte, len(crypted))
	// origData := crypted
	blockMode.CryptBlocks(origData, crypted)
	origData = PKCS5UnPadding(origData)
	//	origData = ZeroUnPadding(origData)
	log.Println("crypted:",crypted)
	log.Println("decode:",origData)
	return origData, nil
}

func ZeroPadding(ciphertext []byte, blockSize int) []byte {
	padding := blockSize - len(ciphertext)%blockSize
	padtext := bytes.Repeat([]byte{0}, padding)
	return append(ciphertext, padtext...)
}

func ZeroUnPadding(origData []byte) []byte {
	length := len(origData)
	unpadding := int(origData[length-1])
	return origData[:(length - unpadding)]
}

func PKCS5Padding(ciphertext []byte, blockSize int) []byte {
	padding := blockSize - len(ciphertext)%blockSize
	padtext := bytes.Repeat([]byte{byte(padding)}, padding)
	return append(ciphertext, padtext...)
}

func PKCS5UnPadding(origData []byte) []byte {
	length := len(origData)
	// 去掉最后一个字节 unpadding 次
	unpadding := int(origData[length-1])
	return origData[:(length - unpadding)]
}


func GetRandomString(lenght int) string{
	str := "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
	bytes := []byte(str)
	bytesLen := len(bytes)
	result := []byte{}
	r := mrand.New(mrand.NewSource(time.Now().UnixNano()))
	for i := 0; i < lenght; i++ {
		result = append(result, bytes[r.Intn(bytesLen)])
	}
	return string(result)
}