package org.hanghae.markethub.domain.cart.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.cart.dto.CartRequestDto;
import org.hanghae.markethub.domain.cart.entity.Cart;
import org.hanghae.markethub.domain.cart.repository.CartRepository;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.global.constant.Status;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    // security 도입되면 User변경 해야함
    // 다른 부분을 연결하면 item도 존재하는지 검사넣기
    public ResponseEntity<String> addCart(User user, CartRequestDto requestDto){

        Item item = requestDto.getItem();
        ValidItem(item);

        Optional<Cart> checkCart = cartRepository.findByitemId(requestDto.getItem().getId());
        if (checkCart.isPresent()) {
            checkCart.get().update(requestDto);
            cartRepository.save(checkCart.get());
        } else {
            Cart cart = Cart.builder()
                    .item(requestDto.getItem())
                    .status(Status.EXIST)
                    .address(user.getAddress())
                    .quantity(requestDto.getItem().getQuantity())
                    .price(requestDto.getItem().getPrice())
                    .user(user)
                    .build();

            cartRepository.save(cart);
        }

        return ResponseEntity.ok("Success Cart");
    }

    @Transactional
    public ResponseEntity<String> updateCart(User user, CartRequestDto requestDto,Long cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(null);
        cart.update(requestDto);

        return ResponseEntity.ok("Success Update Cart");
    }

    public ResponseEntity<String> deleteCart(User user,Long cartId){
        Cart cart = cartRepository.findById(cartId).orElseThrow(null);
        cart.delete();

        return ResponseEntity.ok("Success Delete Cart");
    }

    public ResponseEntity<List<Cart>> getCarts(User user){
        List<Cart> carts = cartRepository.findAllByUser(user);

        return ResponseEntity.ok(carts);
    }


    private static void ValidItem(Item item) {
        if (item.getStatus().equals(Status.DELETED) || item.getQuantity() <= 0){
            throw new IllegalArgumentException("해당 상품은 존재하지않으므로 다시 확인해주세요");
        }
    }

}
