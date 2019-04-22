package com.ivmall.android.app.entity;


/**
 *
 */
public class ControlResponse {

    public enum ControlType {  //控制端类型
        close, remoteSend, confirm
    }

    private String action;
    private ControlInfo custom_content;
    private String description;
    private String rightButton;
    private int sequence;
    private int serieId;
    private String title;
    private String token;


    public String getAction() {
        return action;
    }

    public ControlType getCustomContent() {
        return custom_content.getAction();
    }

    public String getDescription() {
        return description;
    }

    public String getRightButton() {
        return rightButton;
    }

    public int getSequence() {
        return sequence;
    }

    public int getSerieId() {
        return serieId;
    }

    public String getTitle() {
        return title;
    }

    public String getToken() {
        return token;
    }
}
