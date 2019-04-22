package com.applidium.nickelodeon.entity;

import com.applidium.nickelodeon.uitls.ListUtils;

import java.util.List;

/**
 * Created by colin on 2015/5/25.
 */
public class ProfileInfo {
    private List<ProfileItem> list; //获取profile 返回


    public int getProfileId() {
        int profileId = 0;
        if (!ListUtils.isEmpty(list)) {
            profileId = list.get(0).getId();
        }
        return profileId;
    }

    public List<ProfileItem> getList() {
        return list;
    }
}
