package org.hanghae.markethub.domain.cart.repository;

import org.hanghae.markethub.domain.cart.entity.NoUserCart;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RedisRepository extends CrudRepository<NoUserCart,Long> {

    List<NoUserCart> findAllByIp(String ip);

    NoUserCart findByIp(String ip);
}
