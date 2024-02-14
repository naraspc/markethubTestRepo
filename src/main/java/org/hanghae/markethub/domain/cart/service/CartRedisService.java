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
        // 실제 item객체를 넣어주니 redis에 넘어가기전에 JVM의 heap쪽 메모리가 다 사용되서 오류가 발생되어 저장되지 않았다
        // 그래서 실제 id값만 넣어주고 호출할때는 id값으로 실제 db에서 한번더 호출해서 가져오는 작업을 진행
        if (checkCart.isPresent()){
            checkCart.get().update(requestDto,item);
        }else{

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

        return ResponseEntity.ok("ok");
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

//    @Transactional
    public void deleteCart(CartRequestDto requestDto){
        NoUserCart noUserCart = redisRepository.findByIpAndItemId(requestDto.getCartIp(), requestDto.getItemId().get(0)).orElse(null);
//        noUserCart.delete();
        redisRepository.delete(noUserCart);

    }

}
