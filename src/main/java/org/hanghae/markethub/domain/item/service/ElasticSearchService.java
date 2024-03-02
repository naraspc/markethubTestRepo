package org.hanghae.markethub.domain.item.service;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.item.dto.ItemsResponseDto;
import org.hanghae.markethub.domain.item.entity.ElasticItem;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.global.service.AwsS3Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
public class ElasticSearchService {
	private final ElasticsearchOperations elasticsearchOperations;
	private final AwsS3Service awsS3Service;

	public Page<ItemsResponseDto> searchNativeQuery(String queryText, int pageNumber, int pageSize) {


//		Query query = NativeQuery.builder()
//				.withQuery(QueryBuilders.multiMatch()
//						.query(queryText)
//						.fields("item_info", "item_name")
//						.type(TextQueryType.MostFields)
//						.build()._toQuery())
//				.withPageable(PageRequest.of(pageNumber, pageSize))
//				.withSort(Sort.by(Sort.Order.desc("_score")))
//				.build();

		Query query = NativeQuery.builder()
				.withQuery(QueryBuilders.queryString()
						.query("*"+queryText+"*")
						.fields("item_info", "item_name")
						.type(TextQueryType.MostFields)
						.build()._toQuery())
				.withPageable(PageRequest.of(pageNumber, pageSize))
				.withSort(Sort.by(Sort.Order.desc("_score")))
				.build();
//		Criteria criteria = new Criteria("item_info").contains(queryText).or("item_name").contains(queryText);
//		Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "_score"));
//		Query query = new CriteriaQuery(criteria).setPageable(pageable);

		// ElasticsearchOperations 검색
		SearchHits<ElasticItem> searchHits = elasticsearchOperations.search(query, ElasticItem.class);

		for (SearchHit<ElasticItem> searchHit : searchHits) {
			float score = searchHit.getScore(); // 해당 문서의 스코어 가져오기
			ElasticItem document = searchHit.getContent(); // 해당 문서 가져오기
			System.out.println("Score: " + score); // 스코어 출력
			System.out.println("Document: " + document); // 문서 출력
		}

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
