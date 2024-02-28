package org.hanghae.markethub.domain.item.config;

import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.item.entity.ElasticItem;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.global.service.AwsS3Service;
import org.springframework.context.event.EventListener;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ElasticSearchConfig {
	private final ElasticsearchOperations elasticsearchOperations;
	private final AwsS3Service awsS3Service;

	@EventListener
	public void handleItemEventForElasticSearch(Item item) {
		syncItemToElasticsearch(item);
	}

	public void syncItemToElasticsearch(Item item) {
		ElasticItem elasticItem = convertToElasticItem(item);
		elasticsearchOperations.save(elasticItem);
	}

	public void deleteItemForElasticSearch(Item item) {
		elasticsearchOperations.delete(convertToElasticItem(item));
	}

	private ElasticItem convertToElasticItem(Item item) {
		List<String> pictureUrls = awsS3Service.getObjectUrlsForItem(item.getId());
		return ElasticItem.builder()
				.id(item.getId())
				.itemName(item.getItemName())
				.price(item.getPrice())
				.quantity(item.getQuantity())
				.itemInfo(item.getItemInfo())
				.category(item.getCategory())
				.pictureUrls(pictureUrls)
				.build();
	}
}
