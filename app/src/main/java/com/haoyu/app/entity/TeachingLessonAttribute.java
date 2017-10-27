package com.haoyu.app.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 创建日期：2017/10/26 on 10:24
 * 描述:
 * 作者:马飞奔 Administrator
 */
public class TeachingLessonAttribute implements Serializable {
    /**
     * id	ID
     * mStage	学段
     * mSubject	学科
     */
    @Expose
    @SerializedName("id")
    private String id;
    @Expose
    @SerializedName("stage")
    private String stage;
    @Expose
    @SerializedName("subject")
    private String subject;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
