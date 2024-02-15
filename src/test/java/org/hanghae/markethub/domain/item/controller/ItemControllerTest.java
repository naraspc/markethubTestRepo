package org.hanghae.markethub.domain.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.hanghae.markethub.domain.item.dto.ItemCreateRequestDto;
import org.hanghae.markethub.domain.item.dto.ItemUpdateRequestDto;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.domain.item.repository.ItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ItemControllerTest {
	@Autowired
	private MockMvc api;

	@Autowired
	private ItemRepository itemRepository;

	private final ObjectMapper mapper = new ObjectMapper();

	@Test
	@DisplayName("아이템 전체 조회")
	void getAllItems() throws Exception {
		api.perform(
						get("/api/items")
								.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	@DisplayName("아이템 단건 조회")
	void getAllItem() throws Exception {
		Long itemId= 5L;
		api.perform(
						get("/api/items/"+ itemId)
								.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	@DisplayName("아이템 생성")
	void createItem() throws Exception {
		MockMultipartFile file1 = new MockMultipartFile("files", "filename1.txt", "text/plain", "file1 data".getBytes());

		ItemCreateRequestDto build = ItemCreateRequestDto.builder()
				.itemName("멀티파일")
				.itemInfo("김")
				.price(5000)
				.quantity(5)
				.category("전자제품")
				.build();
		String value = mapper.writeValueAsString(build);
		api.perform(multipart(HttpMethod.POST, "/api/items")
						.file(file1)
						.file(new MockMultipartFile("itemData","","application/json",
								value.getBytes(StandardCharsets.UTF_8)))
						.contentType(MediaType.MULTIPART_FORM_DATA)  // Set content type to multipart form data
				)
				.andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	@DisplayName("아이템 정보 수정")
	void updateItem() throws Exception {
		Long id = 17L;
		ItemUpdateRequestDto build = ItemUpdateRequestDto.builder()
				.itemName("17번수정")
				.itemInfo("노트북")
				.price(5000)
				.quantity(5)
				.category("전자제품")
				.build();

		String value = mapper.writeValueAsString(build);
		api.perform(multipart(HttpMethod.PATCH, "/api/items/" + id)
								.file(new MockMultipartFile("itemData","","application/json",
										value.getBytes(StandardCharsets.UTF_8)))
				)
				.andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	@DisplayName("아이템 Status DELETE 변경")
	void deleteItem() throws Exception {
		Long itemId= 16L;
		api.perform(
						delete("/api/items/"+ itemId)
								.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isOk());

//		Item item = itemRepository.findById(itemId).get();
		//Assertions.assertThat(item.getStatus().toString().equals("DELETE")); DELETE 되면 조회 안되게 막아둬서 안나옴
	}
}
