package com.microservicio.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class EmailConfig {
    @Bean
    public Pattern emailPattern(@Value("${app.email.regex}") String regex) {
        return Pattern.compile(regex);
    }
}
