package org.hanghae.markethub.global.elasticsearch;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.item.entity.ElasticItem;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.domain.item.repository.ElasticSearchRepository;
import org.hanghae.markethub.domain.item.repository.ItemRepository;
import org.hanghae.markethub.global.service.AwsS3Service;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ElasticSearch {
	private final ItemRepository itemRepository;
	private final ElasticSearchRepository elasticSearchRepository;
	private final AwsS3Service awsS3Service;

	@PostConstruct
	public void syncItemToElasticsearch() {
		List<Item> items = itemRepository.findAllNotPageable();
		List<ElasticItem> elasticItems = items.stream()
				.map(item -> ElasticItem.builder()
						.id(item.getId())
						.itemName(item.getItemName())
						.price(item.getPrice())
						.quantity(item.getQuantity())
						.itemInfo(item.getItemInfo())
						.pictureUrls(awsS3Service.getObjectUrlsForItem(item.getId()))
						.build())
				.collect(Collectors.toList());

		elasticSearchRepository.saveAll(elasticItems);
	}
}
