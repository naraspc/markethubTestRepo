package org.hanghae.markethub.domain.cart.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.cart.config.CartValids;
import org.hanghae.markethub.domain.cart.dto.CartRequestDto;
import org.hanghae.markethub.domain.cart.dto.CartResponseDto;
import org.hanghae.markethub.domain.cart.entity.Cart;
import org.hanghae.markethub.domain.cart.entity.NoUserCart;
import org.hanghae.markethub.domain.cart.repository.RedisRepository;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.global.constant.Status;
import org.hanghae.markethub.global.service.AwsS3Service;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartRedisService{
    private final RedisRepository redisRepository;
    private final CartValids cartValids;
    private final AwsS3Service awsS3Service;


    public ResponseEntity<String> save(CartRequestDto requestDto) throws UnknownHostException {
        String ip = String.valueOf(InetAddress.getLocalHost());

        NoUserCart checkCart = redisRepository.findByIpAndItemId(ip, requestDto.getItemId().get(0));

        Item item = cartValids.checkItem(requestDto.getItemId().get(0));

        if (checkCart != null){
            redisRepository.delete(checkCart);
        }

        NoUserCart cart = NoUserCart.builder()
                .ip(ip)
                .status(Status.EXIST)
                .quantity(requestDto.getQuantity().get(0))
                .itemId(requestDto.getItemId().get(0))
                .price(item.getPrice() * requestDto.getQuantity().get(0))
                .build();

        redisRepository.save(cart);

        return ResponseEntity.ok("Success Cart");
    }


    public List<CartResponseDto> getAll() throws UnknownHostException {

        String ip = String.valueOf(InetAddress.getLocalHost());

        return redisRepository.findAllByIpAndStatus(ip,Status.EXIST).stream()
                .map(cart -> CartResponseDto.builder()
                        .id(cart.getIp())
                        .price(cart.getPrice())
                        .item(cartValids.checkItem(cart.getItemId()))
                        .img(awsS3Service.getObjectUrlsForItem(cart.getItemId()).get(0))
                        .quantity(cart.getQuantity())
                        .build())
                .collect(Collectors.toList());
    }

    public void deleteCart(Long cartId){

        NoUserCart cart = redisRepository.findById(cartId).orElse(null);

        redisRepository.delete(cart);

    }

}
