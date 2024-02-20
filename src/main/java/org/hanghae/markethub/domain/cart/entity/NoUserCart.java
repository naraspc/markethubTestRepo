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
// value 값(redis keyspace)에 특정한 값을 넣어줌으로써 추후 해당 데이터에 대한 key가 생성될 때 prefix를 지정할 수 있으며,

public class NoUserCart {
    @Id // keys * 로 조회하면 해당 id 전체값이 나온다
    // @Id 어노테이션을 통해 prefix:구분자 형태(keyspace:@id)로 데이터에 대한 키를 저장하여 각 데이터를 구분
    private Long id;

    @Indexed // 필드값으로 데이터를 찾을 수 있도록 설정
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

