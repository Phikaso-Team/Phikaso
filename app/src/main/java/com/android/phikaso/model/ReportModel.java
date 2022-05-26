package com.android.phikaso.model;

import com.google.gson.annotations.SerializedName;

// DTO : 사용자가 피싱 신고 -> store in DB
public class ReportModel {

    @SerializedName("phishingReportMsg")
    private String phishingReportMsg;

    @Override
    public String toString() {
        return phishingReportMsg;
    }

    public String getPhishingReportMsg() {
        return phishingReportMsg;
    }

    public void setPhishingReportMsg(String phishingReportMsg) {
        this.phishingReportMsg = phishingReportMsg;
    }

    public ReportModel(String phishingReportMsg) {
        this.phishingReportMsg = phishingReportMsg;
    }
}

