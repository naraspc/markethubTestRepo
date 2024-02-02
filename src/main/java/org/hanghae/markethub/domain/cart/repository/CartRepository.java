package org.hanghae.markethub.domain.cart.repository;

import org.hanghae.markethub.domain.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart,Long> {

    Optional<Cart> findByitemId(Long itemId);
}
