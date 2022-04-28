package com.android.phikaso.server;

import com.google.gson.annotations.SerializedName;

public class PhishingData {
    //피싱 여부
    @SerializedName("phishing")
    private boolean phishing;

    //피싱 가능성
    @SerializedName("probability")
    private String probability;

    //피싱 텍스트
    @SerializedName("text")
    private String text;

    public boolean getPhishing(){
        return phishing;
    }

    public void setPhishing(boolean phishing){
        this.phishing = phishing;
    }

    public String getProbability(){
        return probability;
    }

    public void setProbability(String probability){
        this.probability = probability;
    }

    public String getText(){
        return text;
    }

    public void setText(String text){
        this.text = text;
    }
}
