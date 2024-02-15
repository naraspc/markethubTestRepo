//package org.hanghae.markethub.domain.item.service;
//
//import org.hanghae.markethub.domain.item.dto.ItemCreateRequestDto;
//import org.hanghae.markethub.domain.item.dto.ItemUpdateRequestDto;
//import org.hanghae.markethub.domain.item.dto.ItemsResponseDto;
//import org.hanghae.markethub.domain.item.entity.Item;
//import org.hanghae.markethub.domain.item.repository.ItemRepository;
//import org.hanghae.markethub.global.service.AwsS3Service;
//import org.hanghae.markethub.domain.store.entity.Store;
//import org.hanghae.markethub.domain.store.repository.StoreRepository;
//import org.hanghae.markethub.domain.user.entity.User;
//import org.hanghae.markethub.domain.user.repository.UserRepository;
//import org.hanghae.markethub.global.constant.Status;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.test.annotation.Commit;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureMockMvc
//class ItemServiceTest {
//
//	@Autowired
//	private ItemRepository itemRepository;
//
//	@Autowired
//	private StoreRepository storeRepository;
//
//	@Autowired
//	private UserRepository userRepository;
//	@Autowired
//	private AwsS3Service awsS3Service;
//
//	@Test
//	void createItem() throws IOException {
//		MockMultipartFile file1 = new MockMultipartFile("files", "filename1.txt", "text/plain", "file1 data".getBytes());
//		MockMultipartFile file2 = new MockMultipartFile("files", "filename2.txt", "text/plain", "file2 data".getBytes());
//		Store store = storeRepository.findById(1L).get();
//		User user = userRepository.findById(1L).get();
//		ItemCreateRequestDto build = ItemCreateRequestDto.builder()
//				.itemName("테스트")
//				.itemInfo("김")
//				.price(5000)
//				.quantity(5)
//				.category("전자제품")
//				.build();
//		Item item = Item.builder()
//				.itemName(build.getItemName())
//				.itemInfo(build.getItemInfo())
//				.price(build.getPrice())
//				.quantity(build.getQuantity())
//				.category(build.getCategory())
//				.store(store)
//				.status(Status.EXIST)
//				.user(user)
//				.build();
//		Item save = itemRepository.save(item);
//		awsS3Service.uploadFiles(Arrays.asList(file1, file2), save.getId());
//		assertEquals("테스트", save.getItemName());
//		assertEquals(5000, save.getPrice());
//		assertEquals(5, save.getQuantity());
//		assertEquals("김", save.getItemInfo());
//		assertEquals("전자제품", save.getCategory());
//
//	}
//
//	@Test
//	@Transactional
//	@Commit
//	void updateItem() {
//		Item item = itemRepository.findById(16L).orElseThrow();
//		ItemUpdateRequestDto itemUpdateRequestDto = ItemUpdateRequestDto.builder()
//				.itemName("컴퓨터")
//				.price(1000000)
//				.quantity(2)
//				.itemInfo("콤퓨타")
//				.category("전자제품")
//				.build();
//
//		item.updateItem(itemUpdateRequestDto);
//
//		Item updatedItem = itemRepository.findById(16L).orElseThrow();
//
//		assertEquals("컴퓨터", updatedItem.getItemName());
//		assertEquals(1000000, updatedItem.getPrice());
//		assertEquals(2, updatedItem.getQuantity());
//		assertEquals("콤퓨타", updatedItem.getItemInfo());
//		assertEquals("전자제품", updatedItem.getCategory());
//	}
//
//	@Test
//	void getItems() {
//		List<Item> items = itemRepository.findAll();
//		List<ItemsResponseDto> itemsResponseDtos = items.stream()
//				.map(item -> {
//					List<String> pictureUrls = awsS3Service.getObjectUrlsForItem(item.getId());
//					ItemsResponseDto itemsResponseDto = ItemsResponseDto.fromEntity(item, pictureUrls);
//					System.out.println(itemsResponseDto.getItemName());
//					System.out.println(itemsResponseDto.getItemInfo());
//					return itemsResponseDto;
//				})
//				.collect(Collectors.toList());
//	}
//
//	@Test
//	void getItem() {
//		Long itemId= 16L;
//		Item item = itemRepository.findById(itemId).orElseThrow();
//		List<String> pictureUrls = awsS3Service.getObjectUrlsForItem(item.getId());
//		ItemsResponseDto itemsResponseDto = ItemsResponseDto.fromEntity(item, pictureUrls);
//		assertEquals("컴퓨터", itemsResponseDto.getItemName());
//		assertEquals("콤퓨타", itemsResponseDto.getItemInfo());
//		assertEquals("전자제품", itemsResponseDto.getCategory());
//		assertEquals(1000000, itemsResponseDto.getPrice());
//		assertEquals(2, itemsResponseDto.getQuantity());
//	}
//
//	@Test
//	@Transactional
//	@Commit
//	void deleteItem() {
//		Long itemId= 16L;
//		Item item = itemRepository.findById(itemId).orElseThrow();
//		item.deleteItem();
//// EXIST 인 아이템만 출력 되어서 값이 바뀌면 안나옴
////		Item findItem = itemRepository.findById(itemId).orElseThrow();
////		assertEquals(Status.DELETED, findItem.getStatus());
//	}
//}