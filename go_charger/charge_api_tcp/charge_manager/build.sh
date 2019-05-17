PWD=`pwd`
echo $PWD
export GOPATH=$PWD
go build   -o ./bin/charge_mgr  app
