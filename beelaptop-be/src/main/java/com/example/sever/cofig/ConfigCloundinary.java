package com.example.sever.cofig;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ConfigCloundinary {

    @Bean
    public Cloudinary configKey(){


        Map<String,String> config = new HashMap<>();
        config.put("cloud_name", "dkgg0enrq");
        config.put("api_key", "836851493337871");
        config.put("api_secret", "gLWCfs86bqXlWjX1r4yy-mSQcSQ");
        return new Cloudinary(config);
    }


}
