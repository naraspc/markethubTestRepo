package org.hanghae.markethub.domain.picture.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.item.config.ElasticSearchConfig;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.domain.item.service.ItemService;
import org.hanghae.markethub.global.service.AwsS3Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PictureService {
	private final AwsS3Service awsS3Service;
	private final ItemService itemService;
	private final ElasticSearchConfig elasticSearchConfig;

	@Transactional
	public void createPicture(Long itemId, List<MultipartFile> files) throws IOException {
		Item item = itemService.getItemValid(itemId);
		awsS3Service.uploadFiles(files, item);
		elasticSearchConfig.handleItemEventForElasticSearch(item);
	}

	public void deletePicture(Long itemId) throws JsonProcessingException {
		Item item = itemService.getItemValid(itemId);
		awsS3Service.deleteFilesByItemId(item);
		elasticSearchConfig.deleteItemForElasticSearch(item);
	}
}
