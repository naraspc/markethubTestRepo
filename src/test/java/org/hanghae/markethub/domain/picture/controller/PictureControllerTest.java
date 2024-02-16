package org.hanghae.markethub.domain.picture.controller;

import org.hanghae.markethub.domain.picture.service.PictureService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class PictureControllerTest {

	@InjectMocks
	private PictureController pictureController;

	@Mock
	private PictureService pictureService;

	@Mock
	private MockMvc mockMvc;

	@BeforeEach
	public void init() {
		mockMvc = MockMvcBuilders.standaloneSetup(pictureController).build();
	}

	@Test
	@DisplayName("사진 등록")
	void createPicture() throws Exception {
		Long itemId = 1L;
		MockMultipartFile file1 = new MockMultipartFile("files", "filename1.txt", "text/plain", "file1 data" .getBytes());


		//  createPicture 메소드가 동작하고 void 리턴이라 doNothing
		doNothing().when(pictureService).createPicture(anyLong(), anyList());


		mockMvc.perform(
						MockMvcRequestBuilders.multipart("/api/pictures/" + itemId)
								.file(file1)
								.contentType(MediaType.MULTIPART_FORM_DATA)
				)
				.andDo(print())
				.andExpect(status().isOk());
	}
	@Test
	@DisplayName("사진 삭제")
	void deletePicture() throws Exception {
		Long itemId= 1L;

		//  createPicture 메소드가 동작하고 void 리턴이라 doNothing
		doNothing().when(pictureService).deletePicture(anyLong());

		mockMvc.perform(
						MockMvcRequestBuilders
								.delete("/api/pictures/" + itemId)
								.contentType(MediaType.MULTIPART_FORM_DATA)
				)
				.andDo(print())
				.andExpect(status().isOk());
	}
}
