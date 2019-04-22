package com.taku.safe.protocol.respond;

import java.util.List;

/**
 * Created by colin on 2017/7/10.
 */

public class RespNoConnectList extends RespBaseBean {

    private PageInfoBean pageInfo;

    public PageInfoBean getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfoBean pageInfo) {
        this.pageInfo = pageInfo;
    }

    public static class PageInfoBean {

        private int totalCount;
        private int pageSize;
        private int totalPage;
        private int currPage;
        private List<ListBean> list;

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getTotalPage() {
            return totalPage;
        }

        public void setTotalPage(int totalPage) {
            this.totalPage = totalPage;
        }

        public int getCurrPage() {
            return currPage;
        }

        public void setCurrPage(int currPage) {
            this.currPage = currPage;
        }

        public boolean isLastPage() {
            boolean res = false;
            if (currPage > 0 && currPage == totalPage) {
                res = true;
            }
            return res;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * id : 1
             * name : 李春风
             * avatar : http://taku-dev.oss-cn-shenzhen.aliyuncs.com/avata/20170722/abe0b6398cef4e9fae62eae3d3280952.jpg
             * classId : 1
             * className : 美院一班
             * phoneNo : 18676786434
             * lastSignTime : 2017-09-20 00:00:00
             * lastShowTime : 2017-08-28 18:01:24
             * lastShowAddr : 北京大学
             * day : 89
             */

            private int id;
            private String name;
            private String avatar;
            private int classId;
            private String className;
            private String phoneNo;
            private String lastSignTime;
            private String lastShowTime;
            private String lastShowAddr;
            private int day;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getAvatar() {
                return avatar;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
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

            public String getLastSignTime() {
                return lastSignTime;
            }

            public void setLastSignTime(String lastSignTime) {
                this.lastSignTime = lastSignTime;
            }

            public String getLastShowTime() {
                return lastShowTime;
            }

            public void setLastShowTime(String lastShowTime) {
                this.lastShowTime = lastShowTime;
            }

            public String getLastShowAddr() {
                return lastShowAddr;
            }

            public void setLastShowAddr(String lastShowAddr) {
                this.lastShowAddr = lastShowAddr;
            }

            public int getDay() {
                return day;
            }

            public void setDay(int day) {
                this.day = day;
            }
        }
    }
}
