
package com.mykj.andr.model;

import com.mykj.game.utils.Util;

import java.io.Serializable;


/**
 * *****************************************
 *
 * @文件名称    : ChatMsgEntity.java
 * @文件描述    : 消息实体
 * *****************************************
 */
public class ChatMsgEntity {
    /**
     * 数据库中编号
     */
    private String mId;

    /**
     * 用户标识
     */
    private String mUid;

    /**
     * 游戏标识
     */
    private String mGameId;

    /**
     * 留言内容
     */
    private String mContent;

    /**
     * 提交时间
     */
    private String mReferTime;

    /**
     * 操作员
     */
    private String mOperator;


    /**
     * 回复
     */
    private String mReply;


    /**
     * 操作时间
     */
    private String mOperateTime;


    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public String getUid() {
        return mUid;
    }

    public void setUid(String mUid) {
        this.mUid = mUid;
    }

    public String getGameId() {
        return mGameId;
    }

    public void setGameId(String mGameId) {
        this.mGameId = mGameId;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String mContent) {
        this.mContent = mContent;
    }

    public String getReferTime() {
        return mReferTime;
    }

    public void setReferTime(String mReferTime) {
        this.mReferTime = mReferTime;
    }

    public String getOperator() {
        return mOperator;
    }

    public void setOperator(String mOperator) {
        this.mOperator = mOperator;
    }

    public String getReply() {

        if (Util.isEmptyStr(mReply)) {
            String res = "客服还没有回复您...";
            return res;
        }

        return mReply;
    }

    public void setReply(String mReply) {
        this.mReply = mReply;
    }

    public String getOperateTime() {
        return mOperateTime;
    }

    public void setOperateTime(String mOperateTime) {
        this.mOperateTime = mOperateTime;
    }
}
