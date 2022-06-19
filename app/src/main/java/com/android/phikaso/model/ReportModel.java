package com.android.phikaso.model;

import com.google.gson.annotations.SerializedName;

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

