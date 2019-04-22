/**
 *
 */
package com.applidium.nickelodeon.entity;


public class NmjLoginResponse {

    public AccountInfo account; // 详细信息


    public String getToken() {
        return account.getToken();
    }

}
