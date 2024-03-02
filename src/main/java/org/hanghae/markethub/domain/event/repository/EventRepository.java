package org.hanghae.markethub.domain.event.repository;

import org.hanghae.markethub.domain.event.entity.Event;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EventRepository extends CrudRepository<Event, Long> {
	Event findByItemId(Long itemId);
	List<Event> findAll();
}
