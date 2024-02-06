package org.hanghae.markethub.domain.cart.service;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.cart.config.CartValids;
import org.hanghae.markethub.domain.cart.dto.CartRequestDto;
import org.hanghae.markethub.domain.cart.dto.CartResponseDto;
import org.hanghae.markethub.domain.cart.entity.Cart;
import org.hanghae.markethub.domain.cart.repository.CartRepository;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.global.constant.Status;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartValids cartValids;

    // security 도입되면 User변경 해야함
    // 다른 부분을 연결하면 item도 존재하는지 검사넣기
    public ResponseEntity<String> addCart(User user, CartRequestDto requestDto){

        List<Item> items = requestDto.getItem();
        cartValids.validItems(items);

        for (int i = 0; i < items.size(); i++){
            Optional<Cart> checkCart = cartRepository.findByitemId(items.get(i).getId());
            if (checkCart.isPresent()) {
                checkCart.get().update(requestDto);
                cartRepository.save(checkCart.get());
            } else {
                Cart cart = Cart.builder()
                        .item(items.get(i))
                        .status(Status.EXIST)
                        .address(user.getAddress())
                        .quantity(requestDto.getQuantity().get(i))
                        .price(items.get(i).getPrice() * requestDto.getQuantity().get(i))
                        .user(user)
                        .build();

                cartRepository.save(cart);
            }
        }
        return ResponseEntity.ok("Success Cart");
    }

//    public ResponseEntity<String> noLoginAddCart(CartRequestDto requestDto){
//
//        List<Item> items = requestDto.getItem();
//        cartValids.validItems(items);
//
//        // 하나의 value만 저장할 경우
////        ValueOperations<String,String> valueOperations = redisTemplate.opsForValue();
////        valueOperations.set(String.valueOf(item.getId()),item.getItemName());
//
//        // list형식으로 value저장할 경우
//        ListOperations<String,String> listOperations = redisTemplate.opsForList();
//
//        // list말고도 set,hash등 다양한 타입으로 넣어줄 수 있다
//
//        for (Item item : items) {
//            listOperations.leftPush(String.valueOf(item.getId()),item.getItemName());
//        }
//
//        return ResponseEntity.ok("Success Cart");
//    }

    @Transactional
    public ResponseEntity<String> updateCart(User user, CartRequestDto requestDto,Long cartId) {

        cartValids.validItems(requestDto.getItem());

        Cart cart = cartRepository.findById(cartId).orElseThrow(null);
        cart.update(requestDto);

        return ResponseEntity.ok("Success Update Cart");
    }

    public ResponseEntity<String> deleteCart(User user,Long cartId){

        Cart cart = cartRepository.findById(cartId).orElseThrow(null);
        cart.delete();

        return ResponseEntity.ok("Success Delete Cart");
    }

    public List<CartResponseDto> getCarts(User user){

        try{
            return cartRepository.findAllByUser(user).stream()
                    .map(cart -> CartResponseDto.builder()
                            .price(cart.getPrice())
                            .item(cart.getItem())
                            .quantity(cart.getQuantity())
                            .build())
                    .collect(Collectors.toList());
        }catch (Exception e){
            throw new NullPointerException("해당 user의 장바구니에는 아무것도 없습니다");
        }
    }

}
