package com.taku.safe.protocol.respond;

import java.util.List;

/**
 * Created by colin on 2017/8/15.
 */

public class RespStudentList extends RespBaseBean {
    /**
     * msg : success
     * code : 0
     * ts : 1496563665079
     * studentList : [{"studentName":"李三","studentId":1,"unusualNum":10,"unusualDays":["2010-01-01"]}]
     */

    private List<StudentListBean> studentList;

    public List<StudentListBean> getStudentList() {
        return studentList;
    }

    public void setStudentList(List<StudentListBean> studentList) {
        this.studentList = studentList;
    }

    public static class StudentListBean {
        /**
         * studentName : 李三
         * studentId : 1
         * unusualNum : 10
         * unusualDays : ["2010-01-01"]
         */

        private String studentName;
        private int studentId;
        private int unusualNum;
        private List<String> unusualDays;

        public String getStudentName() {
            return studentName;
        }

        public void setStudentName(String studentName) {
            this.studentName = studentName;
        }

        public int getStudentId() {
            return studentId;
        }

        public void setStudentId(int studentId) {
            this.studentId = studentId;
        }

        public int getUnusualNum() {
            return unusualNum;
        }

        public void setUnusualNum(int unusualNum) {
            this.unusualNum = unusualNum;
        }

        public List<String> getUnusualDays() {
            return unusualDays;
        }

        public void setUnusualDays(List<String> unusualDays) {
            this.unusualDays = unusualDays;
        }
    }
}
