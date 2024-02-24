package org.hanghae.markethub.domain.picture.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
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

	@Transactional
	public void createPicture(Long itemId, List<MultipartFile> files) throws IOException {
		awsS3Service.uploadFiles(files, itemId);
	}
	public void deletePicture(Long itemId) throws JsonProcessingException {
		awsS3Service.deleteFilesByItemId(itemId);
	}
}
