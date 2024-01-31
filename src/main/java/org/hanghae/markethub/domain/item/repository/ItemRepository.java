package org.hanghae.markethub.domain.item.repository;

import org.hanghae.markethub.domain.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
