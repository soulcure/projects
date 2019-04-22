/**
 *
 */
package com.applidium.nickelodeon.entity;


/**
 * 支付 接口响应结果封装基础类
 *
 * @author Roseox Hu
 */
public class SmartRecommendResponse {

    public int errorCode; // 错误类型
    public String errorMessage; // 错误信息

    public RecommendInfo data; // 详细信息


    public boolean isSucess() {
        return errorCode == 0 ? true : false;
    }

    public RecommendInfo getData() {
        return data;
    }

    public String getMessage() {
        return errorMessage;
    }
}
