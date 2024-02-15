package org.hanghae.markethub.domain.cart.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.cart.config.CartValids;
import org.hanghae.markethub.domain.cart.dto.CartRequestDto;
import org.hanghae.markethub.domain.cart.dto.CartResponseDto;
import org.hanghae.markethub.domain.cart.entity.NoUserCart;
import org.hanghae.markethub.domain.cart.repository.RedisRepository;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.domain.item.service.ItemService;
import org.hanghae.markethub.global.constant.Status;
import org.hanghae.markethub.global.service.AwsS3Service;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartRedisService{
    private final RedisRepository redisRepository;
    private final CartValids cartValids;
    private final AwsS3Service awsS3Service;
    private final ItemService itemService;
//    @Transactional
    public ResponseEntity<String> save(CartRequestDto requestDto) throws UnknownHostException {
        String ip = String.valueOf(InetAddress.getLocalHost());

//        Item item = cartValids.checkItem(requestDto.getItemId().get(0));
        Item item = itemService.getItemValid(requestDto.getItemId().get(0));

        cartValids.validItem(item);

        NoUserCart checkCart = redisRepository.findByIpAndItemId(ip, requestDto.getItemId().get(0)).orElse(null);

        if (checkCart != null){
//            checkCart.get().update(requestDto,item);
            redisRepository.delete(checkCart);
        }

        saveCart(requestDto, ip, item);

        return ResponseEntity.ok("ok");
    }




    public List<CartResponseDto> getAll() throws UnknownHostException {

        String ip = String.valueOf(InetAddress.getLocalHost());

        return redisRepository.findAllByIpAndStatus(ip,Status.EXIST).stream()
                .map(cart -> CartResponseDto.builder()
                        .id(cart.getIp())
                        .price(cart.getPrice())
                        .item(itemService.getItemValid(cart.getItemId()))
                        .img(awsS3Service.getObjectUrlsForItem(cart.getItemId()).get(0))
                        .quantity(cart.getQuantity())
                        .build())
                .collect(Collectors.toList());
    }

//    @Transactional
    public void deleteCart(CartRequestDto requestDto){
        NoUserCart noUserCart = redisRepository.findByIpAndItemId(requestDto.getCartIp(), requestDto.getItemId().get(0)).orElse(null);
//        noUserCart.delete();
        redisRepository.delete(noUserCart);

    }

//    @Transactional
    public void updateCart(CartRequestDto requestDto) {
        NoUserCart noUserCart = redisRepository.findByIp(requestDto.getCartIp());
//        Item item = cartValids.checkItem(noUserCart.getItemId());
        Item item = itemService.getItemValid(noUserCart.getItemId());
        if (noUserCart == null){
            throw new NullPointerException("해당 아이템이 카트에 존재하지않습니다");
        }else {
            redisRepository.delete(noUserCart);
        }

        saveCart(requestDto,noUserCart.getIp(),item);

//        try {
//            NoUserCart cart = NoUserCart.builder()
//                    .ip(noUserCart.getIp())
//                    .status(Status.EXIST)
//                    .quantity(requestDto.getQuantity().get(0))
//                    .itemId(item.getId())
//                    .price(item.getPrice() * requestDto.getQuantity().get(0))
//                    .build();
//            redisRepository.save(cart);
//        }catch (Exception e){
//            System.out.println(e.getMessage());
//        }
    }

    private void saveCart(CartRequestDto requestDto, String ip, Item item) {
        try {
            NoUserCart cart = NoUserCart.builder()
                    .ip(ip)
                    .status(Status.EXIST)
                    .quantity(requestDto.getQuantity().get(0))
                    .itemId(item.getId())
                    .price(item.getPrice() * requestDto.getQuantity().get(0))
                    .build();
            redisRepository.save(cart);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
