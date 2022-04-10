package com.android.phikaso.model;

import java.util.HashMap;
import java.util.Map;

public class RegisterModel {
    public String name;
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
