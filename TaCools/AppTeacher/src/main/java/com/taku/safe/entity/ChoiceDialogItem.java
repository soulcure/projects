package com.taku.safe.entity;

/**
 * Created by soulcure on 2017/7/10.
 */

public class ChoiceDialogItem {
    String title;
    int id;


    public ChoiceDialogItem(String title, int id) {
        this.title = title;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
