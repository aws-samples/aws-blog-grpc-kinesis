package com.amazonaws.blog.demo;

import lombok.Data;

@Data
public class ConfigData {
    private  String version;
    private String clientId;
    private String clientSecret;
    private String tokenUrl;
}
