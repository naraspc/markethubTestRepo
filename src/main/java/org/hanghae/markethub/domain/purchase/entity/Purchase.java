package org.hanghae.markethub.domain.purchase.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hanghae.markethub.domain.cart.entity.Cart;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.global.constant.Status;
import org.hanghae.markethub.global.date.BaseTimeEntity;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Purchase extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


<<<<<<< HEAD:src/main/java/org/hanghae/markethub/domain/order/entity/Purchase.java
//    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL)
//    private List<Cart> cart;
=======
    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL)
    private List<Cart> cart;
>>>>>>> ec1a5e20d32624d653068c0c3de874f9e56fb96e:src/main/java/org/hanghae/markethub/domain/purchase/entity/Purchase.java

    @OneToOne
    private Item item;

    @Enumerated(EnumType.STRING)
    private Status status;


}
