package org.hanghae.markethub.domain.cart.service;

import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.hanghae.markethub.MarkethubApplication;
import org.hanghae.markethub.domain.cart.dto.CartRequestDto;
import org.hanghae.markethub.domain.cart.entity.Cart;
import org.hanghae.markethub.domain.cart.repository.CartRepository;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.domain.item.repository.ItemRepository;
import org.hanghae.markethub.domain.store.entity.Store;
import org.hanghae.markethub.domain.store.repository.StoreRepository;
import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.global.constant.Role;
import org.hanghae.markethub.global.constant.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;


// @ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = MarkethubApplication.class)
// @DataJpaTest
// @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CartServiceTest {
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CartRepository cartRepository;

    private User user;
    private Item item;

    @BeforeEach
    public void datas(){

        User user = User.builder()
                .id(1L)
                .email("1234@naver.com")
                .password("1234")
                .name("lee")
                .phone("010-1234")
                .address("서울시")
                .role(Role.ADMIN)
                .status(Status.EXIST)
                .build();

        Store store = Store.builder()
                .id(1L)
                .user(user)
                .status(Status.EXIST)
                .build();

        Item item = Item.builder()
                .id(1L)
                .itemName("노트북")
                .price(500000)
                .quantity(5)
                .itemInfo("구형 노트북")
                .category("가전 제품")
                .status(Status.EXIST)
                .store(store)
                .build();
//
//        Item item2 = Item.builder()
//                .id(2L)
//                .itemName("아이폰11")
//                .price(1111111)
//                .quantity(5)
//                .itemInfo("구형 핸드폰")
//                .category("가전 제품")
//                .status(Status.DELETED)
//                .store(store)
//                .build();
//
//        Item item3 = Item.builder()
//                .id(3L)
//                .itemName("아이폰12")
//                .price(222222)
//                .quantity(0)
//                .itemInfo("구형 핸드폰")
//                .category("가전 제품")
//                .status(Status.EXIST)
//                .store(store)
//                .build();
//
//        items.add(item);
//        items.add(item2);
//        items.add(item3);

        storeRepository.save(store);
        itemRepository.save(item);

    }

    @Test
    @DisplayName("카드 등록 성공")
    void addCartSuccess(){
        // given
        CartRequestDto requestDto = new CartRequestDto();
//        Item item = items.get(0);
        requestDto.setItem(item);
        requestDto.setQuantity(1);
        
        // when
        if (item.getStatus().equals(Status.DELETED) || item.getQuantity() <= 0){
            throw new IllegalArgumentException("해당 상품은 존재하지않으므로 다시 확인해주세요");
        }

        Cart save;
        Optional<Cart> checkCart = cartRepository.findByitemId(requestDto.getItem().getId());
        if (checkCart.isPresent()){
            checkCart.get().update(requestDto,checkCart);
            save = cartRepository.save(checkCart.get());
        }else{
            Cart cart = Cart.builder()
                    .item(requestDto.getItem())
                    .status(Status.EXIST)
                    .address(user.getAddress())
                    .quantity(requestDto.getItem().getQuantity())
                    .price(requestDto.getItem().getPrice())
                    .user(user)
                    .build();

           save = cartRepository.save(cart);
        }

        System.out.println(save.getCartId());

    }


}