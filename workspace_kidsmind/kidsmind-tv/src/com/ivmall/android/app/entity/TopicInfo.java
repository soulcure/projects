package com.ivmall.android.app.entity;

import java.util.List;

/**
 * Created by colin on 2015/5/25.
 */
public class TopicInfo {

    private List<TopicItem> list;


    public List<TopicItem> getList() {
        return list;
    }

    public TopicItem getTopicFirstItem() {
        TopicItem res = null;
        if (list != null && list.size() > 0) {
            res = list.get(0);
        }
        return res;
    }

}
