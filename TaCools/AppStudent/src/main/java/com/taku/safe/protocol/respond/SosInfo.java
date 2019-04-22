package com.taku.safe.protocol.respond;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * Created by soulcure on 2017/8/5.
 */

public class SosInfo implements Parcelable {

    /**
     * lng : 113.945263
     * sosId : 45
     * lat : 22.527865
     * pushType : sos_new
     */
    private int sosId;
    private double lng;
    private double lat;

    //report_reply 校长直通车已回复 （学生端收）
    //internship_sign_remind 实习打卡提醒（学生端收）
    //rest_sign_remind  作息打卡提醒（学生端收）
    //sos_new  新建SOS           （老师端收）
    //sos_stop sos已停止         （老师端收）
    //public_notice 公告通知信息

    private String pushType;

    private String title;

    private String pushTime;


    public boolean isRestSignRemind() {  //作息打卡提醒
        return !TextUtils.isEmpty(pushType) && pushType.equals("rest_sign_remind");
    }

    public boolean isInternshipSignRemind() {  //实习打卡提醒
        return !TextUtils.isEmpty(pushType) && pushType.equals("internship_sign_remind");
    }

    public boolean isReportReply() {  //校长直通车已回复
        return !TextUtils.isEmpty(pushType) && pushType.equals("report_reply");
    }

    public boolean isSos() {
        return !TextUtils.isEmpty(pushType) && (pushType.equals("sos_new") || pushType.equals("sos_stop"));
    }

    public boolean isNotice() {  //公告消息
        return !TextUtils.isEmpty(pushType) && pushType.equals("public_notice");
    }

    public int getSosId() {
        return sosId;
    }

    public void setSosId(int sosId) {
        this.sosId = sosId;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getPushType() {
        return pushType;
    }

    public void setPushType(String pushType) {
        this.pushType = pushType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPushTime() {
        return pushTime;
    }

    public void setPushTime(String pushTime) {
        this.pushTime = pushTime;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.sosId);
        dest.writeDouble(this.lng);
        dest.writeDouble(this.lat);
        dest.writeString(this.pushType);
        dest.writeString(this.title);
        dest.writeString(this.pushTime);
    }

    public SosInfo() {
    }

    protected SosInfo(Parcel in) {
        this.sosId = in.readInt();
        this.lng = in.readDouble();
        this.lat = in.readDouble();
        this.pushType = in.readString();
        this.title = in.readString();
        this.pushTime = in.readString();
    }

    public static final Creator<SosInfo> CREATOR = new Creator<SosInfo>() {
        @Override
        public SosInfo createFromParcel(Parcel source) {
            return new SosInfo(source);
        }

        @Override
        public SosInfo[] newArray(int size) {
            return new SosInfo[size];
        }
    };
}
