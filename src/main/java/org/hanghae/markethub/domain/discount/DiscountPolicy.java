package org.hanghae.markethub.domain.discount;

import org.hanghae.markethub.domain.user.entity.User;

import java.math.BigDecimal;

public interface DiscountPolicy {

    /*
     * @return 할인 대상 금액
     */
    BigDecimal discount (User user, BigDecimal price);
}
