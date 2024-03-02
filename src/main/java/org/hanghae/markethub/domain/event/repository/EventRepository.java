package org.hanghae.markethub.domain.event.repository;

import org.hanghae.markethub.domain.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
	Event findByItemId(Long itemId);
}
