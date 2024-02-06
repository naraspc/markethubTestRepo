package org.hanghae.markethub.domain.cart.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "nouser")
public class NoUserCart {
    @Id // keys * 로 조회하면 해당 id 전체값이 나온다
    private String id;

    @Indexed // 필드값으로 데이터를 찾을 수 있도록 설정
    private String ip;

    private Long itemId;

    private Long quantity;
}
