package org.hanghae.markethub.domain.cart.service;

import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.cart.config.CartValids;
import org.hanghae.markethub.domain.cart.dto.CartRequestDto;
import org.hanghae.markethub.domain.cart.entity.NoUserCart;
import org.hanghae.markethub.domain.cart.repository.RedisRepository;
import org.hanghae.markethub.domain.item.entity.Item;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartRedisService implements NoUserCartService{
    private final RedisRepository redisRepository;

    @Override
    public NoUserCart save(NoUserCart cart) {
        return redisRepository.save(cart);
    }

    @Override
    public List<NoUserCart> findAllByIp(String ip) {
        return redisRepository.findAllByIp(ip);
    }
}
