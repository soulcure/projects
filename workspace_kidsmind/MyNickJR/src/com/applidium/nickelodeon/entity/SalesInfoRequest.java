/**
 *
 */
package com.applidium.nickelodeon.entity;

import com.applidium.nickelodeon.uitls.GsonUtil;

import org.json.JSONException;


/**
 * 促销信息请求参数封装类
 *
 * @author Roseox Hu
 */
public class SalesInfoRequest extends ProtocolRequest {


    private String mLocation = "vipList"; // 位置, 默认vipList
    private String mToken; // 用户标识

    public void setmLocation(String mLocation) {
        this.mLocation = mLocation;
    }

    public void setmToken(String mToken) {
        this.mToken = mToken;
    }

    @Override
    public String toJsonString() {
        return GsonUtil.format(this);
    }


}
