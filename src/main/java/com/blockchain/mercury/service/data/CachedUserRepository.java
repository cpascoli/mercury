package com.blockchain.mercury.service.data;


import java.util.Collections;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.stereotype.Component;
import com.blockchain.mercury.model.User;
import com.blockchain.mercury.util.LRUCache;


@Component
public class CachedUserRepository extends BaseUserRepository {

	private static final int CACHE_SIZE = 300;	
	
	private static final Logger logger = Logger.getLogger(CachedUserRepository.class.getName());

	/*
	 * A LRU cache of userId vs user data
	 */
	private final Map<Long, User> userCache;
	
	public CachedUserRepository() {
		super();
		this.userCache = Collections.synchronizedMap(new LRUCache<Long, User>(CACHE_SIZE));
	}
	

	@Override
	public User findById(Long userId) {
		User user = userCache.get(userId);

		if (user == null) {
			logger.info("cache miss - userId: "+userId);
			// if user data in not in cache fetch it from the datasource and add to the cache
			user = super.findById(userId);
			userCache.put(userId, user);
		} else {
			logger.info("cache hit - userId: "+userId);
		}
		return user;
	}
	 
	@Override
	public void save(User user) {
		synchronized (user) {
			// update local cache and persist user
			userCache.put(user.getUserId(), user);
			super.save(user);
		}
	}

	
}
