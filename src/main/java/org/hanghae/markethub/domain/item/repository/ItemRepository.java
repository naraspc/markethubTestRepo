package org.hanghae.markethub.domain.item.repository;

import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.global.constant.Status;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
	@NotNull
	@Query("SELECT i FROM Item i WHERE i.id = :itemId AND i.status = 'EXIST'")
	Optional<Item> findById(@Param("itemId")Long itemId);

//	@Query("SELECT p FROM Item p WHERE p.itemName = :category AND p.status = 'EXIST'")
//	List<Item> findByItemName(@Param("itemName")String itemName);

//	@Query("SELECT p FROM Item p WHERE p.itemName LIKE %:itemName% AND p.status = 'EXIST'")
//	List<Item> findByItemNameContaining(@Param("itemName") String itemName);

//	@Query("SELECT DISTINCT p FROM Item p LEFT JOIN FETCH p.pictures WHERE p.itemName LIKE %:itemName% AND p.status = 'EXIST'")
//	Page<Item> findByItemNameContaining(@Param("itemName") String itemName);
//	@Query("SELECT DISTINCT p FROM Item p LEFT JOIN FETCH p.pictures WHERE p.itemName LIKE %:itemName% AND p.status = 'EXIST'")
//	Page<Item> findByItemNameContaining(@Param("itemName") String itemName, Pageable pageable);

	@Query("SELECT DISTINCT i FROM Item i LEFT JOIN Picture p ON p.item.id = i.id AND i.status = 'EXIST' WHERE i.itemName LIKE %:itemName%")
	Page<Item> findByItemNameContaining(@Param("itemName") String itemName, Pageable pageable);

	@Query("SELECT p FROM Item p WHERE p.category = :category AND p.store.id = :storeId AND p.status = 'EXIST'")
	List<Item> findByCategoryAndStoreId(@Param("category") String category, @Param("storeId") Long storeId);

	@Query("SELECT i FROM Item i WHERE i.status = 'EXIST'")
	Page<Item> findAll(Pageable pageable);

	@Query("SELECT DISTINCT i FROM Item i LEFT JOIN Picture p ON i.id = p.item.id WHERE i.status = 'EXIST'")
	List<Item> findAllWithPictures();

	@Query("SELECT i FROM Item i LEFT JOIN Store s ON i.store.id = s.id AND s.user.id = :userId AND i.status = 'EXIST'")
	List<Item> findByUserId(@Param("userId") Long userId);

//	@Query("SELECT p FROM Item p WHERE p.user.id = :userId AND p.status = 'EXIST'")
//	List<Item> findByUserId(@Param("userId")Long userId);

}
