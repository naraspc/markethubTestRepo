package org.hanghae.markethub.domain.cart.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.global.constant.Status;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "nouser",timeToLive = 10)
public class NoUserCart {
    @Id
    private Long id;

    @Indexed
    private String ip;

    @Indexed
    private Long itemId;

    private Item item;

    private int price;

    private int quantity;

    @Indexed
    private Status status;
}

