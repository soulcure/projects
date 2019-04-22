package com.taku.safe.protocol.respond;

import com.taku.safe.TakuApp;

/**
 * Created by soulcure on 2017/6/3.
 */

public class RespBaseBean {

    /**
     * 0：正常
     * 400：请求参数错误
     * 401：签名校验失败
     * 403：权限设置原因拒绝请求
     * 500：系统服务内部错误
     */
    public int code;
    public String msg;
    public long ts;

    public static TakuApp mTakuApp;

    public static void setTakuApp(TakuApp app) {
        mTakuApp = app;
    }

    public boolean isSuccess() {
        if (code == 0) {
            return true;
        } else if (code == 401) {
            if (mTakuApp != null) {
                mTakuApp.reLoginByProtoCode();
            }
            return false;
        } else {
            return false;
        }
    }

    public boolean isReBindDevice() {
        return code == 504 && msg.contains("已绑定其他手机");
    }


    public String getMsg() {
        return msg;
    }

    public long getTs() {
        return ts;
    }

}
