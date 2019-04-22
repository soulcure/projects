package com.ivmall.android.app.entity;

import android.content.Context;

import com.ivmall.android.app.uitls.AppUtils;
import com.ivmall.android.app.uitls.ListUtils;
import com.ivmall.android.app.uitls.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by colin on 2015/5/25.
 */
public class PlayUrlInfo {

    /*下面是逻辑判断字段*/
    public static final String CHINESE = "zh-cn"; //中文
    public static final String ENGLISH = "en-gb"; //英文
    public static final String FHD = "fhd";       //全高清
    public static final String HD = "hd";         //高清

    private String mLang = CHINESE;
    private String mQuality = FHD;


    /*下面是协议字段*/
    private int profileId; //
    private String episodeName;
    private int serieId;
    private String serieName;
    private int prologue;
    private List<String> preferences; //
    private List<PlayUrlItem> langUrl; //
    private List<PlayUrlItem> langUrlFHD; // 新增高清接口 （实际为标清）

    private List<PlayUrlItem> langUrl_spare;   //备用标清播放地址
    private List<PlayUrlItem> langUrlFHD_spare; //备用高清播放地址


    private List<String> rates; //


    // 2016.2.25新增导购
    private boolean hasShopping;  //是否有商城链接
    private String shoppingUrl;  // 商城购买
    private String adImgUrl;  //广告图片
    private String pauseImgUrl;  // 暂停图片
    private List<Map<String, Integer>> adTimeInfo; // 广告投放时间
    private boolean canShare;  // 是否可以分享
    private String shareUrl;  //微信分享Url

    private int behaviorPlayId;
    private boolean isSkip;
    

    public String getShoppingUrl() {
        return shoppingUrl;
    }

    public String getAdImgUrl() {
        return adImgUrl;
    }

    public String getPauseImgUrl() {
        return pauseImgUrl;
    }

    public List<Map<String, Integer>> getAdTimeInfo() {
        return adTimeInfo;
    }


    public String getShareUrl() {
        return shareUrl;
    }


    public boolean isHasShopping() {
        return hasShopping;
    }

    public boolean isCanShare() {
        return canShare;
    }

    public int getBehaviorPlayId() {
        return behaviorPlayId;
    }

    public boolean isSkip() {
        return isSkip;
    }

    /**
     * 设置语言
     *
     * @param lang
     */
    public void setLang(String lang) {
        this.mLang = lang;
    }

    /**
     * 设置语言
     *
     * @param index 偶数 表示 中文
     *              奇数 表示 英文
     */
    public void setLang(int index) {
        String lang = PlayUrlInfo.CHINESE;
        int cur = index % 2;
        if (cur == 1) {
            lang = PlayUrlInfo.ENGLISH;
        }
        setLang(lang);
    }


    /**
     * 设置播放片源质量
     *
     * @param quality
     */
    public void setQuality(Context context, String quality) {
        this.mQuality = quality;
        AppUtils.setStringSharedPreferences(context, "Quality", quality);
    }


    public int getProfileId() {
        return profileId;
    }

    public String getEpisodeName() {
        return episodeName;
    }

    public int getSerieId() {
        return serieId;
    }

    public String getSerieName() {
        return serieName;
    }

    public List<String> getPreferences() {
        return preferences;
    }

    public List<PlayUrlItem> getLangUrl() {
        return langUrl;
    }

    public List<String> getRates() {
        return rates;
    }

    public int getPrologue() {
        return prologue;
    }


    public String getPlayUrl() {
        String url = null;
        if (!ListUtils.isEmpty(langUrl) && mQuality.equals(HD)) {
            for (PlayUrlItem item : langUrl) {
                if (item.getLang().equals(mLang)) {
                    url = item.getPlayUrl();
                    break;
                }
            }
        } else if (!ListUtils.isEmpty(langUrlFHD) && mQuality.equals(FHD)) {
            for (PlayUrlItem item : langUrlFHD) {
                if (item.getLang().equals(mLang)) {
                    url = item.getPlayUrl();
                    break;
                }
            }
        }

        if (StringUtils.isEmpty(url)) {
            if (!ListUtils.isEmpty(langUrl)) {
                for (PlayUrlItem item : langUrl) {
                    if (item.getLang().equals(mLang)) {
                        url = item.getPlayUrl();
                        break;
                    }
                }
            } else if (!ListUtils.isEmpty(langUrlFHD)) {

                for (PlayUrlItem item : langUrlFHD) {
                    if (item.getLang().equals(mLang)) {
                        url = item.getPlayUrl();
                        break;
                    }
                }
            }
        }

        if (StringUtils.isEmpty(url)) {
            if (!ListUtils.isEmpty(langUrl)) {
                url = langUrl.get(0).getPlayUrl();
            } else if (!ListUtils.isEmpty(langUrlFHD)) {
                url = langUrlFHD.get(0).getPlayUrl();
            }
        }

        return url;
    }


    /**
     * 获取备用播放地址
     *
     * @return
     */
    public String getSparePlayUrl() {
        String url = null;
        if (!ListUtils.isEmpty(langUrl_spare) && mQuality.equals(HD)) {
            for (PlayUrlItem item : langUrl_spare) {
                if (item.getLang().equals(mLang)) {
                    url = item.getPlayUrl();
                    break;
                }
            }
        } else if (!ListUtils.isEmpty(langUrlFHD_spare) && mQuality.equals(FHD)) {
            for (PlayUrlItem item : langUrlFHD_spare) {
                if (item.getLang().equals(mLang)) {
                    url = item.getPlayUrl();
                    break;
                }
            }
        }

        if (StringUtils.isEmpty(url)) {
            if (!ListUtils.isEmpty(langUrl_spare)) {
                for (PlayUrlItem item : langUrl_spare) {
                    if (item.getLang().equals(mLang)) {
                        url = item.getPlayUrl();
                        break;
                    }
                }
            } else if (!ListUtils.isEmpty(langUrlFHD_spare)) {

                for (PlayUrlItem item : langUrlFHD_spare) {
                    if (item.getLang().equals(mLang)) {
                        url = item.getPlayUrl();
                        break;
                    }
                }
            }
        }

        if (StringUtils.isEmpty(url)) {
            if (!ListUtils.isEmpty(langUrl_spare)) {
                url = langUrl_spare.get(0).getPlayUrl();
            } else if (!ListUtils.isEmpty(langUrlFHD_spare)) {
                url = langUrlFHD_spare.get(0).getPlayUrl();
            }
        }

        return url;
    }


    public boolean hasEnglish() {
        boolean res = false;
        if (!ListUtils.isEmpty(langUrl)
                && langUrl.size() > 1) {
            res = true;
        } else if (!ListUtils.isEmpty(langUrlFHD)
                && langUrlFHD.size() > 1) {
            res = true;
        }
        return res;
    }


    public boolean hasHD() {
        boolean res = false;
        if (!ListUtils.isEmpty(langUrlFHD)) {
            res = true;
        }
        return res;
    }


}
