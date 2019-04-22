package com.taku.safe.protocol.respond;

import java.util.List;

/**
 * Created by colin on 2017/7/10.
 */

public class RespUnusualOutList extends RespBaseBean {

    private PageInfoBean pageInfo;

    public PageInfoBean getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfoBean pageInfo) {
        this.pageInfo = pageInfo;
    }

    public static class PageInfoBean {

        private int currPage;
        private int totalPage;
        private int pageSize;
        private int totalCount;
        private List<StudentlistBean> list;

        public int getCurrPage() {
            return currPage;
        }

        public void setCurrPage(int currPage) {
            this.currPage = currPage;
        }

        public int getTotalPage() {
            return totalPage;
        }

        public void setTotalPage(int totalPage) {
            this.totalPage = totalPage;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public boolean isLastPage() {
            boolean res = false;
            if (currPage > 0 && currPage == totalPage) {
                res = true;
            }
            return res;
        }

        public List<StudentlistBean> getList() {
            return list;
        }

        public void setList(List<StudentlistBean> list) {
            this.list = list;
        }

        public static class StudentlistBean {
            /**
             * studentId : 1
             * avatar : http://host/abc.jpg
             * studentName : 陈小牛
             * classId : 1
             * className : 计本一班
             * phoneNo : 1867678123
             * times : 8
             */

            private int studentId;
            private String avatar;
            private String studentName;
            private int classId;
            private String className;
            private String phoneNo;
            private int times;

            public int getStudentId() {
                return studentId;
            }

            public void setStudentId(int studentId) {
                this.studentId = studentId;
            }

            public String getAvatar() {
                return avatar;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }

            public String getStudentName() {
                return studentName;
            }

            public void setStudentName(String studentName) {
                this.studentName = studentName;
            }

            public int getClassId() {
                return classId;
            }

            public void setClassId(int classId) {
                this.classId = classId;
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

            public int getTimes() {
                return times;
            }

            public void setTimes(int times) {
                this.times = times;
            }
        }
    }
}
