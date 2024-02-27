package org.hanghae.markethub.domain.item.service;

import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.item.entity.ElasticItem;
import org.hanghae.markethub.domain.item.repository.ElasticSearchRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ElasticSearchService {
	private final ElasticSearchRepository elasticSearchRepository;

	public Page<ElasticItem> findByItemName(String keyword, Pageable pageable) {
		return elasticSearchRepository.findByItemNameContaining(keyword, pageable);
	}
}
