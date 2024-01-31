package org.hanghae.markethub.domain.order.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.global.constant.Status;
import org.hanghae.markethub.global.date.BaseTimeEntity;

import java.time.LocalDateTime;

@Entity
@Getter
@RequiredArgsConstructor
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALl)
    private Cart cart;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALl)
    private Item item;
     */
    private Status status;

}
