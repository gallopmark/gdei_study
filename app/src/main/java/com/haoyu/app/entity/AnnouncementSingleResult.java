package com.haoyu.app.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 创建日期：2016/11/8 on 17:42
 * 描述: 通知公告
 * 作者:马飞奔 Administrator
 */
public class AnnouncementSingleResult {
    @Expose
    @SerializedName("responseCode")
    private String responseCode;
    @Expose
    @SerializedName("responseData")
    private AnnouncementEntity responseData;
    @Expose
    @SerializedName("success")
    private Boolean success;

    public String getResponseCode() {
        return this.responseCode;
    }

    public AnnouncementEntity getResponseData() {
        return this.responseData;
    }

    public Boolean getSuccess() {
        return this.success;
    }

    public void setResponseCode(String paramString) {
        this.responseCode = paramString;
    }

    public void setResponseData(AnnouncementEntity paramAnnouncementsEntity) {
        this.responseData = paramAnnouncementsEntity;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
