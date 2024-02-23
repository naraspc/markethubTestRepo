package org.hanghae.markethub.domain.cart.repository;

import org.hanghae.markethub.domain.cart.entity.NoUserCart;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.global.constant.Status;
import org.springframework.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;

public interface RedisRepository extends CrudRepository<NoUserCart,Long> {

    List<NoUserCart> findAllByIpAndStatus(String ip, Status status);

    Optional<NoUserCart> findByIpAndItem(String ip, Item item);

    NoUserCart findByIp(String cartIp);
}
