package com.applidium.nickelodeon.entity;

/**
 * Created by Markry on 2016/1/8.
 */
public class CodeOpenVipResponse {
    public int errorCode; // 错误类型
    public String errorMessage; // 错误信息

    public vipRspData data; // 详细信息

    public boolean isSucess() {
        return errorCode == 0 ? true : false;
    }

    public String getMessage() {
        return errorMessage;
    }

    public vipRspData getData() {
        return data;
    }

    public class vipRspData {
        private String buyTime;
        private String vipExpiryTime;
        private String vipExpiryTip;
        private String vipGuid;
        private String vipTitle;
        private String vipImg;
        private String vipDescription;
        private String trial;
        private String points;

        public String getBuyTime() {
            return buyTime;
        }

        public void setBuyTime(String buyTime) {
            this.buyTime = buyTime;
        }

        public String getTrial() {
            return trial;
        }

        public void setTrial(String trial) {
            this.trial = trial;
        }

        public String getPoints() {
            return points;
        }

        public void setPoints(String points) {
            this.points = points;
        }

        public String getVipExpiryTime() {
            return vipExpiryTime;
        }

        public void setVipExpiryTime(String vipExpiryTime) {
            this.vipExpiryTime = vipExpiryTime;
        }

        public String getVipExpiryTip() {
            return vipExpiryTip;
        }

        public void setVipExpiryTip(String vipExpiryTip) {
            this.vipExpiryTip = vipExpiryTip;
        }

        public String getVipGuid() {
            return vipGuid;
        }

        public void setVipGuid(String vipGuid) {
            this.vipGuid = vipGuid;
        }

        public String getVipTitle() {
            return vipTitle;
        }

        public void setVipTitle(String vipTitle) {
            this.vipTitle = vipTitle;
        }

        public String getVipImg() {
            return vipImg;
        }

        public void setVipImg(String vipImg) {
            this.vipImg = vipImg;
        }

        public String getVipDescription() {
            return vipDescription;
        }

        public void setVipDescription(String vipDescription) {
            this.vipDescription = vipDescription;
        }
    }

}
