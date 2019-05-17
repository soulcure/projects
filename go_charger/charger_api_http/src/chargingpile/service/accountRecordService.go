package service

import (
	"chargingpile/models"
	"github.com/astaxie/beego/logs"
)

func QueryRecord(userId string, remark int, pageNo int, pageSize int) []*models.AccountRecord {
	rePageNo := (pageNo - 1) * pageSize
	if remark == -1 {
		accountRecord := models.QueryRecord(userId, rePageNo, pageSize)
		if accountRecord != nil {
			logs.Info("查询所有账单记录 success ! info:%+v", accountRecord)
			return accountRecord
		}
		return nil
	}
	byRemark := models.QueryRecordByRemark(userId, remark, rePageNo, pageSize)
	if byRemark != nil {
		logs.Info("查询remark:%v(0消费,1充值) 账单 success ! info:%+v", remark, byRemark)
		return byRemark
	}
	return nil
}

func GetUserChargeRecordService(seq string, userId string) *models.ChargeRecordDetails {
	recordDetails := models.QueryChargeRecordDetails(seq, userId)
	logs.Info("查询费用详情记录  success ! info:%+v", recordDetails)
	return recordDetails
}
