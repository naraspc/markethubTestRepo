package org.hanghae.markethub.domain.cart.controller;
import com.fasterxml.jackson.core.JsonProcessingException;


import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.cart.dto.CartRequestDto;
import org.hanghae.markethub.domain.cart.service.CartService;
import org.hanghae.markethub.global.security.impl.UserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.UnknownHostException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public String getCarts(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) throws JsonProcessingException {

        model.addAttribute("carts", cartService.getCarts(userDetails.getUser()));
        return "cart";
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<String> addCart(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody CartRequestDto requestDto){

        return cartService.addCart(userDetails.getUser(), requestDto);
    }

    @GetMapping("/addCarts")
    public String addNoUserCart(@AuthenticationPrincipal UserDetailsImpl userDetails) throws UnknownHostException {
        cartService.addNoUserCart(userDetails.getUser());
        return "redirect:/api/carts";
    }

    @PatchMapping("/{cartId}")
    public ResponseEntity<String> updateCart(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody CartRequestDto requestDto,@PathVariable Long cartId, Model model){
        // dynamicUpdate 애노테이션이 성능이 더 좋다는 의견이 있어서 나중에 참고하기
        return cartService.updateCart(userDetails.getUser(), requestDto, cartId);
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<String> deleteCart(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long cartId, Model model){

        return cartService.deleteCart(userDetails.getUser(), cartId);

    }

    @DeleteMapping("/allCarts")
    public ResponseEntity<String> deleteAllCart(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return cartService.deleteAllCart(userDetails);
    }

}