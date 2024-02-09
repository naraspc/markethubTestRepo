package org.hanghae.markethub.domain.cart.controller;

import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.cart.entity.NoUserCart;
import org.hanghae.markethub.domain.cart.service.CartRedisService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.*;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/cart/nouser")
public class NoUserCartController {

    private final CartRedisService redisService;

    @PostMapping("/{itemId}/{quantities}")
    public String saveRedis(@PathVariable Long itemId, @PathVariable Long quantities) throws UnknownHostException {

        redisService.save(itemId,quantities);
        return "item";
    }

    @GetMapping("/getAll")
    public String getAllRedis(Model model)throws UnknownHostException{

        redisService.findAllByIp();

        return "cart";
    }

    @DeleteMapping("/{itemId}")
    public String

}
