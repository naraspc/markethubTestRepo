package org.hanghae.markethub.domain.item.repository;

import org.hanghae.markethub.domain.item.entity.ElasticItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


public interface ElasticSearchRepository extends ElasticsearchRepository<ElasticItem, Long> {

	@Query("{\"bool\": {\"must\": [{\"match\": {\"itemName\": \"?0\"}}]}}")
	Page<ElasticItem> findByItemNameContaining(String keyword, Pageable pageable);
}
