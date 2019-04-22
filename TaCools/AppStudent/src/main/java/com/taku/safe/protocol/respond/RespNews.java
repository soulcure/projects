package com.taku.safe.protocol.respond;

import java.util.List;

/**
 * Created by colin on 2018/1/11.
 */

public class RespNews extends RespBaseBean {

    private PageInfoBean pageInfo;

    public PageInfoBean getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfoBean pageInfo) {
        this.pageInfo = pageInfo;
    }

    public static class PageInfoBean {
        /**
         * articleList : [{"articleId":1,"title":"校园十大美女决出","createTime":"2017-09-11 11:11:20","image":"http://imagecdn/aaa.jpg","readNum":1000}]
         * currPage : 1
         * pageSize : 10
         * totalPage : 100
         * totalCount : 1000
         */

        private int currPage;
        private int pageSize;
        private int totalPage;
        private int totalCount;
        private List<ArticleListBean> articleList;

        public int getCurrPage() {
            return currPage;
        }

        public void setCurrPage(int currPage) {
            this.currPage = currPage;
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

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public List<ArticleListBean> getArticleList() {
            return articleList;
        }

        public void setArticleList(List<ArticleListBean> articleList) {
            this.articleList = articleList;
        }

        public static class ArticleListBean {
            /**
             * articleId : 1
             * title : 校园十大美女决出
             * createTime : 2017-09-11 11:11:20
             * image : http://imagecdn/aaa.jpg
             * readNum : 1000
             */

            private int articleId;
            private String title;
            private String createTime;
            private String image;
            private int readNum;

            public int getArticleId() {
                return articleId;
            }

            public void setArticleId(int articleId) {
                this.articleId = articleId;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getCreateTime() {
                return createTime;
            }

            public void setCreateTime(String createTime) {
                this.createTime = createTime;
            }

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }

            public int getReadNum() {
                return readNum;
            }

            public void setReadNum(int readNum) {
                this.readNum = readNum;
            }
        }
    }
}
