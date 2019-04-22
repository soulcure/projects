package com.ivmall.android.app.entity;


/**
 * Created by john on 2015/5/29.
 */
public class VipListItem {

    private String vipName;
    private String vipDesc;

    private double listPrice;   //原价
    private double vipPrice;   //现价

    private String vipGuid;
    private int vipExpireHours;
    private String vipExpireDesc;
    private String vipImg;


    private double preferential;
    private double preferentialPrice;

    /*大麦支付 产品序列号*/
    private String partnerProductId;


    public String getVipName() {
        return vipName;
    }

    public String getVipDesc() {
        return vipDesc;
    }

    public double getVipPrice() {
        return vipPrice;
    }

    public String getPreferentialStr() {
        if (preferential % 1.0 == 0) {
            return "¥" + (int) preferential;
        } else {
            return "¥" + preferential;
        }
    }

    public double getPreferential() {
        return preferential;
    }

    public String getPreferentialPriceStr() {
        if (preferentialPrice % 1.0 == 0) {
            return "¥" + (int) preferentialPrice;
        } else {
            return "¥" + preferentialPrice;
        }
    }

    public double getPreferentialPrice() {
        return preferentialPrice;
    }

    public boolean isShowTag() {
        boolean res = false;
        if (listPrice > vipPrice) {
            res = true;
        }
        return res;
    }

    public String getVipPriceStr() {
        if (vipPrice % 1.0 == 0) {
            return "¥" + (int) vipPrice;
        } else {
            return "¥" + vipPrice;
        }
    }

    public String getListPriceStr() {
        String price = "";
        if (listPrice > vipPrice) {
            if (listPrice % 1.0 == 0) {
                price = "¥" + (int) listPrice;
            } else {
                price = "¥" + listPrice;
            }
        }
        return price;
    }


    public double getListPrice() {
        return listPrice;
    }

    public String getVipGuid() {
        return vipGuid;
    }

    public int getVipExpireHours() {
        return vipExpireHours;
    }

    public String getVipExpireDesc() {
        return vipExpireDesc;
    }

    public String getVipImg() {
        return vipImg;
    }

    public String getPartnerProductId() {
        return partnerProductId;
    }
}
