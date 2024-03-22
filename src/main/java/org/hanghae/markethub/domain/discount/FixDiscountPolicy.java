package org.hanghae.markethub.domain.discount;

import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.global.constant.Role;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class FixDiscountPolicy implements DiscountPolicy{

    private BigDecimal discountFixAmount = new BigDecimal(100);
    @Override
    public BigDecimal discount(User user, int price) {
        if (user.getRole().equals(Role.USER)) {
            return discountFixAmount;
        }
        return BigDecimal.ZERO;
    }
}
