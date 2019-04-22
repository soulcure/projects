package com.ivmall.android.app.entity;

import com.ivmall.android.app.uitls.GsonUtil;

/**
 * Created by colin on 2015/5/25.
 */
public class MainPlayListRequest extends ProtocolRequest {
    public static final String TAB_CARTOON = "cartoon";
    public static final String TAB_SONGS = "songs";
    public static final String TAB_TOYS = "toys";

    public static final String TAB_EARLYEDUCATION = "earlyeducation";  //幼教
    public static final String TAB_ENTERTAINMENT = "entertainment";    //娱乐

    private String token;
    private int rowCount; //默认 4
    private int startIndex;
    private int offset;   //默认 10

    private String category;  //"cartoon","songs"

    public void setToken(String token) {
        this.token = token;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setCategory(String tab) {
        this.category = tab;
    }

    @Override
    public String toJsonString() {
        return GsonUtil.format(this);
    }


}
