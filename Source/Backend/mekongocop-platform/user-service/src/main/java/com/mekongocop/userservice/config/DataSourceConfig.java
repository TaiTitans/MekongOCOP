package com.mekongocop.userservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);
    @Bean
    public DataSource dataSource(Environment env) {
        String url = env.getProperty("DB_URL");
        String username = env.getProperty("DB_USERNAME");
        String password = env.getProperty("DB_PASSWORD");
        try{
            DataSource dataSource = DataSourceBuilder.create()
                    .url(url)
                    .username(username)
                    .password(password).build();
            logger.info("==== Database connection successful ====");
            return dataSource;
        }catch(Exception e){
            logger.error("Failed to connect to the database: {}",e.getMessage(), e);
            throw e;
        }
    }
}