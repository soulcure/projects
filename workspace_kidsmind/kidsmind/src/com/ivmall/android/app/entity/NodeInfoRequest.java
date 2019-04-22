package com.ivmall.android.app.entity;

import com.ivmall.android.app.uitls.GsonUtil;

/**
 * Created by colin on 2015/5/25.
 */
public class NodeInfoRequest extends ProtocolRequest {
    private String token;
    private int noteId;


    public void setToken(String token) {
        this.token = token;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    @Override
    public String toJsonString() {
        return GsonUtil.format(this);
    }


}
