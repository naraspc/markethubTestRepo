package org.hanghae.markethub.domain.cart.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hanghae.markethub.domain.cart.dto.CartRequestDto;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.domain.user.entity.User;

import org.hanghae.markethub.global.constant.Status;
import org.hanghae.markethub.global.date.BaseTimeEntity;

import java.util.Optional;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "cart")
@AllArgsConstructor
@Builder
public class Cart extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Status status;

//    @Builder
//    public Cart(User user, Item item, int price, int quantity, String address) {
//        this.user = user;
//        this.item = item;
//        this.price = price;
//        this.quantity = quantity;
//        this.address = address;
//    }

    public void update(CartRequestDto requestDto){
        this.item = requestDto.getItem();
        this.quantity = requestDto.getQuantity();
    }
}
