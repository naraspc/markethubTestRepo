package org.hanghae.markethub.domain.cart.repository;

import org.hanghae.markethub.domain.cart.entity.Cart;
import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.global.constant.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Stack;

public interface CartRepository extends JpaRepository<Cart,Long> {

    Optional<Cart> findByitemIdAndUser(Long itemId,User user);
    List<Cart> findAllByUserAndStatusOrderByCreatedTime(User user, Status status);


}
