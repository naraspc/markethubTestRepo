package org.hanghae.markethub.global.config;

import jakarta.annotation.PostConstruct;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.stereotype.Component;

@Component
public class RedissonFairLock {
	private RedissonClient redissonClient;

	@PostConstruct
	public void init() {
		Config config = new Config();
		config.useSingleServer().setAddress("redis://127.0.0.1:6379");
		redissonClient = Redisson.create(config);
	}

	public void performWithFairLock(String lockKey, Runnable action) {
		RLock fairLock = redissonClient.getFairLock(lockKey);
		try {
			fairLock.lock();
			action.run();
		} finally {
			fairLock.unlock();
		}
	}
}
