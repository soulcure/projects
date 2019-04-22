package com.ivmall.android.app.entity;

import java.util.List;

/**
 * Created by koen on 2015/11/9.
 */
public class RecommendChildInfo {

    private int profileId; //
    private List<RecommendChildItem> recommendation; //

    public int getProfileId() {
        return profileId;
    }

    public List<RecommendChildItem> getRecommendation() {
        return recommendation;
    }
}
