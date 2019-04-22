package com.taku.safe.protocol.respond;

import java.util.List;

/**
 * Created by colin on 2017/7/10.
 */

public class RespSosList extends RespBaseBean {

    private PageInfoBean pageInfo;

    public PageInfoBean getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfoBean pageInfo) {
        this.pageInfo = pageInfo;
    }

    public static class PageInfoBean {

        private int totalCount;  //总条数
        private int pageSize; //每页大小
        private int totalPage; //总页数
        private int currPage;  //当前页  默认第1页开始  （请求默认从 第0页开始）

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

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * sosId : 590
             * studentName : 吴建业
             * className : 美院一班
             * sosDate : 2017-10-30 17:10:40
             * sosStatus : 0
             */

            private int sosId;
            private String studentName;
            private String className;
            private String sosDate;
            private int sosStatus;

            public int getSosId() {
                return sosId;
            }

            public void setSosId(int sosId) {
                this.sosId = sosId;
            }

            public String getStudentName() {
                return studentName;
            }

            public void setStudentName(String studentName) {
                this.studentName = studentName;
            }

            public String getClassName() {
                return className;
            }

            public void setClassName(String className) {
                this.className = className;
            }

            public String getSosDate() {
                return sosDate;
            }

            public void setSosDate(String sosDate) {
                this.sosDate = sosDate;
            }

            public int getSosStatus() {
                return sosStatus;
            }

            public void setSosStatus(int sosStatus) {
                this.sosStatus = sosStatus;
            }
        }

        public boolean isLastPage() {
            boolean res = false;
            if (currPage > 0 && currPage == totalPage) {
                res = true;
            }
            return res;
        }
    }
}
