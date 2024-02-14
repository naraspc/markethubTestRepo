package org.hanghae.markethub.domain.cart.service;

import org.assertj.core.api.Assertions;
import org.hanghae.markethub.domain.cart.entity.NoUserCart;
import org.hanghae.markethub.domain.cart.repository.RedisRepository;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.global.constant.Status;
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
        Item item = Item.builder()
                .id(1L)
                .itemInfo("test")
                .itemName("name")
                .price(1111)
                .status(Status.EXIST)
                .quantity(11111)
                .build();

        String ip = String.valueOf(InetAddress.getLocalHost());

        NoUserCart cart = NoUserCart.builder()
                .ip(ip)
                .quantity(11)
                .item(item)
                .build();

        NoUserCart userCart = redisRepository.save(cart);

        assertThat(userCart.getIp()).isEqualTo(ip);
        assertThat(cart.getItem().getItemName()).isEqualTo("name");
    }

    @Test
    void findAllByIp() throws UnknownHostException {

        Item item = Item.builder()
                .id(1L)
                .itemInfo("test")
                .itemName("name")
                .price(1111)
                .status(Status.EXIST)
                .quantity(11111)
                .build();

        String ip = String.valueOf(InetAddress.getLocalHost());

        NoUserCart cart = NoUserCart.builder()
                .ip(ip)
                .quantity(11)
                .item(item)
                .status(Status.EXIST)
                .build();

        redisRepository.save(cart);

        List<NoUserCart> ips = redisRepository.findAllByIpAndStatus(ip,Status.EXIST);

        assertThat(ips.get(0).getIp()).isEqualTo(ip);
        assertThat(ips.get(0).getItem().getItemName()).isEqualTo("name");
    }
}