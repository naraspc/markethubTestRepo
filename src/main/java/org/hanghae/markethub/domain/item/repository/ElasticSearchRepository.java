package org.hanghae.markethub.domain.item.repository;

import org.hanghae.markethub.domain.item.entity.ElasticItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ElasticSearchRepository extends ElasticsearchRepository<ElasticItem, Long> {
	Page<ElasticItem> findByItemInfoContainingOrItemNameContaining(String itemInfoKeyword, String itemNameKeyword, Pageable pageable);
}
