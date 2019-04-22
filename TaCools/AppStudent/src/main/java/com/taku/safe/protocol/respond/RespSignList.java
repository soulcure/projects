package com.taku.safe.protocol.respond;

import java.util.List;

/**
 * Created by colin on 2017/7/10.
 */

public class RespSignList extends RespBaseBean {


    private List<SignlistBean> signlist;

    public List<SignlistBean> getSignlist() {
        return signlist;
    }

    public void setSignlist(List<SignlistBean> signlist) {
        this.signlist = signlist;
    }


    public String signNormalCount() {
        int res = 0;
        if (signlist != null) {
            for (SignlistBean item : signlist) {
                if (item.getSignValid() == 1) {
                    res++;
                }

            }
        }

        return res + "";
    }

    public String signUnusualCount() {
        int res = 0;
        if (signlist != null) {
            for (SignlistBean item : signlist) {
                if (item.getSignValid() == 0) {
                    res++;
                }

            }
        }
        return res + "";
    }


    public static class SignlistBean {
        /**
         * signId : 4
         * studentId : 3
         * signDate : 2017-07-22 17:13:47
         * lat : 22.60351
         * lng : 113.850841
         * location : 广东省深圳市宝安区固戍二路72号靠近向日葵艺术培训中心(富通·瑞翔居西南)
         * locateMode : 0
         * mapType : 0
         * note : 123456789
         * distance : 5
         * validDistance : 200
         * signValid : 1
         */

        private int signId;
        private int studentId;
        private String signDate;
        private double lat;
        private double lng;
        private String location;
        private int locateMode;
        private int mapType;
        private String note;
        private int distance;
        private int validDistance;
        private int signValid;
        private int changeStatus;
        private String changeTime;

        public int getSignId() {
            return signId;
        }

        public void setSignId(int signId) {
            this.signId = signId;
        }

        public int getStudentId() {
            return studentId;
        }

        public void setStudentId(int studentId) {
            this.studentId = studentId;
        }

        public String getSignDate() {
            return signDate;
        }

        public void setSignDate(String signDate) {
            this.signDate = signDate;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public int getLocateMode() {
            return locateMode;
        }

        public void setLocateMode(int locateMode) {
            this.locateMode = locateMode;
        }

        public int getMapType() {
            return mapType;
        }

        public void setMapType(int mapType) {
            this.mapType = mapType;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public int getDistance() {
            return distance;
        }

        public void setDistance(int distance) {
            this.distance = distance;
        }

        public int getValidDistance() {
            return validDistance;
        }

        public void setValidDistance(int validDistance) {
            this.validDistance = validDistance;
        }

        public int getSignValid() {
            return signValid;
        }

        public void setSignValid(int signValid) {
            this.signValid = signValid;
        }

        public boolean isChangeStatus() {
            return changeStatus == 1;
        }

        public void setChangeStatus(int changeStatus) {
            this.changeStatus = changeStatus;
        }

        public String getChangeTime() {
            return changeTime;
        }

        public void setChangeTime(String changeTime) {
            this.changeTime = changeTime;
        }
    }
}
