package com.ivmall.android.app.entity;

/**
 * Created by colin on 2016/4/11.
 */
public class NodeComment {

    private String userName;
    private int userId;
    private String description;
    private String imgUrl;
    private String reviewTime;
    private boolean isOwn;
    private int toReplayUserId;
    private int sequence;


    public String getUserName() {
        return userName;
    }

    public int getUserId() {
        return userId;
    }

    public String getDescription() {
        return description;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getReviewTime() {
        return reviewTime;
    }

    public boolean isOwn() {
        return isOwn;
    }

    public int getToReplayUserId() {
        return toReplayUserId;
    }

    public int getSequence() {
        return sequence;
    }
}
