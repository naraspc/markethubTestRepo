package org.hanghae.markethub.domain.picture.service;

import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.domain.item.repository.ItemRepository;
import org.hanghae.markethub.domain.picture.repository.PictureRepository;
import org.hanghae.markethub.global.constant.Status;
import org.hanghae.markethub.global.service.AwsS3Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Commit;
import org.springframework.web.multipart.MultipartFile;
import org.junit.jupiter.api.Assertions;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PictureServiceTest {
	@InjectMocks
	private PictureService pictureService;

	@Mock
	private AwsS3Service awsS3Service;

	@Test
	@DisplayName("사진 등록 성공")
	void createPicture() throws IOException {
		MockMultipartFile file1 = new MockMultipartFile("files", "filename1.txt", "text/plain", "file1 data".getBytes());
		List<MultipartFile> files = new ArrayList<>();
		files.add(file1);

		Long itemId = 1L;
		pictureService.createPicture(itemId, files);

		// verify: awsS3Service.uploadFiles 메소드가 호출되었는지 확인
		verify(awsS3Service, times(1)).uploadFiles(eq(files), eq(itemId));
	}

	@Test
	@DisplayName("사진 등록 실패")
	void createPictureFail() throws IOException {
		// 메서드 호출 여부를 확인합니다.
		pictureService.createPicture(null, null);
		// null 값으로 호출되었을 때, awsS3Service.uploadFiles 메서드가 호출되지 않았는지 확인합니다.
		verify(awsS3Service, never()).uploadFiles(anyList(), anyLong());
	}


	@Test
	@DisplayName("사진 삭제 성공")
	void deletePicture() {
		Long itemId = 1L;

		pictureService.deletePicture(itemId);
		// verify: awsS3Service.deleteFilesByItemId 메소드가 호출되었는지 확인
		verify(awsS3Service, times(1)).deleteFilesByItemId(eq(itemId));
	}

	@Test
	@DisplayName("사진 삭제 실패")
	void deletePictureFail() {
		pictureService.deletePicture(null);
		// null 값으로 호출되었을 때, awsS3Service.deleteFilesByItemId 메서드가 호출되지 않았는지 확인합니다.
		verify(awsS3Service, never()).deleteFilesByItemId(anyLong());
	}
}