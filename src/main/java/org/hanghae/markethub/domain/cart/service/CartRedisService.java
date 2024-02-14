package org.hanghae.markethub.domain.cart.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.cart.config.CartValids;
import org.hanghae.markethub.domain.cart.dto.CartRequestDto;
import org.hanghae.markethub.domain.cart.dto.CartResponseDto;
import org.hanghae.markethub.domain.cart.entity.NoUserCart;
import org.hanghae.markethub.domain.cart.repository.RedisRepository;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.global.constant.Status;
import org.hanghae.markethub.global.service.AwsS3Service;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartRedisService{
    private final RedisRepository redisRepository;
    private final CartValids cartValids;
    private final AwsS3Service awsS3Service;
    @Transactional
    public ResponseEntity<String> save(CartRequestDto requestDto) throws UnknownHostException {
        String ip = String.valueOf(InetAddress.getLocalHost());

        Item item = cartValids.checkItem(requestDto.getItemId().get(0));

        cartValids.validItem(item);

        Optional<NoUserCart> checkCart = redisRepository.findByIpAndItemId(ip, requestDto.getItemId().get(0));

        // java.lang.RuntimeException: java.lang.StackOverflowError
        if (checkCart.isPresent()){
            checkCart.get().update(requestDto,item);
        }else{

           try {
               NoUserCart cart = NoUserCart.builder()
                       .ip(ip)
                       .status(Status.EXIST)
                       .quantity(requestDto.getQuantity().get(0))
                       .item(item)
                       .price(item.getPrice() * requestDto.getQuantity().get(0))
                       .build();
               redisRepository.save(cart);
           }catch (Exception e){
               System.out.println(e.getMessage());
           }


        }

        return ResponseEntity.ok("ok");
    }


    public List<CartResponseDto> getAll() throws UnknownHostException {

        String ip = String.valueOf(InetAddress.getLocalHost());

        return redisRepository.findAllByIpAndStatus(ip,Status.EXIST).stream()
                .map(cart -> CartResponseDto.builder()
                        .id(cart.getIp())
                        .price(cart.getPrice())
                        .item(cartValids.checkItem(cart.getItem().getId()))
                        .img(awsS3Service.getObjectUrlsForItem(cart.getItem().getId()).get(0))
                        .quantity(cart.getQuantity())
                        .build())
                .collect(Collectors.toList());
    }

    public void deleteCart(String cartIp,Long item){

        NoUserCart cart = redisRepository.findByIpAndItemId(cartIp, item).orElse(null);

        redisRepository.delete(cart);

    }

//    @Override
//    public NoUserCart save(CartRequestDto requestDto) throws UnknownHostException {
//        String ip = String.valueOf(InetAddress.getLocalHost());
//
//        Item item = cartValids.checkItem(requestDto.getItemId().get(0));
//
//        NoUserCart cart = NoUserCart.builder()
//                .ip(ip)
//                .status(Status.EXIST)
//                .quantity(requestDto.getQuantity().get(0))
//                .item(item)
//                .price(item.getPrice() * requestDto.getQuantity().get(0))
//                .build();
//
//        return redisRepository.save(cart);
//
//    }
//
//    @Override
//    public List<NoUserCart> findAllByIp(String ip) {
//        return null;
//    }
}
