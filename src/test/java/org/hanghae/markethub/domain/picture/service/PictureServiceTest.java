package org.hanghae.markethub.domain.picture.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.hanghae.markethub.domain.item.config.ElasticSearchConfig;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.domain.item.service.ItemService;
import org.hanghae.markethub.global.service.AwsS3Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@ExtendWith(MockitoExtension.class)
class PictureServiceTest {

	@InjectMocks
	private PictureService pictureService;

	@Mock
	private AwsS3Service awsS3Service;

	@Mock
	ItemService itemService;

	@Mock
	private ElasticSearchConfig elasticSearchConfig;

	@Test
	@DisplayName("사진 등록 성공")
	void createPicture() throws IOException {
		Item mockItem = new Item();
		mockItem.setId(1L);

		List<MultipartFile> files = new ArrayList<>();
		files.add(new MockMultipartFile("file", "test.jpg", "image/jpeg", "test".getBytes()));

		when(itemService.getItemValid(1L)).thenReturn(mockItem);

		pictureService.createPicture(1L, files);

		verify(awsS3Service, times(1)).uploadFiles(eq(files), any(Item.class));
		verify(elasticSearchConfig, times(1)).handleItemEventForElasticSearch(mockItem);
	}


	@Test
	@DisplayName("사진 등록 실패")
	void createPictureFail() throws IOException {
		Item mockItem = new Item();
		mockItem.setId(1L);

		pictureService.createPicture(null, null);
		// null 값으로 호출되었을 때, awsS3Service.uploadFiles 메서드가 호출되지 않았는지 확인합니다.
		verify(awsS3Service, never()).uploadFiles(anyList(), any(Item.class));
		verify(elasticSearchConfig, never()).handleItemEventForElasticSearch(mockItem);
	}

	@Test
	@DisplayName("사진 삭제 성공")
	void deletePicture() throws JsonProcessingException {
		Long itemId = 1L;
		Item mockItem = new Item();
		mockItem.setId(itemId);

		when(itemService.getItemValid(itemId)).thenReturn(mockItem);

		pictureService.deletePicture(itemId);

		// verify: awsS3Service.deleteFilesByItemId 메소드가 호출되었는지 확인
		verify(awsS3Service, times(1)).deleteFilesByItemId(any(Item.class));
		verify(elasticSearchConfig, times(1)).deleteItemForElasticSearch(mockItem);
	}

	@Test
	@DisplayName("사진 삭제 실패")
	void deletePictureFail() throws JsonProcessingException {
		Long itemId = 1L;
		Item mockItem = new Item();
		mockItem.setId(itemId);
		when(itemService.getItemValid(itemId)).thenReturn(null);

		pictureService.deletePicture(itemId);
		// null 값으로 호출되었을 때, awsS3Service.deleteFilesByItemId 메서드가 호출되지 않았는지 확인합니다.
		verify(awsS3Service, never()).deleteFilesByItemId(any(Item.class));
		verify(elasticSearchConfig, never()).deleteItemForElasticSearch(mockItem);
	}
}