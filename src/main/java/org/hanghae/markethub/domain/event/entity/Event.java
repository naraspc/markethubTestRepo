package org.hanghae.markethub.domain.event.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hanghae.markethub.domain.picture.entity.Picture;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.ArrayList;
import java.util.List;

//@DynamicUpdate
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "event")
public class Event {
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	private Long id;
//
//	@Column(nullable = false)
//	private Long itemId;
//
//	@Column(nullable = false)
//	private int price;
//
//	@Column(nullable = false)
//	private int quantity;

	@Id
	private Long id;

	@Indexed
	private Long itemId;

	private int quantity;

	private int price;

}
