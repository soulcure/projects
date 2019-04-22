package com.ivmall.android.app.entity;

/**
 * Created by colin on 2016/4/8.
 */
public class BBSItem {
    private int noteId;
    private String imgUrl;
    private int commentCount;
    private String userName;
    private String publishTime;
    private String summary;
    private int userId;


    public int getNoteId() {
        return noteId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public String getUserName() {
        return userName;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public String getSummary() {
        return summary;
    }

    public int getUserId() {
        return userId;
    }
}
