package com.taku.safe.protocol.respond;


/**
 * Created by colin on 2017/6/6.
 */

public class RespRestSignData extends RespBaseBean {
    /**
     * notSignin : 0
     * siginNumExpect : 0
     * siginNumAbnormal : 0
     * siginNumAct : 0
     * notActive : 0
     * siginNumNormal : 0
     */

    private int notSignin;  //未签到
    private int siginNumExpect; //应签到人数
    private int siginNumAbnormal;//异常
    private int siginNumAct;  //签到人数
    private int notActive;//未激活
    private int siginNumNormal;//正常

    public int getNotSignin() {
        return notSignin;
    }

    public void setNotSignin(int notSignin) {
        this.notSignin = notSignin;
    }

    public int getSiginNumExpect() {
        return siginNumExpect;
    }

    public void setSiginNumExpect(int siginNumExpect) {
        this.siginNumExpect = siginNumExpect;
    }

    public int getSiginNumAbnormal() {
        return siginNumAbnormal;
    }

    public void setSiginNumAbnormal(int siginNumAbnormal) {
        this.siginNumAbnormal = siginNumAbnormal;
    }

    public int getSiginNumAct() {
        return siginNumAct;
    }

    public void setSiginNumAct(int siginNumAct) {
        this.siginNumAct = siginNumAct;
    }

    public int getNotActive() {
        return notActive;
    }

    public void setNotActive(int notActive) {
        this.notActive = notActive;
    }

    public int getSiginNumNormal() {
        return siginNumNormal;
    }

    public void setSiginNumNormal(int siginNumNormal) {
        this.siginNumNormal = siginNumNormal;
    }
}
