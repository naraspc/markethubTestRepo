package org.hanghae.markethub.domain.purchase.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hanghae.markethub.domain.cart.entity.Cart;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.global.constant.Status;
import org.hanghae.markethub.global.date.BaseTimeEntity;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Purchase extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToMany(cascade = CascadeType.ALL)
    private List<Cart> cart;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Item item;

    @Enumerated(EnumType.STRING)
    private Status status;

    @NonNull
    private Long totalPrice;

    public void updateTotalPrice(long price) { // 메서드 이름에 UpdatetotalPrice를 명시하여 의도와 다른 동작 예방
        this.totalPrice += price;
    }

    public void setStatusToDelivery() {
        this.status = Status.IN_DELIVERY;
    }
    public void setStatusToComplete() {
        this.status = Status.DELIVERY_COMPLETED;
    }
    public void setStatusToDelete() {
        this.status = Status.DELETED;
    }
}
