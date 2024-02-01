package org.hanghae.markethub.domain.cart.repository;

import org.hanghae.markethub.domain.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart,Long> {
}
