package org.hanghae.markethub.domain.store.controller;

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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
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
	@DisplayName("스토어 생성")
	@WithMockUser
	void createStore() throws Exception {
		//  createStore 메소드가 동작하고 void 리턴이라 doNothing
		doNothing().when(storeService).createStore(any());
		mockMvc.perform(
						MockMvcRequestBuilders.post("/api/stores/")
								.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	@DisplayName("스토어 삭제")
	void deleteStore() throws Exception {
		// deleteStore 메소드가 동작하고 void 리턴이라 doNothing
		doNothing().when(storeService).deleteStore(any());

		mockMvc.perform(
						delete("/api/stores/")
								.contentType(MediaType.APPLICATION_JSON)
				)
				.andDo(print())
				.andExpect(status().isOk());
	}

}
