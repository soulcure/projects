package com.ivmall.android.app.entity;

import java.util.List;

/**
 * Created by koen on 2015/12/22.
 */
public class PrizesInfo {

    private String coupon;  //中奖信息
    private int leftCount;   //剩余次数
    private String selectedCoupon;   //已获得的奖项
    private List<SelectedCouponListEntity> selectedCouponList;  //其他人获得的奖项
    private String title;

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    public void setLeftCount(int leftCount) {
        this.leftCount = leftCount;
    }

    public void setSelectedCoupon(String selectedCoupon) {
        this.selectedCoupon = selectedCoupon;
    }

    public void setSelectedCouponList(List<SelectedCouponListEntity> selectedCouponList) {
        this.selectedCouponList = selectedCouponList;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getCoupon() {
        return coupon;
    }

    public int getLeftCount() {
        return leftCount;
    }

    public String getSelectedCoupon() {
        return selectedCoupon;
    }

    public List<SelectedCouponListEntity> getSelectedCouponList() {
        return selectedCouponList;
    }

    public class SelectedCouponListEntity {
        private String userName;
        private String selectedCoupon;

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public void setSelectedCoupon(String selectedCoupon) {
            this.selectedCoupon = selectedCoupon;
        }

        public String getUserName() {
            return userName;
        }

        public String getSelectedCoupon() {
            return selectedCoupon;
        }
    }
}
