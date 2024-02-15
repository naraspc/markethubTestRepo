package org.hanghae.markethub.domain.cart.repository;

import jakarta.transaction.Transactional;
import org.hanghae.markethub.domain.cart.entity.NoUserCart;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.domain.item.repository.ItemRepository;
import org.hanghae.markethub.domain.store.entity.Store;
import org.hanghae.markethub.domain.store.repository.StoreRepository;
import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.global.constant.Role;
import org.hanghae.markethub.global.constant.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class RedisRepositoryTest {

    @Autowired
    private RedisRepository redisRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private StoreRepository storeRepository;

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

        List<NoUserCart> all = redisRepository.findAllByIpAndStatus(ip, Status.EXIST);

        assertThat(all.get(0).getIp()).isEqualTo(ip);
        assertThat(all.get(0).getPrice()).isEqualTo(item.getPrice());
    }

    @Test
    void findByIpAndItemId() {
    }

    @Test
    void findByIp() {
    }
}