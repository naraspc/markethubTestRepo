package org.hanghae.markethub.domain.cart.service;

import org.hanghae.markethub.domain.cart.entity.NoUserCart;

import java.util.List;

public interface NoUserCartService {
     NoUserCart save(NoUserCart cart);
     List<NoUserCart> findAllByIp(String ip);
}
