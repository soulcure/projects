package db

import (
	"github.com/go-redis/redis"
	"log"
	"os"
	"util"
)

const (
	Charge_state_info int = 0
	User_charge_state int = 1

	User_charge_record int = 2
	User_charge_index  int = 3
)

var RedisClient [4]*redis.Client

func init() {
	addr := util.CFG.Section("redis").Key("addr").String()
	password := util.CFG.Section("redis").Key("password").String() // no password set
	for i := 0; i < 4; i++ {
		RedisClient[i] = connect(addr, password, i)
	}
	log.Println("redis connect success")
}

func connect(add, password string, index int) *redis.Client {
	poolSize, _ := util.CFG.Section("redis").Key("poolSize").Int()

	client := redis.NewClient(&redis.Options{
		Addr:               add,
		Password:           password, // no password set
		DB:                 index,    // use default DB
		PoolSize:           poolSize,
		ReadTimeout:        1000 * 1000 * 1000 * 60,
		IdleTimeout:        1000 * 1000 * 1000 * 3,
		IdleCheckFrequency: 1000 * 1000 * 1000 * 1,
		//PoolTimeout:30000,
	})

	//测试连接
	err := client.Ping().Err()
	if err != nil {
		log.Println(err)
		os.Exit(404)
	}
	return client
}
