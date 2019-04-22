package com.taku.safe.protocol.respond;

import java.util.List;

/**
 * Created by colin on 2018/1/11.
 */

public class RespEvaluation extends RespBaseBean {


    private List<TestListBean> testList;

    public List<TestListBean> getTestList() {
        return testList;
    }

    public void setTestList(List<TestListBean> testList) {
        this.testList = testList;
    }

    public static class TestListBean {
        /**
         * testId : 1
         * title : 你是变态吗？
         * image : http://oss_url/a.jpg
         * free : 0
         * price : 100
         */

        private int testId;
        private String title;
        private String image;
        private int free;
        private int price;

        public int getTestId() {
            return testId;
        }

        public void setTestId(int testId) {
            this.testId = testId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public int getFree() {
            return free;
        }

        public void setFree(int free) {
            this.free = free;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }
    }
}
