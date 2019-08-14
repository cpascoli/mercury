package com.blockchain.mercury.service.data.datasource;


import com.blockchain.mercury.model.User;

public interface DataSource {

	public User findUserById(Long userId);
	public void save(User user);
	
}
