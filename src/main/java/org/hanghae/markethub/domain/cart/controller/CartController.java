package org.hanghae.markethub.domain.cart.controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.cart.config.CartValids;
import org.hanghae.markethub.domain.cart.dto.CartRequestDto;
import org.hanghae.markethub.domain.cart.dto.CartResponseDto;
import org.hanghae.markethub.domain.cart.entity.Cart;
import org.hanghae.markethub.domain.cart.service.CartRedisService;
import org.hanghae.markethub.domain.cart.service.CartService;
import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.domain.user.security.UserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @GetMapping
    public String getCarts(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) throws JsonProcessingException {

        User user = userDetails.getUser();

        List<CartResponseDto> carts = cartService.getCarts(user);
        model.addAttribute("carts",carts);
        return "cart";
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<String> addCart(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody CartRequestDto requestDto){
        return cartService.addCart(userDetails.getUser(), requestDto);
    }

    @PatchMapping("/{cartId}")
    public String updateCart(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody CartRequestDto requestDto,@PathVariable Long cartId, Model model){
        // dynamicUpdate 애노테이션이 성능이 더 좋다는 의견이 있어서 나중에 참고하기
        List<CartResponseDto> carts = cartService.updateCart(userDetails.getUser(), requestDto, cartId);
        model.addAttribute("carts",carts);
        cartService.updateCart(userDetails.getUser(),requestDto,cartId);

        return "cart";
    }

    @DeleteMapping("/{cartId}")
    public String deleteCart(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long cartId, Model model){
        List<CartResponseDto> carts = cartService.deleteCart(userDetails.getUser(), cartId);
        model.addAttribute("carts",carts);

        return "cart";
    }

}
