package org.hanghae.markethub.domain.cart.controller;

import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.cart.dto.CartRequestDto;
import org.hanghae.markethub.domain.cart.dto.CartResponseDto;
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

    @PostMapping
    public ResponseEntity<String> saveRedis(@RequestBody CartRequestDto requestDto) throws UnknownHostException {

        return redisService.save(requestDto);

    }

    @GetMapping("/getAll")
    public String getAllRedis(Model model)throws UnknownHostException{

        List<CartResponseDto> carts = redisService.getAll();

        model.addAttribute("carts",carts);
        return "cart";
    }

    @DeleteMapping
    public String deleteCart(@RequestBody CartRequestDto requestDto){
        redisService.deleteCart(requestDto);

        return "cart";
    }

    @PatchMapping
    public String updateCart(@RequestBody CartRequestDto requestDto){
        redisService.updateCart(requestDto);

        return "cart";
    }

}
