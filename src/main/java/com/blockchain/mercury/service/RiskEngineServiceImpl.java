package com.blockchain.mercury.service;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.blockchain.mercury.model.TradeSettlementMessage;
import com.blockchain.mercury.model.User;
import com.blockchain.mercury.model.WithdrawBalanceRequest;
import com.blockchain.mercury.service.data.CachedUserRepository;
import com.blockchain.mercury.service.data.exception.InsufficientBalanceException;
import com.blockchain.mercury.service.response.WithdrawBalanceResponse;

@Component
public class RiskEngineServiceImpl implements RiskEngineService {

	
	private static final Logger logger = Logger.getLogger(RiskEngineServiceImpl.class.getName());

	@Autowired
	private CachedUserRepository userRepository;
	

	public RiskEngineServiceImpl() {
		super();
		logger.info("RiskEngineServiceImpl inited");
	}
	
	@Override
	public WithdrawBalanceResponse withdrawBalance(WithdrawBalanceRequest request) {
	
		WithdrawBalanceResponse response = null;
		try {
			userRepository.handleWithdrawBalance(request); 
			response = WithdrawBalanceResponse.SUFFICIENT;
		} catch (InsufficientBalanceException ex) {
			response = WithdrawBalanceResponse.INSUFFICIENT;			
		}

		return response; 
	}
	

	@Override
	public User getUser(Long userId) {
		User user = userRepository.findById(userId);
		return user;
	}
	
	
	@KafkaListener(topics = "${kafka.topic.mercury}")
	public void receive(TradeSettlementMessage message) {
		
		logger.info("received TradeSettlement message="+ message.toString());		
		userRepository.handleTradeSettlement(message);
	}
	

}
