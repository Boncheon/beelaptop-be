package com.example.sever.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "ghn")
public class GhnProperties {
    private String baseUrl;
    private String token;
    private Integer shopId;

    private Integer fromDistrictId;
    private String fromWardCode;

    private Integer serviceTypeId = 2;
}

