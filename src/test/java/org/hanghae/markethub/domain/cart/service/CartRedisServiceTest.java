package org.hanghae.markethub.domain.cart.service;

import org.hanghae.markethub.domain.cart.config.CartConfig;
import org.hanghae.markethub.domain.cart.dto.CartRequestDto;
import org.hanghae.markethub.domain.cart.dto.CartResponseDto;
import org.hanghae.markethub.domain.cart.entity.NoUserCart;
import org.hanghae.markethub.domain.cart.repository.RedisRepository;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.domain.item.service.ItemService;
import org.hanghae.markethub.global.constant.Status;
import org.hanghae.markethub.global.service.AwsS3Service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

import static org.assertj.core.api.Assertions.*;

//@Transactional
//@SpringBootTest
@ExtendWith(MockitoExtension.class)
class CartRedisServiceTest {

//    @Autowired
    @Mock
    private RedisRepository redisRepository;

    @Mock
    private CartConfig cartConfig;

    @Mock
    private ItemService itemService;

    @Mock
    private AwsS3Service awsS3Service;

    @InjectMocks
    private CartRedisService cartRedisService;

    @Test
    void save() throws UnknownHostException {
        // given
        Item item = Item.builder()
                .id(1L)
                .itemInfo("test")
                .itemName("name")
                .price(1111)
                .status(Status.EXIST)
                .quantity(11111)
                .build();

        List<Long> itemId = new ArrayList<>();
        itemId.add(item.getId());

        List<Integer> quantities = new ArrayList<>();
        quantities.add(1);

        String ip = String.valueOf(InetAddress.getLocalHost());

        NoUserCart cart = NoUserCart.builder()
                .ip(ip)
                .quantity(11)
                .itemId(item.getId())
                .build();

        CartRequestDto requestDto = CartRequestDto.builder()
                        .itemId(itemId)
                                .cartIp("Ip")
                                        .quantity(quantities)
                                                .build();

        when(redisRepository.save(any(NoUserCart.class))).thenReturn(cart);
        when(itemService.getItemValid(anyLong())).thenReturn(item);

        // when

        ResponseEntity<String> save = cartRedisService.save(requestDto);
        // then
        assertThat(save).isEqualTo(ResponseEntity.ok("ok"));
    }

    @Test
    void getAll() throws UnknownHostException {

        // given

        Item item = Item.builder()
                .id(1L)
                .itemInfo("test")
                .itemName("name")
                .price(1)
                .status(Status.EXIST)
                .quantity(11111)
                .build();

        String ip = String.valueOf(InetAddress.getLocalHost());

        NoUserCart cart = NoUserCart.builder()
                .ip(ip)
                .quantity(11)
                .price(11)
                .itemId(item.getId())
                .status(Status.EXIST)
                .build();

        List<NoUserCart> carts = new ArrayList<>();
        carts.add(cart);

        List<String> img = new ArrayList<>();
        img.add("img");

        when(redisRepository.findAllByIpAndStatus(anyString(),any(Status.class))).thenReturn(carts);
        when(itemService.getItemValid(anyLong())).thenReturn(item);
        when(awsS3Service.getObjectUrlsForItem(anyLong())).thenReturn(img);

        List<CartResponseDto> responseDtos = new ArrayList<>();
        CartResponseDto responseDto = CartResponseDto.builder()
                .price(cart.getPrice())
                .quantity(cart.getQuantity())
                .item(item)
                .date(LocalDate.now())
                .id(cart.getIp())
                .build();

        responseDtos.add(responseDto);

        // when
        List<CartResponseDto> all = cartRedisService.getAll();

        // then
        assertThat(all.size()).isEqualTo(1);
        assertThat(all.get(0).getItem()).isEqualTo(item);
        assertThat(all.get(0).getPrice()).isEqualTo(11);

    }

    @Test
    void delete() throws UnknownHostException {

        // given
        Item item = Item.builder()
                .id(1L)
                .itemInfo("test")
                .itemName("name")
                .price(1111)
                .status(Status.EXIST)
                .quantity(11111)
                .build();

        String ip = String.valueOf(InetAddress.getLocalHost());

        NoUserCart cart = NoUserCart.builder()
                .ip(ip)
                .quantity(11)
                .itemId(item.getId())
                .status(Status.EXIST)
                .build();

        List<Long> itemId = new ArrayList<>();
        itemId.add(item.getId());

        List<Integer> quantities = new ArrayList<>();
        quantities.add(1);

        CartRequestDto requestDto = CartRequestDto.builder()
                .itemId(itemId)
                .cartIp("Ip")
                .quantity(quantities)
                .build();

        when(redisRepository.findByIpAndItemId(anyString(),anyLong())).thenReturn(Optional.ofNullable(cart));

        // when
        ResponseEntity<String> response = cartRedisService.deleteCart(requestDto);

        // then
        assertThat(response).isEqualTo(ResponseEntity.ok("ok"));

    }
}