package org.hanghae.markethub.domain.cart.config;

import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.global.constant.Status;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CartValids {
    public void validItems(List<Item> items) {
        for (Item item : items) {
            if (item.getStatus().equals(Status.DELETED) || item.getQuantity() <= 0){
                throw new IllegalArgumentException("해당 상품은 존재하지않으므로 다시 확인해주세요");
            }
        }
    }

    public void validItem(Item item) {

            if (item.getStatus().equals(Status.DELETED) || item.getQuantity() <= 0){
                throw new IllegalArgumentException("해당 상품은 존재하지않으므로 다시 확인해주세요");
            }

    }
}
