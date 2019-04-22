package com.taku.safe.protocol.respond;

import android.text.TextUtils;

import java.util.List;

/**
 * Created by colin on 2017/7/10.
 */

public class RespIndoorDetail extends RespBaseBean {


    /**
     * studentId : 1
     * name : 陈大牛
     * className : 计本一班
     * phoneNo : 1867678123
     * eventList : [{"startDay":"2016-01-01","endDay":"2016-08-01","days":10,"lastShowDate":"2017-01-01 11:11:11"}]
     */

    private int studentId;
    private String name;
    private String className;
    private String phoneNo;
    private String address;
    private List<EventListBean> eventList;

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<EventListBean> getEventList() {
        return eventList;
    }

    public void setEventList(List<EventListBean> eventList) {
        this.eventList = eventList;
    }

    public static class EventListBean {
        /**
         * startDay : 2016-01-01
         * endDay : 2016-08-01
         * days : 10
         * lastShowDate : 2017-01-01 11:11:11
         */

        private String startDay;
        private String endDay;
        private int days;
        private String lastShowDate;

        public String getRangeDay() {
            if (!TextUtils.isEmpty(startDay) && !TextUtils.isEmpty(endDay)) {
                return startDay + " - " + endDay;
            } else {
                return startDay;
            }
        }


        public String getStartDay() {
            return startDay;
        }

        public void setStartDay(String startDay) {
            this.startDay = startDay;
        }

        public String getEndDay() {
            return endDay;
        }

        public void setEndDay(String endDay) {
            this.endDay = endDay;
        }

        public int getDays() {
            return days;
        }

        public void setDays(int days) {
            this.days = days;
        }

        public String getLastShowDate() {
            return lastShowDate;
        }

        public void setLastShowDate(String lastShowDate) {
            this.lastShowDate = lastShowDate;
        }
    }
}
