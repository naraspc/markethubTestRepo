package org.hanghae.markethub.domain.cart.service;

import org.hanghae.markethub.domain.cart.dto.CartRequestDto;
import org.hanghae.markethub.domain.cart.dto.CartResponseDto;
import org.hanghae.markethub.domain.cart.entity.NoUserCart;

import java.net.UnknownHostException;
import java.util.List;

public interface NoUserCartService {

    NoUserCart save(CartRequestDto requestDto) throws UnknownHostException;

    List<NoUserCart> findAllByIp(String ip);
}
