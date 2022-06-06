package com.android.phikaso.model;

public class CallItem {

    String number;      // 전화번호
    String name;        // 이름
    String time;        // 시간
    String callType;    // 수신, 발신, 부재중

    public CallItem(String number, String name, String time, String callType) {
        this.number = number;
        this.name = name;
        this.time = time;
        this.callType = callType;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

}
