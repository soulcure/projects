package com.applidium.nickelodeon.entity;


/**
 * Created by colin on 2015/5/25.
 */
public class AccountInfo {

    private int accountId;
    private String token;
    private NmjProfileInfo profile_list;

    public int getAccountId() {
        return accountId;
    }

    public String getToken() {
        return token;
    }

    public NmjProfileInfo getProfile_list() {
        return profile_list;
    }
}
