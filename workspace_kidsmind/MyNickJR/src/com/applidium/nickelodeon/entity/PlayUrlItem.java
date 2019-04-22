package com.applidium.nickelodeon.entity;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by colin on 2015/5/25.
 */
public class PlayUrlItem implements Parcelable {

    private String lang; // 语言
    private String playUrl; // 播放地址


    public String getLang() {
        return lang;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(lang);
        dest.writeString(playUrl);
    }

    public static final Parcelable.Creator<PlayUrlItem> CREATOR = new Parcelable.Creator<PlayUrlItem>() {
        public PlayUrlItem createFromParcel(Parcel in) {
            return new PlayUrlItem(in);
        }

        public PlayUrlItem[] newArray(int size) {
            return new PlayUrlItem[size];
        }
    };

    private PlayUrlItem(Parcel in) {
        lang = in.readString();
        playUrl = in.readString();
    }
}
