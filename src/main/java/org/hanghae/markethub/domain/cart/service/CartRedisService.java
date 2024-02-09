package org.hanghae.markethub.domain.cart.service;

import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.cart.config.CartValids;
import org.hanghae.markethub.domain.cart.dto.CartResponseDto;
import org.hanghae.markethub.domain.cart.entity.NoUserCart;
import org.hanghae.markethub.domain.cart.repository.RedisRepository;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.global.constant.Status;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartRedisService{
    private final RedisRepository redisRepository;
    private final CartValids cartValids;


    public NoUserCart save(Long itemId,Long quantities) throws UnknownHostException {
        String ip = String.valueOf(InetAddress.getLocalHost());

        NoUserCart cart = NoUserCart.builder()
                .ip(ip + itemId)
                .quantity(quantities)
                .itemId(itemId)
                .build();

        return redisRepository.save(cart);
    }


    public List<CartResponseDto> findAllByIp() throws UnknownHostException {

        String ip = String.valueOf(InetAddress.getLocalHost());

        return redisRepository.findAllByIp(ip).stream()
                .map(noUserCart -> {

                })

    }

//    public List<CartResponseDto> getCarts(User user) throws NullPointerException{
//
//        User validUser = cartValids.validUser(user.getId());
//
//        return cartRepository.findAllByUserAndStatusOrderByCreatedTime(validUser, Status.EXIST).stream()
//                .map(cart -> CartResponseDto.builder()
//                        .id(cart.getCartId())
//                        .price(cart.getPrice())
//                        .date(LocalDate.from(cart.getCreatedTime()))
//                        .item(cartValids.checkItem(cart.getItem().getId()))
//                        .img(awsS3Service.getObjectUrlsForItem(cart.getItem().getId()).get(0))
//                        .quantity(cart.getQuantity())
//                        .build())
//                .collect(Collectors.toList());
//    }
}
