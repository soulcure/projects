package com.taku.safe.protocol.respond;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by colin on 2017/7/10.
 */

public class RespReportList extends RespBaseBean {

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

            private int reportId;
            private String title;
            private String reportDate;
            private int state;
            private String responseContent;
            private String responseDate;
            private int issueType;
            private String content;
            private String image1;
            private String image2;
            private String image3;
            private String image4;
            private String image5;
            private String reportStudName;

            public int getReportId() {
                return reportId;
            }

            public void setReportId(int reportId) {
                this.reportId = reportId;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getReportDate() {
                return reportDate;
            }

            public void setReportDate(String reportDate) {
                this.reportDate = reportDate;
            }

            public int getState() {
                return state;
            }

            public void setState(int state) {
                this.state = state;
            }

            public String getResponseContent() {
                return responseContent;
            }

            public void setResponseContent(String responseContent) {
                this.responseContent = responseContent;
            }

            public String getResponseDate() {
                return responseDate;
            }

            public void setResponseDate(String responseDate) {
                this.responseDate = responseDate;
            }

            public int getIssueType() {
                return issueType;
            }

            public void setIssueType(int issueType) {
                this.issueType = issueType;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }


            public List<String> getImageList() {
                List<String> list = new ArrayList<>();
                if (!TextUtils.isEmpty(image1)) {
                    list.add(image1);
                }
                if (!TextUtils.isEmpty(image2)) {
                    list.add(image2);
                }
                if (!TextUtils.isEmpty(image3)) {
                    list.add(image3);
                }
                if (!TextUtils.isEmpty(image4)) {
                    list.add(image4);
                }
                if (!TextUtils.isEmpty(image5)) {
                    list.add(image5);
                }
                return list;
            }

            public String getReportStudName() {
                return reportStudName;
            }

            public void setReportStudName(String reportStudName) {
                this.reportStudName = reportStudName;
            }
        }
    }
}
