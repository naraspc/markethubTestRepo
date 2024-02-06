package org.hanghae.markethub.global.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
public class RedisConfiguration {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    // RedisConnection은 RedisConnectionFactory를 통해 생성되고 이 RedisConnectionFactory가 PersistenceExceptionTranslator 역할을 수행
    // IOC 컨테이너를 통해 RedisConnectionFactory에 적절한 connector를 설정하고 이를 주입받아서 사용
    @Bean
    public RedisConnectionFactory redisConnectionFactory(){
        // LettuceConnectionFactory로부터 생성된 Lettuce는 동일한 thread-safe connection을 공유
        return new LettuceConnectionFactory(redisHost,redisPort);
    }

//    @Bean
//    public RedisTemplate<String,String> redisTemplate(){
//        // key,value에 대해 Serializer를 설정
//        // String,String으로 하기 때문에 new StringRedisSerializer()로 등록
//        // key, value둘중에 하나라도 설정을 안해주면 임의의 값이 등록된다
//
//        RedisTemplate<String,String > redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(redisConnectionFactory());
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new StringRedisSerializer());
//        return redisTemplate;
//    }
}
