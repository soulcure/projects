package com.applidium.nickelodeon.entity;


import android.os.Parcel;
import android.os.Parcelable;

import com.applidium.nickelodeon.uitls.ListUtils;
import com.applidium.nickelodeon.uitls.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by colin on 2015/5/25.
 */
public class ContentItem implements Parcelable {


    /*下面是逻辑判断字段*/
    public static final String CHINESE = "zh-cn"; //中文
    public static final String ENGLISH = "en-gb"; //英文


    /*下面是协议字段*/
    private int contentId;
    private String contentImg;
    private String contentName;
    private boolean langSwitch;
    private boolean supportChi;
    private boolean supportEng;
    private int sequence;
    private boolean trial;   //true 代表免费
    private List<PlayUrlItem> langUrl;   //标清播放地址
    private List<PlayUrlItem> langUrlFHD;//高清播放地址

    private List<PlayUrlItem> langUrl_spare;   //备用标清播放地址
    private List<PlayUrlItem> langUrlFHD_spare; //备用高清播放地址


    public int getContentId() {
        return contentId;
    }

    public String getContentImg() {
        return contentImg;
    }

    public String getContentName() {
        return contentName;
    }

    public boolean isLangSwitch() {
        return langSwitch;
    }

    public boolean isSupportChi() {
        return supportChi;
    }

    public boolean isSupportEng() {
        return supportEng;
    }

    public String getSequence() {
        return sequence + "";
    }

    /**
     * true  代表免费
     * false 代表收费
     *
     * @return
     */
    public boolean isTrial() {
        return trial;
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


    public boolean hasHD() {
        boolean res = false;
        if (!ListUtils.isEmpty(langUrlFHD)) {
            res = true;
        }
        return res;
    }






    /**
     * 下面是android 序列号传送对象
     */
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        /*下面是协议字段*/
        dest.writeInt(contentId);
        dest.writeString(contentImg);
        dest.writeString(contentName);
        dest.writeByte((byte) (langSwitch ? 1 : 0));     //if myBoolean == true, byte == 1
        dest.writeByte((byte) (supportChi ? 1 : 0));     //if myBoolean == true, byte == 1
        dest.writeByte((byte) (supportEng ? 1 : 0));     //if myBoolean == true, byte == 1
        dest.writeInt(sequence);
        dest.writeByte((byte) (trial ? 1 : 0));     //if myBoolean == true, byte == 1
        dest.writeTypedList(langUrl);
        dest.writeTypedList(langUrlFHD);
        dest.writeTypedList(langUrl_spare);  //新增备用播放地址
        dest.writeTypedList(langUrlFHD_spare);

    }

    public static final Parcelable.Creator<ContentItem> CREATOR = new Parcelable.Creator<ContentItem>() {
        public ContentItem createFromParcel(Parcel in) {
            return new ContentItem(in);
        }

        public ContentItem[] newArray(int size) {
            return new ContentItem[size];
        }
    };

    private ContentItem(Parcel in) {
        /*下面是协议字段*/
        contentId = in.readInt();
        contentImg = in.readString();
        contentName = in.readString();

        if (in.readByte() == 1) {
            langSwitch = true;
        } else {
            langSwitch = false;
        }
        if (in.readByte() == 1) {
            supportChi = true;
        } else {
            supportChi = false;
        }
        if (in.readByte() == 1) {
            supportEng = true;
        } else {
            supportEng = false;
        }

        sequence = in.readInt();
        if (in.readByte() == 1) {
            trial = true;
        } else {
            trial = false;
        }

        langUrl = new ArrayList<PlayUrlItem>();
        in.readTypedList(langUrl, PlayUrlItem.CREATOR);

        langUrlFHD = new ArrayList<PlayUrlItem>();
        in.readTypedList(langUrlFHD, PlayUrlItem.CREATOR);

        langUrl_spare = new ArrayList<PlayUrlItem>();
        in.readTypedList(langUrl_spare, PlayUrlItem.CREATOR);

        langUrlFHD_spare = new ArrayList<PlayUrlItem>();
        in.readTypedList(langUrlFHD_spare, PlayUrlItem.CREATOR);

    }
    /**上面是android 序列号传送对象*/
}
