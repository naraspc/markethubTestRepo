package org.hanghae.markethub.domain.cart.service;

import org.hanghae.markethub.domain.cart.config.CartConfig;
import org.hanghae.markethub.domain.cart.dto.CartRequestDto;
import org.hanghae.markethub.domain.cart.dto.CartResponseDto;
import org.hanghae.markethub.domain.cart.dto.UpdateValidResponseDto;
import org.hanghae.markethub.domain.cart.entity.Cart;
import org.hanghae.markethub.domain.cart.repository.CartRepository;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.domain.item.service.ItemService;
import org.hanghae.markethub.domain.store.entity.Store;
import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.domain.user.service.UserService;
import org.hanghae.markethub.global.constant.Role;
import org.hanghae.markethub.global.constant.Status;
import org.hanghae.markethub.global.service.AwsS3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.UnknownHostException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartConfig cartConfig;
    @Mock
    private ItemService itemService;
    @Mock
    private UserService userService;
    @Mock
    private AwsS3Service awsS3Service;
    @Mock
    private CartRedisService cartRedisService;
    @InjectMocks
    private CartService cartService;
    private Item item;
    private Item notExistItem;
    private Item soldOutItem;

    @BeforeEach
    public void init() {

        User user = User.builder()
                .id(1L)
                .email("1234@naver.com")
                .password("1234")
                .name("lee")
                .phone("010-1234")
                .address("서울시")
                .role(Role.ADMIN)
                .status(Status.EXIST).build();

        Store store = Store.builder()
                .id(1L)
                .user(user)
                .status(Status.EXIST)
                .build();

        item = Item.builder()
                .id(2L)
                .itemName("노트북2")
                .price(5000)
                .quantity(5)
                .user(user)
                .itemInfo("구형 노트북")
                .category("가전 제품")
                .status(Status.EXIST)
                .store(store)
                .build();


        notExistItem = Item.builder()
                .id(1L)
                .itemName("노트북")
                .price(500000)
                .quantity(5)
                .user(user)
                .itemInfo("구형 노트북")
                .category("가전 제품")
                .status(Status.DELETED)
                .store(store)
                .build();

        soldOutItem = Item.builder()
                .id(1L)
                .itemName("노트북")
                .price(500000)
                .quantity(0)
                .user(user)
                .itemInfo("구형 노트북")
                .category("가전 제품")
                .status(Status.EXIST)
                .store(store)
                .build();
    }

    @Nested
    class addCart {
        @Test
        @DisplayName("카트 등록 성공")
        void addCartSuccess() {
            // given
            List<Long> items = new ArrayList<>();
            items.add(item.getId());

            List<Integer> quantities = new ArrayList<>();
            quantities.add(1);

            CartRequestDto requestDto = CartRequestDto.builder()
                    .itemId(items)
                    .quantity(quantities)
                    .build();

            User user = User.builder()
                    .id(1L)
                    .build();


            for (int i = 0; i < items.size(); i++) {

                    Cart cart = Cart.builder()
                            .item(item)
                            .status(Status.EXIST)
                            .address(user.getAddress())
                            .quantity(requestDto.getQuantity().get(i))
                            .price(item.getPrice() * requestDto.getQuantity().get(i))
                            .user(user)
                            .build();

                    when(cartRepository.save(any())).thenReturn(cart);
                    when(itemService.getItemValid(any(Long.class))).thenReturn(item);
            }


            // when
            cartService.addCart(user, requestDto);

            // then
            verify(cartRepository, times(1)).save(any(Cart.class));
        }

        @Test
        @DisplayName("비회원 회원카트에 저장")
        void addNoUserCart() throws UnknownHostException {
            // given
            List<Long> items = new ArrayList<>();
            items.add(item.getId());

            List<Integer> quantities = new ArrayList<>();
            quantities.add(1);

            CartRequestDto requestDto = CartRequestDto.builder()
                    .itemId(items)
                    .quantity(quantities)
                    .build();

            User user = User.builder()
                    .id(1L)
                    .build();

            Cart setCart = Cart.builder()
                    .cartId(1L)
                    .item(item)
                    .status(Status.EXIST)
                    .address(user.getAddress())
                    .quantity(1)
                    .price(1)
                    .user(user)
                    .build();

            CartResponseDto responseDto = CartResponseDto.builder()
                    .price(setCart.getPrice())
                    .quantity(setCart.getQuantity())
                    .item(setCart.getItem())
                    .date(LocalDate.now())
                    .id(String.valueOf(setCart.getCartId()))
                    .build();

            List<CartResponseDto> responseDtos = new ArrayList<>();
            responseDtos.add(responseDto);


            for (int i = 0; i < items.size(); i++) {

                Cart cart = Cart.builder()
                        .item(item)
                        .status(Status.EXIST)
                        .address(user.getAddress())
                        .quantity(requestDto.getQuantity().get(i))
                        .price(item.getPrice() * requestDto.getQuantity().get(i))
                        .user(user)
                        .build();

                when(cartRedisService.getAll()).thenReturn(responseDtos);
                when(cartRepository.save(any())).thenReturn(cart);
                when(itemService.getItemValid(any(Long.class))).thenReturn(item);
            }

            // when
            cartService.addNoUserCart(user);

            // then
            verify(cartRepository, times(1)).save(any(Cart.class));
        }

        @Test
        @DisplayName("이미 카트에 있는 아이템 등록 성공")
        void addCartSuccess2() {
            // given
            List<Long> items = new ArrayList<>();
            items.add(item.getId());

            List<Integer> quantities = new ArrayList<>();
            quantities.add(3);

            CartRequestDto requestDto = CartRequestDto.builder()
                    .itemId(items)
                    .quantity(quantities)
                    .build();

            User user = User.builder()
                    .id(1L)
                    .build();

            Cart cart = Cart.builder()
                    .cartId(1L)
                    .item(item)
                    .user(user)
                    .status(Status.EXIST)
                    .address("addre")
                    .price(item.getPrice())
                    .quantity(1)
                    .build();

            when(cartRepository.findByitemIdAndUser(any(Long.class),any(User.class))).thenReturn(Optional.ofNullable(cart));
            when(itemService.getItemValid(any(Long.class))).thenReturn(item);

            cart.update(requestDto,item);

            // when
            cartService.addCart(user, requestDto);

            // then
            assertThat(cart.getQuantity()).isEqualTo(3);
        }

        @Test
        @DisplayName("비회원 이미 카트에 있는 아이템 등록 성공")
        void addCartNoUser2() throws UnknownHostException {
            // given
            User user = User.builder()
                    .id(1L)
                    .build();

            Cart cart = Cart.builder()
                    .cartId(1L)
                    .item(item)
                    .user(user)
                    .status(Status.EXIST)
                    .address("addre")
                    .price(item.getPrice())
                    .quantity(1)
                    .build();

            CartResponseDto responseDto = CartResponseDto.builder()
                    .price(cart.getPrice())
                    .quantity(cart.getQuantity())
                    .item(cart.getItem())
                    .date(LocalDate.now())
                    .id(String.valueOf(cart.getCartId()))
                    .build();

            List<CartResponseDto> responseDtos = new ArrayList<>();
            responseDtos.add(responseDto);

            when(cartRedisService.getAll()).thenReturn(responseDtos);
            when(cartRepository.findByitemIdAndUser(any(Long.class),any(User.class))).thenReturn(Optional.ofNullable(cart));
            when(itemService.getItemValid(any(Long.class))).thenReturn(item);


            cart.updateNoUser(responseDto);

            // when
            cartService.addNoUserCart(user);

            // then
            assertThat(cart.getQuantity()).isEqualTo(1);
        }

        @Test
        @DisplayName("상품 삭제되서 장바구니 추가안됨")
        void notExistItemFail() {
            // given
            List<Long> items = new ArrayList<>();
            items.add(notExistItem.getId());

            List<Integer> quantities = new ArrayList<>();
            quantities.add(1);


            CartRequestDto requestDto = CartRequestDto.builder()
                    .itemId(items)
                    .quantity(quantities)
                    .build();

            // when & then
            assertThrows(NullPointerException.class, () -> {
                cartService.addCart(User.builder().build(), requestDto);
            });
        }

        @Test
        @DisplayName("상품 개수가 없어서 장바구니 추가안됨")
        void soldOutItemFail() {
            // given
            List<Long> items = new ArrayList<>();
            items.add(soldOutItem.getId());

            List<Integer> quantities = new ArrayList<>();

            quantities.add(1);
            CartRequestDto requestDto = CartRequestDto.builder()
                    .itemId(items)
                    .quantity(quantities)
                    .build();


            // when & then
            assertThrows(NullPointerException.class, () -> {
                cartService.addCart(User.builder().build(), requestDto);
            });
        }

    }


    @Nested
    class updateCart {
        @Test
        @DisplayName("수정 성공")
        void updateCartSuccess() {
            // given
            User user = User.builder()
                    .build();

            List<Long> items = new ArrayList<>();
            items.add(item.getId());

            List<Integer> quantities = new ArrayList<>();
            quantities.add(3);

            Cart setCart = Cart.builder()
                    .cartId(1L)
                    .item(item)
                    .status(Status.EXIST)
                    .address(user.getAddress())
                    .quantity(1)
                    .price(1)
                    .user(user).build();

            CartRequestDto req = CartRequestDto.builder()
                    .itemId(items)
                    .quantity(quantities)
                    .build();

            UpdateValidResponseDto updateValidResponseDto = UpdateValidResponseDto.builder()
                            .cart(setCart)
                                    .item(item)
                                            .build();

            given(cartConfig.updateVaild(anyLong())).willReturn(updateValidResponseDto);

            // when
            cartService.updateCart(user, req, setCart.getCartId());

            // then
            assertThat(setCart.getQuantity()).isEqualTo(3);
        }
    }

    @Nested
    class deleteCart {
        @Test
        @DisplayName("삭제 성공")
        void deleteCartSuccess() {

            // given
            User user = User.builder()
                    .build();

            Cart setCart = Cart.builder()
                    .cartId(1L)
                    .item(item)
                    .status(Status.EXIST)
                    .address(user.getAddress())
                    .quantity(1)
                    .price(1)
                    .user(user).build();

            given(cartRepository.findById(anyLong())).willReturn(Optional.of(setCart));

            // when
            cartService.deleteCart(user, 1L);

            // then
            assertThat(setCart.getStatus()).isEqualTo(Status.DELETED);
        }
    }

    @Nested
    class getCarts {
        @Test
        @DisplayName("전체 조회 성공")
        void getCartsSuccess() {

            // given
            User user = User.builder()
                    .id(1L)
                    .build();

            Cart setCart = Cart.builder()
                    .cartId(1L)
                    .item(item)
                    .status(Status.EXIST)
                    .address(user.getAddress())
                    .quantity(1)
                    .price(1)
                    .user(user)
                    .build();

            CartResponseDto responseDto = CartResponseDto.builder()
                    .price(setCart.getPrice())
                    .quantity(setCart.getQuantity())
                    .item(setCart.getItem())
                    .date(LocalDate.now())
                    .id(String.valueOf(setCart.getCartId()))
                    .build();

            List<CartResponseDto> responseDtos = new ArrayList<>();
            responseDtos.add(responseDto);

            List<Cart> carts = new ArrayList<>();
            carts.add(setCart);


            List<String> img = new ArrayList<>();
            img.add("img");

            given(userService.getUserEntity(anyLong())).willReturn(user);
            given(itemService.getItemValid(anyLong())).willReturn(item);
            given(cartRepository.findAllByUserAndStatusOrderByCreatedTime(any(User.class),any(Status.class))).willReturn(carts);
            given(awsS3Service.getObjectUrlsForItem(anyLong())).willReturn(img);

            // when
            List<CartResponseDto> cartsRes = cartService.getCarts(user);

            // then
            assertThat(cartsRes.size()).isEqualTo(1);
            assertThat(cartsRes.get(0).getItem()).isEqualTo(item);
        }
    }


}