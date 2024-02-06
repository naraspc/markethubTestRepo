package org.hanghae.markethub.domain.cart.service;

import org.assertj.core.api.Assertions;
import org.hanghae.markethub.domain.cart.entity.NoUserCart;
import org.hanghae.markethub.domain.cart.repository.RedisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CartRedisServiceTest {

    @Autowired
    private RedisRepository redisRepository;

    @Test
    void save() throws UnknownHostException {
        String ip = String.valueOf(InetAddress.getLocalHost());

        NoUserCart cart = NoUserCart.builder()
                .ip(ip)
                .quantity(11L)
                .itemId(1L)
                .build();

        NoUserCart userCart = redisRepository.save(cart);
        System.out.println("userCart.getIp() = " + userCart.getIp());
        System.out.println("ip = " + ip);

        assertThat(userCart.getIp()).isEqualTo(ip);
    }

    @Test
    void findAllByIp() throws UnknownHostException {
        String ip = String.valueOf(InetAddress.getLocalHost());

        NoUserCart cart = NoUserCart.builder()
                .ip(ip)
                .quantity(11L)
                .itemId(1L)
                .build();

        redisRepository.save(cart);

        List<NoUserCart> ips = redisRepository.findAllByIp(ip);
        System.out.println("ips.get(0).getIp() = " + ips.get(0).getIp());
        System.out.println("ip = " + ip);
        assertThat(ips.get(0).getIp()).isEqualTo(ip);
    }
}