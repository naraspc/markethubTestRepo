package org.hanghae.markethub.global.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@EnableRedisRepositories // redis를 사용한다고 명시해주는 애노테이션
@Configuration
public class RedisConfiguration {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Bean
    public RedisConnectionFactory redisConnectionFactory(){
        // LettuceConnectionFactory 객체를 생성하여 반환하는 메소드
        // Redis Java 클라이언트 라이브러리인 Lettuce를 사용해서 Redis서버와 연결해준다
        return new LettuceConnectionFactory(redisHost,redisPort);
    }
}
