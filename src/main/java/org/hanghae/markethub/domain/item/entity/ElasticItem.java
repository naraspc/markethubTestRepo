package org.hanghae.markethub.domain.item.entity;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.util.List;

@Document(indexName = "item")
@Data
@Builder
@Setting(replicas = 0)
public class ElasticItem {
	@Id
	private Long id;

	@Field(name = "item_name", type = FieldType.Text)
	private String itemName;

	@Field(name = "price", type = FieldType.Integer)
	private int price;

	@Field(name = "quantity", type = FieldType.Integer)
	private int quantity;

	@Field(name = "item_info",type = FieldType.Text)
	private String itemInfo;

	private String category;

	private List<String> pictureUrls;
}
