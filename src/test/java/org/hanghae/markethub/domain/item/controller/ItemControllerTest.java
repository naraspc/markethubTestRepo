//package org.hanghae.markethub.domain.item.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.hanghae.markethub.domain.item.dto.ItemCreateRequestDto;
//import org.hanghae.markethub.domain.item.dto.ItemUpdateRequestDto;
//import org.hanghae.markethub.domain.item.dto.ItemsResponseDto;
//import org.hanghae.markethub.domain.item.service.ItemService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.lenient;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@ExtendWith(MockitoExtension.class)
//class ItemControllerTest {
//	@InjectMocks
//	private ItemController itemController;
//
//	@Mock
//	private ItemService itemService;
//
//	@Mock
//	private MockMvc mockMvc;
//
//	@BeforeEach
//	public void init() {
//		mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();
//	}
//
//
//	@Test
//	@DisplayName("아이템 전체 조회")
//	void getAllItems() throws Exception {
//
//		List<ItemsResponseDto> responseDtos = new ArrayList<>();
//		ItemsResponseDto itemsResponseDto = ItemsResponseDto.builder().build();
//		responseDtos.add(itemsResponseDto);
//
//		lenient().doReturn(responseDtos)
//				.when(itemService)
//				.getItems();
//
//		mockMvc.perform(
//						MockMvcRequestBuilders.get("/api/items")
//								.contentType(MediaType.APPLICATION_JSON)
//				)
//				.andDo(print())
//				.andExpect(MockMvcResultMatchers.view().name("Allitems"));
//	}
//
//	@Test
//	@DisplayName("아이템 단건 조회")
//	void getItem() throws Exception {
//		Long itemId= 1L;
//		ItemsResponseDto itemsResponseDto = ItemsResponseDto.builder().build();
//
//		lenient().doReturn(itemsResponseDto)
//				.when(itemService)
//				.getItem(itemId);
//
//		mockMvc.perform(
//						MockMvcRequestBuilders.get("/api/items/" + itemId)
//								.contentType(MediaType.APPLICATION_JSON)
//				)
//				.andDo(print())
//				.andExpect(MockMvcResultMatchers.view().name("item"));
//	}
//
////	@Test
////	@DisplayName("아이템 카테고리별 아이템 조회")
////	void getStoreItemsByCategory() throws Exception {
////		String param = "param";
////		List<ItemsResponseDto> responseDtos = new ArrayList<>();
////		ItemsResponseDto itemsResponseDto = ItemsResponseDto.builder().build();
////		responseDtos.add(itemsResponseDto);
////
////		lenient().doReturn(responseDtos)
////				.when(itemService)
////				.findByCategory(anyString());
////
////		mockMvc.perform(
////						MockMvcRequestBuilders.get("/api/items?category="+ param)
////								.contentType(MediaType.APPLICATION_JSON)
////				)
////				.andDo(print())
////				.andExpect(MockMvcResultMatchers.view().name("Allitems"));
////	}
//
//	@Test
//	@DisplayName("아이템 생성")
//	void createItem() throws Exception {
//		ObjectMapper objectMapper = new ObjectMapper();
//		MockMultipartFile file1 = new MockMultipartFile("files", "filename1.txt", "text/plain", "file1 data".getBytes());
//
//		ItemCreateRequestDto build = ItemCreateRequestDto.builder()
//				.itemName("멀티파일")
//				.itemInfo("김")
//				.price(5000)
//				.quantity(5)
//				.category("전자제품")
//				.build();
//
//		String value = objectMapper.writeValueAsString(build);
//
//
//		mockMvc.perform(
//						MockMvcRequestBuilders.multipart("/api/items")
//								.file(file1)
//								.file(new MockMultipartFile("itemData","","application/json", value.getBytes(StandardCharsets.UTF_8)))
//								.contentType(MediaType.MULTIPART_FORM_DATA)
//				)
//				.andDo(print())
//				.andExpect(status().isOk());
//	}
//
//	@Test
//	@DisplayName("아이템 수정")
//	void updateItem() throws Exception {
//		Long itemId= 1L;
//		ObjectMapper objectMapper = new ObjectMapper();
//		MockMultipartFile file1 = new MockMultipartFile("files", "filename1.txt", "text/plain", "file1 data".getBytes());
//
//		ItemUpdateRequestDto build = ItemUpdateRequestDto.builder()
//				.itemName("17번수정")
//				.itemInfo("노트북")
//				.price(5000)
//				.quantity(5)
//				.category("전자제품")
//				.build();
//
//		String value = objectMapper.writeValueAsString(build);
//
//		mockMvc.perform(
//						MockMvcRequestBuilders.multipart(HttpMethod.PATCH,"/api/items/" + itemId)
//								.file(file1)
//								.file(new MockMultipartFile("itemData","","application/json", value.getBytes(StandardCharsets.UTF_8)))
//								.contentType(MediaType.MULTIPART_FORM_DATA)
//				)
//				.andDo(print())
//				.andExpect(status().isOk());
//	}
//
//	@Test
//	@DisplayName("아이템 삭제")
//	void deleteItem() throws Exception {
//		Long itemid =1L;
//
//		mockMvc.perform(
//						MockMvcRequestBuilders.delete("/api/items/"+ itemid)
//								.contentType(MediaType.APPLICATION_JSON)
//				)
//				.andDo(print())
//				.andExpect(status().isOk());
//	}
//}
