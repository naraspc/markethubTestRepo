package org.hanghae.markethub.domain.purchase.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hanghae.markethub.domain.cart.entity.Cart;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.domain.purchase.dto.PurchaseRequestDto;
import org.hanghae.markethub.global.constant.Status;
import org.hanghae.markethub.global.date.BaseTimeEntity;

import java.math.BigDecimal;
import java.util.List;


@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
public class Purchase extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String itemName;
    private String email;

    private int quantity;
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private Status status;

    public void setStatusToDelivery() {
        this.status = Status.IN_DELIVERY;
    }
    public void setStatusToComplete() {
        this.status = Status.DELIVERY_COMPLETED;
    }
    public void setStatusToDelete() {
        this.status = Status.DELETED;
    }

    public void setStatusToOrdered() {
        this.status = Status.ORDERED;
    }
}
