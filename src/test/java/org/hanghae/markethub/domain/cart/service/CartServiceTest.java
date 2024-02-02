package org.hanghae.markethub.domain.cart.service;

import jakarta.transaction.Transactional;
import org.hanghae.markethub.domain.cart.dto.CartRequestDto;
import org.hanghae.markethub.domain.cart.entity.Cart;
import org.hanghae.markethub.domain.cart.repository.CartRepository;
import org.hanghae.markethub.domain.cart.repository.UserRepository;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.domain.item.repository.ItemRepository;
import org.hanghae.markethub.domain.store.entity.Store;
import org.hanghae.markethub.domain.store.repository.StoreRepository;
import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.global.constant.Role;
import org.hanghae.markethub.global.constant.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@Transactional
class CartServiceTest {
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private UserRepository userRepository;

    private User user;
    private Item item;
    private Item notExistItem;
    private Item soldOutItem;

    @BeforeEach
    public void datas() {

        User user1 = User.builder()
                .id(1L)
                .email("1234@naver.com")
                .password("1234")
                .name("lee")
                .phone("010-1234")
                .address("서울시")
                .role(Role.ADMIN)
                .status(Status.EXIST).build();

        user = userRepository.save(user1);

        Store store = Store.builder()
                .id(1L)
                .user(user)
                .status(Status.EXIST)
                .build();

        Item item1 = Item.builder()
                .id(1L)
                .itemName("노트북")
                .price(500000)
                .quantity(5)
                .user(user1)
                .itemInfo("구형 노트북")
                .category("가전 제품")
                .status(Status.EXIST)
                .store(store)
                .build();

        Item item2 = Item.builder()
                .id(1L)
                .itemName("노트북")
                .price(500000)
                .quantity(5)
                .user(user1)
                .itemInfo("구형 노트북")
                .category("가전 제품")
                .status(Status.DELETED)
                .store(store)
                .build();

        Item item3 = Item.builder()
                .id(1L)
                .itemName("노트북")
                .price(500000)
                .quantity(0)
                .user(user1)
                .itemInfo("구형 노트북")
                .category("가전 제품")
                .status(Status.EXIST)
                .store(store)
                .build();

        storeRepository.save(store);
        item = itemRepository.save(item1);
        notExistItem = itemRepository.save(item2);
        soldOutItem = itemRepository.save(item3);
    }

    @Nested
    class addCart {
        @Test
        @DisplayName("카드 등록 성공")
        void addCartSuccess() {
            // given
            CartRequestDto requestDto = new CartRequestDto();
            requestDto.setItem(item);
            requestDto.setQuantity(1);

            // when
            if (item.getStatus().equals(Status.DELETED) || item.getQuantity() <= 0) {
                throw new IllegalArgumentException("해당 상품은 존재하지않으므로 다시 확인해주세요");
            }

            Cart save;
            Optional<Cart> checkCart = cartRepository.findByitemId(requestDto.getItem().getId());
            if (checkCart.isPresent()) {
                checkCart.get().update(requestDto);
                save = cartRepository.save(checkCart.get());
            } else {
                Cart cart = Cart.builder().item(requestDto.getItem()).status(Status.EXIST).address(user.getAddress()).quantity(requestDto.getItem().getQuantity()).price(requestDto.getItem().getPrice()).user(user).build();

                save = cartRepository.save(cart);
            }

            // then
            assertThat(save.getItem()).isEqualTo(item);
        }

        @Test
        @DisplayName("상품 삭제되서 장바구니 추가안됨")
        void notExistItemFail() {
            // given
            CartRequestDto requestDto = new CartRequestDto();
            requestDto.setItem(notExistItem);
            requestDto.setQuantity(1);


            // when
            if (item.getStatus().equals(Status.DELETED) || item.getQuantity() <= 0) {
                throw new IllegalArgumentException("해당 상품은 존재하지않으므로 다시 확인해주세요");
            }

            // then
            assertThrows(IllegalArgumentException.class, () -> {
                throw new IllegalArgumentException("해당 상품은 존재하지않으므로 다시 확인해주세요");
            });

        }

        @Test
        @DisplayName("상품 개수가 없어서 장바구니 추가안됨")
        void soldOutItemFail() {
            // given
            CartRequestDto requestDto = new CartRequestDto();
            requestDto.setItem(soldOutItem);
            requestDto.setQuantity(1);


            // when
            if (item.getStatus().equals(Status.DELETED) || item.getQuantity() <= 0) {
                throw new IllegalArgumentException("해당 상품은 존재하지않으므로 다시 확인해주세요");
            }

            // then
            assertThrows(IllegalArgumentException.class, () -> {
                throw new IllegalArgumentException("해당 상품은 존재하지않으므로 다시 확인해주세요");
            });
        }
    }

    @Nested
    class updateCart {
        @Test
        @DisplayName("수정 성공")
        void updateCartSuccess(){
            // given
            Cart setCart = Cart.builder()
                    .cartId(1L)
                    .item(item)
                    .status(Status.EXIST)
                    .address(user.getAddress())
                    .quantity(1)
                    .price(1)
                    .user(user).build();

            CartRequestDto res = new CartRequestDto();
            res.setQuantity(11);
            res.setItem(item);

            cartRepository.save(setCart);

            // when
            Cart cart = cartRepository.findById(setCart.getCartId()).orElseThrow(null);
            cart.update(res);

            // then
            assertThat(cart.getQuantity()).isEqualTo(11);
        }
    }

    @Nested
    class deleteCart {
        @Test
        @DisplayName("삭제 성공")
        void deleteCartSuccess(){
            // given
            Cart setCart = Cart.builder()
                    .cartId(1L)
                    .item(item)
                    .status(Status.EXIST)
                    .address(user.getAddress())
                    .quantity(1)
                    .price(1)
                    .user(user).build();

            cartRepository.save(setCart);

            // when
            Cart cart = cartRepository.findById(setCart.getCartId()).orElseThrow(null);
            cart.delete();

            // then
            assertThat(cart.getStatus()).isEqualTo(Status.DELETED);
        }
    }


}