package org.hanghae.markethub.domain.picture.repository;

import org.hanghae.markethub.domain.picture.entity.Picture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PictureRepository extends JpaRepository<Picture, Long> {
	List<Picture> findByItemId(Long itemId);
}
