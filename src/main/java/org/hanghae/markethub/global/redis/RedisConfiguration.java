//package org.hanghae.markethub.global.redis;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
//
//@Configuration
//public class RedisConfiguration {
//
//    private String redisHost ="localhost";
//
//    private int redisPort = 6379;
//
//    @Bean
//    public RedisConnectionFactory redisConnectionFactory(){
//        return new LettuceConnectionFactory(redisHost,redisPort);
//    }
//}
