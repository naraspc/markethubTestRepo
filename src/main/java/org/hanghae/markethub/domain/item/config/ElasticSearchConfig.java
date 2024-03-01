package org.hanghae.markethub.domain.item.config;

import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.item.entity.ElasticItem;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.domain.item.repository.ElasticSearchRepository;
import org.hanghae.markethub.global.service.AwsS3Service;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ElasticSearchConfig {
	private final ElasticSearchRepository elasticSearchRepository;
	private final AwsS3Service awsS3Service;


	public void handleItemEventForElasticSearch(Item item) {
		syncItemToElasticsearch(item);
	}

	public void syncItemToElasticsearch(Item item) {
		ElasticItem elasticItem = convertToElasticItem(item);
		elasticSearchRepository.save(elasticItem);
	}

	public void deleteItemForElasticSearch(Item item) {
		elasticSearchRepository.delete(convertToElasticItem(item));
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
