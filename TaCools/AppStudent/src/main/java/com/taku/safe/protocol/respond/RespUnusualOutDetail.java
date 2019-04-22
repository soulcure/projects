package com.taku.safe.protocol.respond;

import java.util.List;

/**
 * Created by colin on 2017/7/10.
 */

public class RespUnusualOutDetail extends RespBaseBean {


    /**
     * studentId : 1
     * name : 陈大牛
     * className : 计本一班
     * phoneNo : 1867678123
     * eventList : [{"eventTime":"2016-01-01 11:22:11","eventDesc":"就寝后出入学校"}]
     */

    private int studentId;
    private String name;
    private String className;
    private String phoneNo;
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

    public List<EventListBean> getEventList() {
        return eventList;
    }

    public void setEventList(List<EventListBean> eventList) {
        this.eventList = eventList;
    }

    public static class EventListBean {
        /**
         * eventTime : 2016-01-01 11:22:11
         * eventDesc : 就寝后出入学校
         */

        private String eventTime;
        private String eventDesc;

        public String getEventTime() {
            return eventTime;
        }

        public void setEventTime(String eventTime) {
            this.eventTime = eventTime;
        }

        public String getEventDesc() {
            return eventDesc;
        }

        public void setEventDesc(String eventDesc) {
            this.eventDesc = eventDesc;
        }
    }
}
