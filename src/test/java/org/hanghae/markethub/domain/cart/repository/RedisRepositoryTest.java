package org.hanghae.markethub.domain.cart.repository;

import jakarta.transaction.Transactional;
import org.hanghae.markethub.domain.cart.entity.NoUserCart;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.domain.item.repository.ItemRepository;
import org.hanghae.markethub.domain.store.entity.Store;
import org.hanghae.markethub.domain.store.repository.StoreRepository;
import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.domain.user.repository.UserRepository;
import org.hanghae.markethub.global.constant.Role;
import org.hanghae.markethub.global.constant.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class RedisRepositoryTest {
    // datajpatest로는 crudrepository를 상속받는 redisRepository가 빈 등록이 안됨
    // 그래서 SpringbootTest로 전체 등록된 bean을 조회해서 사용

    @Autowired
    private RedisRepository redisRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private UserRepository userRepository;

    private Item item;

    private String ip;

    @BeforeEach
    public void cartBuilder() throws UnknownHostException {

        User user = User.builder()
                .email("1234@naver.com")
                .password("1234")
                .name("lee")
                .phone("010-1234")
                .address("서울시")
                .role(Role.ADMIN)
                .status(Status.EXIST)
                .build();

        userRepository.save(user);

        Store store = Store.builder()
                .user(user)
                .status(Status.EXIST)
                .build();

        Item item1 = Item.builder()
                .itemName("노트북")
                .price(500000)
                .quantity(5)
                .user(user)
                .itemInfo("구형 노트북")
                .category("가전 제품")
                .status(Status.EXIST)
                .store(store)
                .build();

        storeRepository.save(store);
        item = itemRepository.save(item1);

        ip = String.valueOf(InetAddress.getLocalHost());
        NoUserCart noUserCart = NoUserCart.builder()
                .itemId(item.getId())
                .ip(ip)
                .status(Status.EXIST)
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .build();

        redisRepository.save(noUserCart);

    }

    @Test
    void findAllByIpAndStatus() {
        // 현재 발생하는 오류
        // org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'org.hanghae.markethub.domain.cart.repository.RedisRepositoryTest': Unsatisfied dependency expressed through field 'redisRepository': No qualifying bean of type 'org.hanghae.markethub.domain.cart.repository.RedisRepository' available: expected at least 1 bean which qualifies as autowire candidate. Dependency annotations: {@org.springframework.beans.factory.annotation.Autowired(required=true)}

        // given & when
        List<NoUserCart> all = redisRepository.findAllByIpAndStatus(ip, Status.EXIST);

        // then
        assertThat(all.get(0).getIp()).isEqualTo(ip);
    }

    @Test
    void findByIpAndItemId() {
        // given & when
        Optional<NoUserCart> all = redisRepository.findByIpAndItemId(ip, item.getId());

        // then
        assertThat(all.get().getIp()).isEqualTo(ip);
        assertThat(all.get().getPrice()).isEqualTo(500000);
    }

//    @Test
//    void findByIp() {
//        // given & when
//        System.out.println(ip);
//        NoUserCart all = redisRepository.findByIp(ip);
//        System.out.println(all.getIp());
//        // then
//        assertThat(all.getIp()).isEqualTo(ip);
//    }
}