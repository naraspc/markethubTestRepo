package org.hanghae.markethub.domain.cart.config;

import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.cart.dto.UpdateValidResponseDto;
import org.hanghae.markethub.domain.cart.entity.Cart;
import org.hanghae.markethub.domain.cart.repository.CartRepository;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.domain.item.repository.ItemRepository;
import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.domain.user.repository.UserRepository;
import org.hanghae.markethub.global.constant.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CartValids {

    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public void validItems(List<Item> items) {
        for (Item item : items) {
            if (item.getStatus().equals(Status.DELETED) || item.getQuantity() <= 0){
                throw new IllegalArgumentException("해당 상품은 존재하지않으므로 다시 확인해주세요");
            }
        }
    }

    public void validItem(Item item) {

            if (item.getStatus().equals(Status.DELETED) || item.getQuantity() <= 0){
                throw new IllegalArgumentException("해당 상품은 존재하지않으므로 다시 확인해주세요");
            }

    }

    public UpdateValidResponseDto updateVaild(Long cartId){
        Cart cart = cartRepository.findById(cartId).orElseThrow(null);

        Item item = itemRepository.findById(cart.getItem().getId()).orElse(null);

        return UpdateValidResponseDto.builder()
                .cart(cart)
                .item(item)
                .build();
    }

    public Item checkItem(Long itemId){
        return itemRepository.findById(itemId).orElse(null);
    }

    public User validUser(Long id){
       return userRepository.findById(id).orElse(null);
    }

}
