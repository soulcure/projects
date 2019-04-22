package com.taku.safe.protocol.respond;


import java.util.List;

/**
 * Created by colin on 2017/6/6.
 */

public class RespSosDetail extends RespBaseBean {

    private double longitude;
    private double latitude;
    private String studentName;
    private String studentPhone;
    private String sosDate;
    private String address;
    private List<VoliceListBean> voliceList;


    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentPhone() {
        return studentPhone;
    }

    public void setStudentPhone(String studentPhone) {
        this.studentPhone = studentPhone;
    }

    public String getSosDate() {
        return sosDate;
    }

    public void setSosDate(String sosDate) {
        this.sosDate = sosDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<VoliceListBean> getVoliceList() {
        return voliceList;
    }

    public void setVoliceList(List<VoliceListBean> voliceList) {
        this.voliceList = voliceList;
    }

    public static class VoliceListBean {
        /**
         * date : 2017-11-11 11:11:11
         * voiceUrl : http: //xxxx/abc.rm
         */

        private String date;
        private String voiceUrl;
        private int duration;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getVoiceUrl() {
            return voiceUrl;
        }

        public void setVoiceUrl(String voiceUrl) {
            this.voiceUrl = voiceUrl;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }
    }
}
