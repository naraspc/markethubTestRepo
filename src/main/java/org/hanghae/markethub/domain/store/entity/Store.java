package org.hanghae.markethub.domain.store.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.domain.user.User;
import org.hanghae.markethub.global.Status;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "store")
public class Store {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(name ="user_id")
	@JsonIgnore
	private User user;

	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private Status status;

	@OneToMany(mappedBy = "store")
	@Builder.Default
	private List<Item> items = new ArrayList<>();
}
