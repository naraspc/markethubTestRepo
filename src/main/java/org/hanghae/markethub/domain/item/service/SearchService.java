package org.hanghae.markethub.domain.item.service;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.item.dto.ItemsResponseDto;
import org.hanghae.markethub.domain.item.entity.ElasticItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {
	private final ElasticsearchOperations elasticsearchOperations;

	public Page<ItemsResponseDto> searchNativeQuery(String queryText, int pageNumber, int pageSize) {

		// NativeQuery Build
		Query query = NativeQuery.builder()
				.withQuery(QueryBuilders.multiMatch()
						.query(queryText)
						.fields("item_name", "item_info")
						.type(TextQueryType.MostFields)
						.fuzziness("AUTO")
						.build()._toQuery())
				.withPageable(PageRequest.of(pageNumber, pageSize))
				.build();


		// ElasticsearchOperations 검색
		SearchHits<ElasticItem> searchHits = elasticsearchOperations.search(query, ElasticItem.class);

		// 검색 결과를 ItemsResponseDto 리스트로 변환
		List<ItemsResponseDto> responseDtoList = new ArrayList<>();
		for (SearchHit<ElasticItem> hit : searchHits) {
			responseDtoList.add(ItemsResponseDto.builder()
					.id(hit.getContent().getId())
					.itemName(hit.getContent().getItemName())
					.price(hit.getContent().getPrice())
					.quantity(hit.getContent().getQuantity())
					.itemInfo(hit.getContent().getItemInfo())
					.category(hit.getContent().getCategory())
					.pictureUrls(hit.getContent().getPictureUrls())
					.build());
		}

		// ItemsResponseDto 리스트를 Page 객체로 변환하여 반환
		return new PageImpl<>(responseDtoList, query.getPageable(), searchHits.getTotalHits());
	}
}
