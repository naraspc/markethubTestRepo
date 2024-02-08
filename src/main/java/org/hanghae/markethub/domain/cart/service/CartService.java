package org.hanghae.markethub.domain.cart.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.cart.config.CartValids;
import org.hanghae.markethub.domain.cart.dto.CartRequestDto;
import org.hanghae.markethub.domain.cart.dto.CartResponseDto;
import org.hanghae.markethub.domain.cart.dto.UpdateValidResponseDto;
import org.hanghae.markethub.domain.cart.entity.Cart;
import org.hanghae.markethub.domain.cart.repository.CartRepository;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.global.constant.Status;
import org.hanghae.markethub.global.service.AwsS3Service;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartValids cartValids;
    private final AwsS3Service awsS3Service;

    @Transactional
    public ResponseEntity<String> addCart(User user, CartRequestDto requestDto){

        User validUser = cartValids.validUser(user.getId());

        Item item = cartValids.checkItem(requestDto.getItemId().get(0));

        cartValids.validItem(item);

        Optional<Cart> checkCart = cartRepository.findByitemIdAndUser(item.getId(),validUser);
            if (checkCart.isPresent()) {
                if (item.getQuantity() < requestDto.getQuantity().get(0)){
                    throw new IllegalArgumentException("상품의 개수를 넘어서 담을수가 없습니다.");
                }

                if (checkCart.get().getStatus().equals(Status.EXIST)){
                    checkCart.get().update(requestDto,item);
                    cartRepository.save(checkCart.get());
                }else{
                    checkCart.get().updateDelete(requestDto,item);
                }
            }else {
                Cart cart = Cart.builder()
                        .item(item)
                        .status(Status.EXIST)
                        .address(validUser.getAddress())
                        .quantity(requestDto.getQuantity().get(0))
                        .price(item.getPrice() * requestDto.getQuantity().get(0))
                        .user(validUser)
                        .build();

                cartRepository.save(cart);
            }

//        for (int i = 0; i < items.size(); i++){
//            Optional<Cart> checkCart = cartRepository.findByitemId(items.get(i).getId());
//            if (checkCart.isPresent()) {
//                checkCart.get().update(requestDto);
//                cartRepository.save(checkCart.get());
//            } else {
//                Cart cart = Cart.builder()
//                        .item(items.get(i))
//                        .status(Status.EXIST)
//                        .address(tempUser.getAddress())
//                        .quantity(requestDto.getQuantity().get(i))
//                        .price(items.get(i).getPrice() * requestDto.getQuantity().get(i))
//                        .user(tempUser)
//                        .build();
//
//                cartRepository.save(cart);
//            }
//        }
        return ResponseEntity.ok("Success Cart");
    }

    @Transactional
    public List<CartResponseDto> updateCart(User user, CartRequestDto requestDto,Long cartId) {

        UpdateValidResponseDto valids = cartValids.updateVaild(cartId);

        cartValids.validItem(valids.getItem());

        valids.getCart().updateCart(requestDto,valids.getItem());

        return getCarts(user);
    }

    @Transactional
    public List<CartResponseDto> deleteCart(User user,Long cartId){

        Cart cart = cartRepository.findById(cartId).orElseThrow(null);
        cart.delete();

        return getCarts(user);
    }

    public List<CartResponseDto> getCarts(User user) throws NullPointerException{

        User validUser = cartValids.validUser(user.getId());

            return cartRepository.findAllByUserAndStatusOrderByCreatedTime(validUser,Status.EXIST).stream()
                    .map(cart -> CartResponseDto.builder()
                            .id(cart.getCartId())
                            .price(cart.getPrice())
                            .date(LocalDate.from(cart.getCreatedTime()))
                            .item(cartValids.checkItem(cart.getItem().getId()))
                            .img(awsS3Service.getObjectUrlsForItem(cart.getItem().getId()).get(0))
                            .quantity(cart.getQuantity())
                            .build())
                    .collect(Collectors.toList());
    }

}
