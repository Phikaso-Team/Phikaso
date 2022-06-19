package com.phikaso.app.model;

import java.util.HashMap;
import java.util.Map;

public class RegisterModel {
    public String title;
    public String phone;
    public String email;
    public String content;
    public String file;
    public int count = 0;

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("count", count);
        return result;
    }
}
