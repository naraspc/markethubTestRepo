package org.hanghae.markethub.domain.cart.config;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.cart.dto.CartRequestDto;
import org.hanghae.markethub.domain.cart.dto.CartResponseDto;
import org.hanghae.markethub.domain.cart.dto.UpdateValidResponseDto;
import org.hanghae.markethub.domain.cart.entity.Cart;
import org.hanghae.markethub.domain.cart.repository.CartRepository;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.domain.item.service.ItemService;
import org.hanghae.markethub.global.constant.Status;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CartConfig {

    private final CartRepository cartRepository;
    private final ItemService itemService;

    public void validItem(Item item) {

        if (item.getStatus().equals(Status.DELETED) || item.getQuantity() <= 0){
            throw new IllegalArgumentException("해당 상품은 존재하지않으므로 다시 확인해주세요");
        }

    }

    public UpdateValidResponseDto updateVaild(Long cartId){

        Cart cart = cartRepository.findById(cartId).orElseThrow(null);
        Item item = itemService.getItemValid(cart.getItem().getId());

        return UpdateValidResponseDto.builder()
                .cart(cart)
                .item(item)
                .build();
    }


    @Transactional
    public void changeCart(CartRequestDto requestDto, Item item, Optional<Cart> checkCart) {
        if (item.getQuantity() < requestDto.getQuantity().get(0)){
            throw new IllegalArgumentException("상품의 개수를 넘어서 담을수가 없습니다.");
        }

        if (checkCart.get().getStatus().equals(Status.EXIST)){
            checkCart.get().update(requestDto, item);
        }else{
            checkCart.get().updateDelete(requestDto, item);
        }
    }

    public void addNoUserCart(CartResponseDto noUserCart, Item item, Optional<Cart> checkCart) {
        if (item.getQuantity() < noUserCart.getQuantity()){
            throw new IllegalArgumentException("상품의 개수를 넘어서 담을수가 없습니다.");
        }
        if (checkCart.get().getStatus().equals(Status.EXIST)){
            checkCart.get().updateNoUser(noUserCart);
        }else{
            checkCart.get().updateDeleteNoUser(noUserCart);
        }
    }
}
