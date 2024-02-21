package org.hanghae.markethub.domain.cart.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hanghae.markethub.global.constant.Status;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "nouser")

////fsafsaf

public class NoUserCart {
    @Id
    private Long id;

    @Indexed
    private String ip;

    @Indexed
    private Long itemId;

    @Indexed
    private int price;

    @Indexed
    private int quantity;

    @Indexed
    private Status status;
}

