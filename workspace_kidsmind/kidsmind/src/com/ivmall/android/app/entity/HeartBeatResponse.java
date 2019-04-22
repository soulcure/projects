/**
 *
 */
package com.ivmall.android.app.entity;


/**
 * 支付交易状态查询接口响应结果封装基础类
 */
public class HeartBeatResponse {


    private int code; // 错误类型
    private String message; // 错误信息


    private UserInfo data;


    /**
     * 118表示需要支付
     *
     * @return
     */
    public boolean isNeedPayVip() {
        boolean res = false;
        if (code == 118) {
            res = true;
        }
        return res;
    }

    /**
     * 200表示需要支付
     *
     * @return
     */
    public boolean success() {
        boolean res = false;
        if (code == 200) {
            res = true;
        }
        return res;
    }


    public String getMessage() {
        return message;
    }


    public int getUserId() {
        return data.getUserId();
    }

    public UserInfo.VipType getVipLevel() {
        return data.getVipLevel();
    }

    public boolean isInvited() {
        return data.isInvited();
    }

    public String getVipExpiresTime() {
        return data.getVipExpiresTime();
    }

    public String getName() {
        return data.getName();
    }

    public String getMobile() {
        return data.getMobile();
    }
}
