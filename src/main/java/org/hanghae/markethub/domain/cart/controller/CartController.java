package org.hanghae.markethub.domain.cart.controller;

import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.cart.dto.CartRequestDto;
import org.hanghae.markethub.domain.cart.dto.CartResponseDto;
import org.hanghae.markethub.domain.cart.entity.Cart;
import org.hanghae.markethub.domain.cart.service.CartRedisService;
import org.hanghae.markethub.domain.cart.service.CartService;
import org.hanghae.markethub.domain.user.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 향후 Controller로 변경
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
//    @GetMapping("/page")
//    public String cartPage(){
//        return "cart";
//    }

    @GetMapping
    public String getCarts(User user, Model model){

        // test용으로 작성했지만 수정필요
        User us = User.builder()
                .id(59L)
                .build();

        List<CartResponseDto> carts = cartService.getCarts(us);
        model.addAttribute("carts",carts);
        return "cart";
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<String> addCart(User user, @RequestBody CartRequestDto requestDto){
        return cartService.addCart(user, requestDto);
    }

    @PatchMapping("/{cartId}")
//    @ResponseBody
    public String updateCart(User user, @RequestBody CartRequestDto requestDto,@PathVariable Long cartId, Model model){
        // dynamicUpdate 애노테이션이 성능이 더 좋다는 의견이 있어서 나중에 참고하기
        List<CartResponseDto> carts = cartService.updateCart(user, requestDto, cartId);
        model.addAttribute("carts",carts);
        cartService.updateCart(user,requestDto,cartId);

        return "cart";
    }

    @DeleteMapping("/{cartId}")
//    @ResponseBody
    public String deleteCart(User user, @PathVariable Long cartId, Model model){
        List<CartResponseDto> carts = cartService.deleteCart(user, cartId);
        model.addAttribute("carts",carts);

        return "cart";
    }

}
