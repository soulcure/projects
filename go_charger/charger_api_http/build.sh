PWD=`pwd`
echo $PWD
export GOPATH=$PWD
go build   -o ./bin/user_mgr  chargingpile/main
