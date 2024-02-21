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

//	@Query("SELECT p FROM Item p WHERE p.itemName = :category AND p.status = 'EXIST'")
//	List<Item> findByItemName(@Param("itemName")String itemName);

//	@Query("SELECT p FROM Item p WHERE p.itemName LIKE %:itemName% AND p.status = 'EXIST'")
//	List<Item> findByItemNameContaining(@Param("itemName") String itemName);

	@Query("SELECT DISTINCT p FROM Item p LEFT JOIN FETCH p.pictures WHERE p.itemName LIKE %:itemName% AND p.status = 'EXIST'")
	List<Item> findByItemNameContaining(@Param("itemName") String itemName);

	@Query("SELECT p FROM Item p WHERE p.category = :category AND p.store.id = :storeId AND p.status = 'EXIST'")
	List<Item> findByCategoryAndStoreId(@Param("category") String category, @Param("storeId") Long storeId);

	@Query("SELECT i FROM Item i WHERE i.status = 'EXIST'")
	List<Item> findAll();

	@Query("SELECT p FROM Item p WHERE p.user.id = :userId AND p.status = 'EXIST'")
	List<Item> findByUserId(@Param("userId")Long userId);

}
