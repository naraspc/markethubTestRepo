package org.hanghae.markethub.domain.cart.service;

import jakarta.transaction.Transactional;
import org.hanghae.markethub.domain.cart.entity.NoUserCart;
import org.hanghae.markethub.domain.cart.repository.RedisRepository;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.global.constant.Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
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
                .itemId(item.getId())
                .build();

        NoUserCart userCart = redisRepository.save(cart);

        assertThat(userCart.getIp()).isEqualTo(ip);
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
                .itemId(item.getId())
                .status(Status.EXIST)
                .build();

        redisRepository.save(cart);

        List<NoUserCart> ips = redisRepository.findAllByIpAndStatus(ip,Status.EXIST);

        assertThat(ips.get(0).getIp()).isEqualTo(ip);
    }

    @Test
    void delete() throws UnknownHostException {
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
                .itemId(item.getId())
                .status(Status.EXIST)
                .build();

        NoUserCart save = redisRepository.save(cart);

        assertThat(save.getIp()).isEqualTo(ip);

        redisRepository.delete(save);

        NoUserCart nullCart = redisRepository.findById(cart.getId()).orElse(null);

        assertThat(nullCart).isEqualTo(null);

    }
}