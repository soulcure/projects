package com.ivmall.android.app.entity;

import java.util.List;

/**
 * Created by colin on 2015/5/25.
 */
public class SerieInfo {

    private int counts;
    private List<SerieItem> list;
    private boolean end;


    public int getCounts() {
        return counts;
    }

    public List<SerieItem> getList() {
        return list;
    }

    public boolean isEnd() {
        return end;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }
}
