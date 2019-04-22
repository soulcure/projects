package com.ivmall.android.app.expand;

/**
 * Created by colin on 2016/3/29.
 */
public class SerieListItem {
    public static final int TYPE_HEADER = 1000;
    public static final int TYPE_BODY = 1001;

    private int serieId;
    private int episodeId;

    private int ItemType;
    private String Text;

    private boolean isChecked;


    public SerieListItem(String text) {
        Text = text;
    }


    public int getSerieId() {
        return serieId;
    }

    public void setSerieId(int serieId) {
        this.serieId = serieId;
    }

    public int getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(int episodeId) {
        this.episodeId = episodeId;
    }

    public int getItemType() {
        return ItemType;
    }

    public void setItemType(int itemType) {
        ItemType = itemType;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
