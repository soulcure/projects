package com.taku.safe.protocol.respond;

import java.util.List;

/**
 * Created by colin on 2017/8/15.
 */

public class RespStudSignRecord extends RespBaseBean {

    /**
     * unusualNum : 4
     * unusualDays : ["2017-08-14","2017-08-10","2017-08-06","2017-08-17"]
     * studentName : 琼瑶
     */

    private int unusualNum;
    private String studentName;
    private String className;
    private String studentPhone;
    private String avatarUrl;


    private List<String> unusualDays;

    public int getUnusualNum() {
        return unusualNum;
    }

    public void setUnusualNum(int unusualNum) {
        this.unusualNum = unusualNum;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public List<String> getUnusualDays() {
        return unusualDays;
    }

    public void setUnusualDays(List<String> unusualDays) {
        this.unusualDays = unusualDays;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getStudentPhone() {
        return studentPhone;
    }

    public void setStudentPhone(String studentPhone) {
        this.studentPhone = studentPhone;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
