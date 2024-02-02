package org.hanghae.markethub.domain.cart.service;

import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.cart.dto.CartRequestDto;
import org.hanghae.markethub.domain.cart.entity.Cart;
import org.hanghae.markethub.domain.cart.repository.CartRepository;
import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.global.constant.Status;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    // security 도입되면 User변경 해야함
    // 다른 부분을 연결하면 item도 존재하는지 검사넣기
    public ResponseEntity<String> addCart(User user, CartRequestDto requestDto){
        Cart cart = Cart.builder()
                    .item(requestDto.getItem())
                    .status(Status.EXIST)
                    .address(user.getAddress())
                    .quantity(requestDto.getItem().getQuantity())
                    .price(requestDto.getItem().getPrice())
                    .user(user)
                    .build();

        cartRepository.save(cart);

        return ResponseEntity.ok("Success Cart");
    }


}
