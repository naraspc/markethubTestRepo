package org.hanghae.markethub.domain.store.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.global.constant.Status;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Store {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name ="user_id")
	private User user;

	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private Status status;

	public void deleteStore() {
		this.status = Status.DELETED;
	}
}
