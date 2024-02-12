package org.hanghae.markethub.domain.cart.repository;

import org.hanghae.markethub.domain.cart.entity.NoUserCart;
import org.hanghae.markethub.global.constant.Status;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RedisRepository extends CrudRepository<NoUserCart,Long> {

    List<NoUserCart> findAllByIpAndStatus(String ip, Status status);

    NoUserCart findByIpAndItemId(String ip, Long itemId);

    void deleteByIp(String ip);
}
