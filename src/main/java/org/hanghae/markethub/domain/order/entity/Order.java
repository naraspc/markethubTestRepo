//package org.hanghae.markethub.domain.order.entity;
//
//import jakarta.persistence.*;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.RequiredArgsConstructor;
//import org.hanghae.markethub.domain.cart.entity.Cart;
//import org.hanghae.markethub.domain.item.entity.Item;
//import org.hanghae.markethub.global.constant.Status;
//import org.hanghae.markethub.global.date.BaseTimeEntity;
//
//import java.util.List;
//
//@Entity
//@Getter
//@RequiredArgsConstructor
//@Table(name = "order")
//public class Order extends BaseTimeEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private final Long id;
//
//
//    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
//    private final List<Cart> cart;
//
//    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
//    private final List<Item> item;
//
//    private final Status status;
//
//    @Builder
//    public Order(Long id, Status status, List<Cart> cart, List<Item> item) {
//        this.id = id;
//        this.status = status;
//        this.cart = cart;
//        this.item = item;
//    }
//    public static updateOrder() {
//
//    }
//}
