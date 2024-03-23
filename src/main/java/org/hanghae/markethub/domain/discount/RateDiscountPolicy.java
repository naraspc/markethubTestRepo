package org.hanghae.markethub.domain.discount;

import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.global.constant.Role;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Primary
@Component
public class RateDiscountPolicy implements DiscountPolicy{
    @Override
    public BigDecimal discount(User user, BigDecimal price) {
        BigDecimal discount = new BigDecimal(10);
        if (user.getRole().equals(Role.USER)) {
            return price.subtract(price.divide(discount,1, RoundingMode.HALF_EVEN));
        }
        return BigDecimal.ZERO;
    }
}
