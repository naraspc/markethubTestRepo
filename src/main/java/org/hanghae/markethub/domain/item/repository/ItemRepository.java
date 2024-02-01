package org.hanghae.markethub.domain.item.repository;

import org.hanghae.markethub.domain.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
	@Query("SELECT p FROM Item p WHERE p.id = :itemId AND p.status = 'EXIST'")
	Optional<Item> findById(@Param("itemId")Long itemId);

	@Query("SELECT i FROM Item i WHERE i.status = 'EXIST'")
	List<Item> findAll();
}
