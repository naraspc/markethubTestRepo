package org.hanghae.markethub.domain.item.entity;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

@Document(indexName = "item")
@Getter
@Builder
public class ElasticItem {
	@Id
	private Long id;
	private String itemName;
	private int price;
	private int quantity;
	private String itemInfo;
	private String category;
	private List<String> pictureUrls;
}
