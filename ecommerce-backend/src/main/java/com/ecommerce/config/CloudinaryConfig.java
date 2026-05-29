package com.ecommerce.config;

import com.cloudinary.Cloudinary;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {

        Map<String, String> config =
                new HashMap<>();

        config.put(
                "cloud_name",
                "dphralznq"
        );

        config.put(
                "api_key",
                "792194578241156"
        );

        config.put(
                "api_secret",
                "4gsb68zudCPu1ZbsMHBRaHDNhek"
        );

        return new Cloudinary(config);
    }
}