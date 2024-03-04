package org.hanghae.markethub.domain.cart.service;

import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.cart.config.CartConfig;
import org.hanghae.markethub.domain.cart.dto.CartRequestDto;
import org.hanghae.markethub.domain.cart.dto.CartResponseDto;
import org.hanghae.markethub.domain.cart.entity.NoUserCart;
import org.hanghae.markethub.domain.cart.repository.RedisRepository;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.domain.item.repository.ItemRepository;
import org.hanghae.markethub.domain.item.service.ItemService;
import org.hanghae.markethub.domain.store.entity.Store;
import org.hanghae.markethub.global.constant.Status;
import org.hanghae.markethub.global.service.AwsS3Service;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartRedisService{
    private final RedisRepository redisRepository;
    private final CartConfig cartConfig;
    private final AwsS3Service awsS3Service;
    private final ItemService itemService;


    public ResponseEntity<String> save(CartRequestDto requestDto) throws UnknownHostException {
        String ip = String.valueOf(InetAddress.getLocalHost());

        Item item = itemService.getItemValid(requestDto.getItemId().get(0));

        cartConfig.validItem(item);

        redisRepository.findByIpAndItemId(ip, item.getId()).ifPresent(redisRepository::delete);

        saveCart(requestDto, ip, item);

        return ResponseEntity.ok("ok");
    }


    @Transactional(readOnly = true)
    public List<CartResponseDto> getAll() throws UnknownHostException {

        String ip = String.valueOf(InetAddress.getLocalHost());

        return redisRepository.findAllByIpAndStatus(ip,Status.EXIST).stream()
                .map(cart -> CartResponseDto.builder()
                        .id(cart.getIp())
                        .price(cart.getPrice())
                        .item(itemService.getItemValid(cart.getItem().getId()))
                        .img(awsS3Service.getObjectUrlsForItem(cart.getItem().getId()).get(0))
                        .quantity(cart.getQuantity())
                        .build())
                .collect(Collectors.toList());
    }


    public ResponseEntity<String> deleteCart(CartRequestDto requestDto) {

        deleteData(requestDto.getCartIp(), requestDto.getItemId().get(0));

        return ResponseEntity.ok("ok");
    }

    public ResponseEntity<String> delete(CartResponseDto req){
        deleteData(req.getId(), req.getItem().getId());

        return ResponseEntity.ok("ok");
    }

    private void deleteData(String requestDto, Long itemId) {
        Item item = itemService.getItemValid(itemId);
        NoUserCart noUserCart = redisRepository.findByIpAndItemId(requestDto, item.getId()).orElse(null);
        redisRepository.delete(noUserCart);
    }


    public ResponseEntity<String> updateCart(CartRequestDto requestDto) {
        NoUserCart noUserCart = redisRepository.findByIpAndItemId(requestDto.getCartIp(), requestDto.getItemId().get(0)).orElseThrow();
        Item item = itemService.getItemValid(noUserCart.getItem().getId());
        if (noUserCart == null){
            throw new NullPointerException("해당 아이템이 카트에 존재하지않습니다");
        }else {
            redisRepository.delete(noUserCart);
        }

        saveCart(requestDto,requestDto.getCartIp(),item);

        return ResponseEntity.ok("ok");

    }
    //

    private void saveCart(CartRequestDto requestDto, String ip, Item item) {
        try {
            NoUserCart cart = NoUserCart.builder()
                    .ip(ip)
                    .status(Status.EXIST)
                    .quantity(requestDto.getQuantity().get(0))
                    .item(item)
                    .itemId(item.getId())
                    .price(item.getPrice() * requestDto.getQuantity().get(0))
                    .build();
            redisRepository.save(cart);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
