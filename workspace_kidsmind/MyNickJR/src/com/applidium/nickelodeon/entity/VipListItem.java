package com.applidium.nickelodeon.entity;


import com.applidium.nickelodeon.config.AppConfig;

/**
 * Created by john on 2015/5/29.
 */
public class VipListItem {

    /**
     * 现网 0.01元 商品ID
     */
    private static final String TEST_GUID_ONLINE = "daad3f4a-61de-4342-97a4-88fdb4e1094b";

    private String vipGuid;
    private String vipTitle;
    private String vipDescription;

    private double listprice;   //原价
    private double price;   //现价

    private String partnerProductId;


    private double preferential; //优惠的金额
    private double preferentialPrice;  //优惠后的价格


    public String getVipTitle() {
        return vipTitle;
    }

    public void setVipTitle(String vipTitle) {
        this.vipTitle = vipTitle;
    }

    public String getVipGuid() {
        /**
         * 是否打开现网测试 0.01元
         */
        if (AppConfig.PAY_TEST_ONLINE) {
            return TEST_GUID_ONLINE;
        } else {
            return vipGuid;
        }
    }

    public void setVipGuid(String vipGuid) {
        this.vipGuid = vipGuid;
    }

    public String getPartnerProductId() {
        return partnerProductId;
    }

    public void setPartnerProductId(String partnerProductId) {
        this.partnerProductId = partnerProductId;
    }

    public String getPrice() {
        String res;
        if (preferential != 0) {  //如果优惠价格不等于 0
            price = preferentialPrice;
        }

        if (price % 1.0 == 0) {
            res = "¥" + (int) price;
        } else {
            res = "¥" + price;
        }

        /**
         * 是否打开现网测试 0.01元
         */
        if (AppConfig.PAY_TEST_ONLINE) {
            res = "¥" + 0.01;
        }
        return res;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getVipDescription() {
        return vipDescription;
    }

    public void setVipDescription(String vipDescription) {
        this.vipDescription = vipDescription;
    }

    public String getListprice() {
        if (listprice % 1.0 == 0) {
            return "¥" + (int) listprice;
        } else {
            return "¥" + listprice;
        }
    }

    public double getpriceDouble() {
        /**
         * 是否打开现网测试 0.01元
         */
        if (AppConfig.PAY_TEST_ONLINE) {
            return 0.01;
        } else {
            return price;
        }
    }

    public void setListprice(double listprice) {
        this.listprice = listprice;
    }


}
