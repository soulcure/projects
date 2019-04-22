package com.applidium.nickelodeon.entity;

import com.applidium.nickelodeon.dialog.DoYouLikeDialog;
import com.applidium.nickelodeon.uitls.GsonUtil;

/**
 * Created by colin on 2015/5/25.
 */
public class DoYouLikeRequest extends ProtocolRequest {


    private String token;
    private int profileId;   //
    private int contentId;   //
    private DoYouLikeDialog.Select rating;
    private int percentageViewed;


    public void setToken(String token) {
        this.token = token;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
    }

    public void setRating(DoYouLikeDialog.Select rating) {
        this.rating = rating;
    }

    public void setPercentageViewed(int percentageViewed) {
        this.percentageViewed = percentageViewed;
    }

    @Override
    public String toJsonString() {
        return GsonUtil.format(this);
    }


}
