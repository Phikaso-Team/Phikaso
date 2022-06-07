package com.android.phikaso.model;

public class CallItem {

    String name;        // 이름
    String number;      // 전화번호
    String date;        // 날짜 및 시간
    String type;        // 수신, 발신, 부재중

    public CallItem(String number, String name, String date, String type) {
        this.name = name;
        this.number = number;
        this.date = date;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() { return type; }

    public void setType(String type) {
        this.type = type;
    }

}
