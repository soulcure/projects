package com.applidium.nickelodeon.entity;

import com.applidium.nickelodeon.uitls.ListUtils;
import com.applidium.nickelodeon.uitls.StringUtils;

import java.util.List;

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


    /*下面是协议字段*/
    private int contentId; // 子剧集ID
    private String contentName;
    private int subId;   //总剧集ID
    private String subName;
    private String seriesImg;
    private boolean langSwitch;
    private List<String> rates; //
    private List<PlayUrlItem> langUrl; //
    private List<PlayUrlItem> langUrlFHD; // 新增高清接口 （实际为标清）

    private List<PlayUrlItem> langUrl_spare;   //备用标清播放地址
    private List<PlayUrlItem> langUrlFHD_spare; //备用高清播放地址

    public int getContentId() {
        return contentId;
    }

    public String getContentName() {
        return contentName;
    }

    public String getSubName() {
        return subName;
    }

    public int getSubId() {
        return subId;
    }

    public String getSeriesImg() {
        return seriesImg;
    }

    public boolean isLangSwitch() {
        return langSwitch;
    }

    public List<String> getRates() {
        return rates;
    }

    public List<PlayUrlItem> getLangUrl() {
        return langUrl;
    }

    public List<PlayUrlItem> getLangUrlFHD() {
        return langUrlFHD;
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
     * 获取中文播放地址
     *
     * @return
     */
    public String getPlayUrl(int langIndex) {
        String lang;
        int cur = langIndex % 2;
        if (cur == 1) {
            lang = PlayUrlInfo.ENGLISH;
        } else {
            lang = PlayUrlInfo.CHINESE;
        }

        return getPlayUrl(lang);
    }


    /**
     * 通过语言获取播放地址
     *
     * @param lang
     * @return
     */
    private String getPlayUrl(String lang) {
        String url = null;
        if (!ListUtils.isEmpty(langUrl)) {
            for (PlayUrlItem item : langUrl) {
                if (item.getLang().equals(lang)) {
                    url = item.getPlayUrl();
                    break;
                }
            }
        } else if (!ListUtils.isEmpty(langUrlFHD)) {
            for (PlayUrlItem item : langUrlFHD) {
                if (item.getLang().equals(lang)) {
                    url = item.getPlayUrl();
                    break;
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
    public String getSparePlayUrl(int langIndex) {
        String lang;
        int cur = langIndex % 2;
        if (cur == 1) {
            lang = PlayUrlInfo.ENGLISH;
        } else {
            lang = PlayUrlInfo.CHINESE;
        }

        return getSparePlayUrl(lang);
    }


    /**
     * 获取备用播放地址
     *
     * @return
     */
    private String getSparePlayUrl(String lang) {
        String url = null;
        if (!ListUtils.isEmpty(langUrl_spare)) {
            for (PlayUrlItem item : langUrl_spare) {
                if (item.getLang().equals(lang)) {
                    url = item.getPlayUrl();
                    break;
                }
            }
        } else if (!ListUtils.isEmpty(langUrlFHD_spare)) {
            for (PlayUrlItem item : langUrlFHD_spare) {
                if (item.getLang().equals(lang)) {
                    url = item.getPlayUrl();
                    break;
                }
            }
        }

        if (StringUtils.isEmpty(url)) {
            if (!ListUtils.isEmpty(langUrl_spare)) {
                for (PlayUrlItem item : langUrl_spare) {
                    if (item.getLang().equals(lang)) {
                        url = item.getPlayUrl();
                        break;
                    }
                }
            } else if (!ListUtils.isEmpty(langUrlFHD_spare)) {

                for (PlayUrlItem item : langUrlFHD_spare) {
                    if (item.getLang().equals(lang)) {
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


}
