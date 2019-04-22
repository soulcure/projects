package com.taku.safe.protocol.respond;

import java.util.List;

/**
 * Created by colin on 2017/7/10.
 */

public class RespSignRestDataList extends RespBaseBean {

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
             * id : 2564
             * name : 金城武
             * classId : 42
             * className : 计算机一班
             * phoneNo :
             * signId : 22384
             */

            private int id;
            private String name;
            private int classId;
            private String className;
            private String phoneNo;
            private int signId;

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

            public int getSignId() {
                return signId;
            }

            public void setSignId(int signId) {
                this.signId = signId;
            }
        }
    }
}
