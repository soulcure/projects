package com.taku.safe.db.bean;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by colin on 2016/7/22.
 */
@Entity
public class NoticeMsg {

    @Id
    private Long id;

    private Long time;

    private String title;

    private String content;

    @Generated(hash = 1756342381)
    public NoticeMsg(Long id, Long time, String title, String content) {
        this.id = id;
        this.time = time;
        this.title = title;
        this.content = content;
    }

    @Generated(hash = 1893322650)
    public NoticeMsg() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTime() {
        return this.time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }


}
