package org.hanghae.markethub.domain.purchase.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hanghae.markethub.global.constant.Status;
import org.hanghae.markethub.global.date.BaseTimeEntity;

import java.math.BigDecimal;


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
    private Long itemId;
    private String impUid;

    @Enumerated(EnumType.STRING)
    private Status status;

    public void setStatusToDelivery() {
        this.status = Status.IN_DELIVERY;
    }
    public void setStatusToComplete() {
        this.status = Status.DELIVERY_COMPLETE;
    }
    public void setStatusToDelete() {
        this.status = Status.DELETED;
    }

    public void setStatusToOrderComplete() {
        this.status = Status.ORDER_COMPLETE;
    }

    public void setItemUidByOrederd(String itemUid) {
        this.impUid = itemUid;
    }

    public void setStatusToCancelled() {
        this.status = Status.CANCELLED;
    }

}
