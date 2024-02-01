package org.hanghae.markethub.domain.cart.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hanghae.markethub.domain.purchase.entity.Purchase;
import org.hanghae.markethub.global.date.BaseTimeEntity;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "cart")
public class Cart extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String itemId;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private Long point;

	@ManyToOne
 	@JoinColumn(name = "purchase_id")
	private Purchase purchase;
}
