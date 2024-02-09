package org.hanghae.markethub.domain.cart.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.cart.entity.NoUserCart;
import org.hanghae.markethub.domain.cart.service.CartRedisService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.*;
import java.util.Enumeration;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart/nouser")
public class NoUserCartController {

    private final CartRedisService redisService;

    @GetMapping("/{itemId}/{quantities}")
    public NoUserCart saveRedis(@PathVariable Long itemId, @PathVariable Long quantities) throws UnknownHostException {
        String ip = String.valueOf(InetAddress.getLocalHost());

        NoUserCart cart = NoUserCart.builder()
                .ip(ip)
                .quantity(quantities)
                .itemId(itemId)
                .build();

        return redisService.save(cart);
    }

    @GetMapping("/getAll")
    public List<NoUserCart> getAllRedis()throws UnknownHostException{
        String ip = String.valueOf(InetAddress.getLocalHost());

        return redisService.findAllByIp(ip);
    }

}
