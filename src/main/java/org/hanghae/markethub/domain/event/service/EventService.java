package org.hanghae.markethub.domain.event.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.event.dto.CreateEventDto;
import org.hanghae.markethub.domain.event.dto.EventDto;
import org.hanghae.markethub.domain.event.dto.EventItemResponseDto;
import org.hanghae.markethub.domain.event.entity.Event;
import org.hanghae.markethub.domain.event.repository.EventRepository;
import org.hanghae.markethub.domain.item.config.ElasticSearchConfig;
import org.hanghae.markethub.domain.item.dto.ItemUpdateRequestDto;
import org.hanghae.markethub.domain.item.dto.ItemsResponseDto;
import org.hanghae.markethub.domain.item.dto.RedisItemResponseDto;
import org.hanghae.markethub.domain.item.entity.Item;
import org.hanghae.markethub.domain.item.service.ItemService;
import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.global.jwt.JwtUtil;
import org.hanghae.markethub.global.service.AwsS3Service;
import org.hibernate.Hibernate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Service
@RequiredArgsConstructor
public class EventService {
	private final ItemService itemService;
	private final EventRepository eventRepository;
	private final ElasticSearchConfig elasticSearchConfig;
	private final List<EventItemResponseDto> eventItemResponseDtos = new ArrayList<>();
	private final TaskScheduler taskScheduler;
	private final RedisTemplate redisTemplate;
	private final ObjectMapper objectMapper;
	private ScheduledFuture<?> startEventScheduledFuture;
	private ScheduledFuture<?> endEventScheduledFuture;
	private int time;
	private Map<Long, Integer> oldPrice = new HashMap<>();
	private final JwtUtil jwtUtil;

	public void setEventSchedule(int startTime, int endTime) {
		int startHour = startTime / 100;
		int startMinute = startTime % 100;
		int endHour = endTime / 100;
		int endMinute = endTime % 100;

		String startCronExpression = convertToCronExpression(startHour, startMinute, 0);
		String endCronExpression = convertToCronExpression(endHour, endMinute, 0);

		if (startEventScheduledFuture != null) {
			startEventScheduledFuture.cancel(true);
		}
		if (endEventScheduledFuture != null) {
			endEventScheduledFuture.cancel(true);
		}

		// 시작 시간 스케줄 설정
		startEventScheduledFuture = taskScheduler.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					startEvent();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}, new CronTrigger(startCronExpression));

		// 종료 시간 스케줄 설정
		endEventScheduledFuture = taskScheduler.schedule(new Runnable() {
			@Override
			public void run() {
				endEvent();
			}
		}, new CronTrigger(endCronExpression));
	}


	public void createEvent(User user, CreateEventDto createEventDto) {
		Item item = itemService.getItemValid(createEventDto.getItemId());
		if(item.getUser().getId() != user.getId()) {
			throw new IllegalArgumentException("본인 상품만 등록 가능합니다.");
		}

		Event findEvent = eventRepository.findByItemId(createEventDto.getItemId());
		if (findEvent != null) {
			eventRepository.delete(findEvent);
		}
		Event event = Event.builder()
				.itemId(createEventDto.getItemId())
				.quantity(createEventDto.getQuantity())
				.price(createEventDto.getPrice())
				.build();

		eventRepository.save(event);
	}

	public void deleteEvent(User user, Long itemId) {
		Item item = itemService.getItemValid(itemId);
		if(item.getUser().getId() != user.getId()) {
			throw new IllegalArgumentException("본인 상품만 삭제가 가능합니다.");
		}
		Event event = eventRepository.findByItemId(itemId);
		if(event != null) {
			eventRepository.delete(event);
		}
	}


	@Transactional
	public void startEvent() throws JsonProcessingException {
		List<Event> events = eventRepository.findAll();
		
		for(Event event : events) {
			Item item = itemService.getItemValid(event.getItemId());

			ItemUpdateRequestDto requestDto = ItemUpdateRequestDto.builder()
							.itemName(item.getItemName())
									.quantity(event.getQuantity())
											.price(event.getPrice())
													.itemInfo(item.getItemInfo())
															.category(item.getCategory())
																	.build();
			itemService.updateItem(item.getId(),requestDto,item.getUser());

			oldPrice.put(item.getId(), item.getPrice());

//			Item item1 = item.updateItemForEvent(event.getPrice(), event.getQuantity());
//			itemService.updateItemForRedis(item1);
//			elasticSearchConfig.syncItemToElasticsearch(item1);
		}
	}

	//	@Scheduled(cron = "0 11 21 * * *")
	@Transactional
	public void endEvent() {
		this.eventItemResponseDtos.clear();
	}

	@Transactional
	public List<EventItemResponseDto> getEventItemsResponseDtos() throws JsonProcessingException {

		if(!eventItemResponseDtos.isEmpty()) {
			eventItemResponseDtos.clear();
		}

		List<Event> events = eventRepository.findAll();
		for (Event event : events) {
			Item item = itemService.getItemValid(event.getItemId());
			String key = "item";
			String findKey = key + ":" + event.getItemId();
			String getKey = (String) redisTemplate.opsForValue().get(findKey);

			RedisItemResponseDto redisItemResponseDto = objectMapper.readValue(getKey, RedisItemResponseDto.class);

			ItemsResponseDto itemsResponseDto = ItemsResponseDto.builder()
					.id(redisItemResponseDto.getId())
					.itemName(redisItemResponseDto.getItemName())
					.price(redisItemResponseDto.getPrice())
					.quantity(redisItemResponseDto.getQuantity())
					.itemInfo(redisItemResponseDto.getItemInfo())
					.category(redisItemResponseDto.getCategory())
					.pictureUrls(redisItemResponseDto.getPictureUrls())
					.build();

			EventItemResponseDto eventItemResponseDto = EventItemResponseDto.builder()
					.items(itemsResponseDto)
					.oldPrice(oldPrice.get(item.getId()))
					.build();

			eventItemResponseDtos.add(eventItemResponseDto);


		}
		return this.eventItemResponseDtos;
	}
	public int getEventTime() {
		return this.time;
	}


	private String convertToCronExpression(int hour, int minute, int second) {
		return String.format("0 %d %d * * *", minute, hour);
	}
}