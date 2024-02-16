package org.hanghae.markethub.domain.store.controller;

import org.hanghae.markethub.domain.item.dto.ItemsResponseDto;
import org.hanghae.markethub.domain.store.service.StoreService;
import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.domain.user.security.UserDetailsImpl;
import org.hanghae.markethub.global.constant.Role;
import org.hanghae.markethub.global.constant.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class StoreControllerTest {

	@InjectMocks
	private StoreController storeController;

	@Mock
	private StoreService storeService;

	@Mock
	private MockMvc mockMvc;

	@BeforeEach
	public void init() {

		mockMvc = MockMvcBuilders.standaloneSetup(storeController).build();

	}


	@Test
	@DisplayName("스토어 생성 컨트롤러 호출")
	@WithMockUser
	void createStore() throws Exception {
		//  createStore 메소드가 동작하고 void 리턴이라 doNothing
		doNothing().when(storeService).createStore(any());
		mockMvc.perform(
						MockMvcRequestBuilders.post("/api/stores")
								.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	@DisplayName("스토어 삭제 컨트롤러 호출")
	void deleteStore() throws Exception {
		// deleteStore 메소드가 동작하고 void 리턴이라 doNothing
		doNothing().when(storeService).deleteStore(any());

		mockMvc.perform(
						MockMvcRequestBuilders.delete("/api/stores")
								.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	@DisplayName("스토어 아이템 전체 조회 호출")
	void getStoreItems() throws Exception {

		List<ItemsResponseDto> responseDtos = new ArrayList<>();
		ItemsResponseDto itemsResponseDto = ItemsResponseDto.builder().build();
		responseDtos.add(itemsResponseDto);

		lenient().doReturn(responseDtos)
				.when(storeService)
				.getStoreItems(any(User.class));

		mockMvc.perform(
						MockMvcRequestBuilders.get("/api/stores")
								.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("storeItems"));
	}

	@Test
	@DisplayName("스토어 아이템 단건 조회 호출")
	void getStoreItem() throws Exception {
		Long itemId= 1L;
		ItemsResponseDto itemsResponseDto = ItemsResponseDto.builder().build();

		lenient().doReturn(itemsResponseDto)
				.when(storeService)
				.getStoreItem(anyLong(), any(User.class));

		mockMvc.perform(
						MockMvcRequestBuilders.get("/api/stores/"+ itemId)
								.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("storeItem"));
	}

	@Test
	@DisplayName("스토어 카테고리별 아이템 조회")
	void getStoreItemsByCategory() throws Exception {
		String param = "param";
		List<ItemsResponseDto> responseDtos = new ArrayList<>();
		ItemsResponseDto itemsResponseDto = ItemsResponseDto.builder().build();
		responseDtos.add(itemsResponseDto);

		lenient().doReturn(responseDtos)
				.when(storeService)
				.findByCategory(anyString(), any(User.class));

		mockMvc.perform(
						MockMvcRequestBuilders.get("/api/stores?category="+ param)
								.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("storeItems"));
	}


}
