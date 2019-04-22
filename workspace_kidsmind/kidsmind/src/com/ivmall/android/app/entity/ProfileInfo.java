package com.ivmall.android.app.entity;

import java.util.List;

/**
 * Created by colin on 2015/5/25.
 */
public class ProfileInfo {
    private int profileId; // 创建profile 返回
    private String headimg;
    private List<ProfileItem> list; //获取profile 返回


    public int getProfileId() {
        return profileId;
    }

    public String getHeadimg() {
        return headimg;
    }

    public List<ProfileItem> getList() {
        return list;
    }
}
