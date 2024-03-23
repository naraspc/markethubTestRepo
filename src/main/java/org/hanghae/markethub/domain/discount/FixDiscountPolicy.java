package org.hanghae.markethub.domain.discount;

import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.global.constant.Role;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class FixDiscountPolicy implements DiscountPolicy{

    private final BigDecimal discountFixAmount = new BigDecimal(100);
    @Override
    public BigDecimal discount(User user, BigDecimal price) {
        if (user.getRole().equals(Role.USER)) {
            return price.subtract(discountFixAmount);
        }
        return BigDecimal.ZERO;
    }
}
