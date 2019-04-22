package com.taku.safe.entity;

/**
 * Created by soulcure on 2017/6/3.
 */

public class AnalysisItem {

    public int resId;
    public String name;
    public Class mClass;


    public AnalysisItem(int resId, String name, Class entry) {
        this.resId = resId;
        this.name = name;
        this.mClass = entry;
    }
}
