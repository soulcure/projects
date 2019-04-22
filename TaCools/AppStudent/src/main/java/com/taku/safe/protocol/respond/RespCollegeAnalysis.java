package com.taku.safe.protocol.respond;

import java.util.List;

/**
 * Created by colin on 2017/8/11.
 */

public class RespCollegeAnalysis extends RespBaseBean {


    private List<UnusualListBean> unusualList;
    private List<CollegeListBean> collegeList;

    public List<UnusualListBean> getUnusualList() {
        return unusualList;
    }

    public void setUnusualList(List<UnusualListBean> unusualList) {
        this.unusualList = unusualList;
    }

    public List<CollegeListBean> getCollegeList() {
        return collegeList;
    }

    public void setCollegeList(List<CollegeListBean> collegeList) {
        this.collegeList = collegeList;
    }

    public static class UnusualListBean {
        /**
         * day : 2017-08-02
         * num : 3
         */

        private String day;
        private int num;

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }
    }

    public static class CollegeListBean {
        /**
         * collegeId : 2
         * collegeName : 美术学院
         * unusualNum : 2
         */

        private int collegeId;
        private String collegeName;
        private int unusualNum;

        public int getCollegeId() {
            return collegeId;
        }

        public void setCollegeId(int collegeId) {
            this.collegeId = collegeId;
        }

        public String getCollegeName() {
            return collegeName;
        }

        public void setCollegeName(String collegeName) {
            this.collegeName = collegeName;
        }

        public int getUnusualNum() {
            return unusualNum;
        }

        public void setUnusualNum(int unusualNum) {
            this.unusualNum = unusualNum;
        }
    }
}
