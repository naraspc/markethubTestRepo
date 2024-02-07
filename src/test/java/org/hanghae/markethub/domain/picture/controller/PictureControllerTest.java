package org.hanghae.markethub.domain.picture.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class PictureControllerTest {
	@Autowired
	private MockMvc api;

	@Test
	@DisplayName("사진 등록")
	void createPicture() throws Exception {
		Long itemId= 22L;
		MockMultipartFile file1 = new MockMultipartFile("files", "filename1.txt", "text/plain", "file1 data".getBytes());

		api.perform(multipart(HttpMethod.POST, "/api/pictures/"+ itemId)
						.file(file1)
						.contentType(MediaType.MULTIPART_FORM_DATA)  // Set content type to multipart form data
				)
				.andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	@DisplayName("사진 삭제")
	void deletePicture() throws Exception {
		Long itemId= 22L;
		api.perform(
						delete("/api/pictures/"+ itemId)
								.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isOk());
	}
}
