package org.hanghae.markethub.domain.picture;

import com.amazonaws.services.s3.AmazonS3;
import org.hanghae.markethub.domain.picture.entity.Picture;
import org.hanghae.markethub.domain.picture.repository.PictureRepository;
import org.hanghae.markethub.global.service.AwsS3Service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class PictureTest {
	@Autowired
	private PictureRepository pictureRepository;
	@Autowired
	private AwsS3Service awsS3Service;
	@Autowired
	private AmazonS3 s3Client;

	@Test
	@Transactional
	@Commit
	void createPicture() throws IOException {
		MockMultipartFile file1 = new MockMultipartFile("files", "filename1.txt", "text/plain", "file1 data".getBytes());
		MockMultipartFile file2 = new MockMultipartFile("files", "filename2.txt", "text/plain", "file2 data".getBytes());
		Long itemId= 4L;

		awsS3Service.uploadFiles(Arrays.asList(file1,file2), itemId);
	}

	@Test
	@Transactional
	@Commit
	void deletePicture() {
		Long itemId= 4L;
		String bucketName = "demo-ec2-lv5-item";
		List<Picture> pictures = pictureRepository.findByItemId(itemId);
		for (Picture picture : pictures) {
			String uuid = picture.getUuid();
			s3Client.deleteObject(bucketName, uuid);
			pictureRepository.delete(picture);
		}
	}
}