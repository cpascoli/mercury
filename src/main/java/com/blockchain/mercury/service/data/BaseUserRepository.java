package com.blockchain.mercury.service.data;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blockchain.mercury.model.User;
import com.blockchain.mercury.model.Token;
import com.blockchain.mercury.model.TradeSettlementMessage;
import com.blockchain.mercury.model.WithdrawBalanceRequest;
import com.blockchain.mercury.service.data.datasource.DataSource;
import com.blockchain.mercury.service.data.exception.InsufficientBalanceException;



@Component
public class BaseUserRepository implements UserRepository {


	
	@Autowired
	private DataSource userDataSource; //TODO rename AccountDataSource
	

	@Override
	public void handleWithdrawBalance(WithdrawBalanceRequest request) throws InsufficientBalanceException {
		
		Long orderId = request.getOrderId();
		Long userId = request.getUserId();
		Token token = request.getToken();
		BigDecimal requestedQuantity = request.getRequestedQuantity();
		
		User user = this.findById(userId);
		
		synchronized (user) {
			BigDecimal balance = user.getBalances().get(token);
			BigDecimal updatedBalance = balance.subtract(requestedQuantity);
			if (updatedBalance.compareTo(BigDecimal.ZERO) == -1) {
				throw new InsufficientBalanceException();
			}
			
			user.getBalances().put(token, updatedBalance);
			
			// remember this WithdrawBalanceRequest to reconcile amounts when the trade settles
			user.getRequestedAmounts().put(orderId, request);
			
			// persist updated account
			this.save(user);
			
		}
	}


	@Override
	public void handleTradeSettlement(TradeSettlementMessage message) {
		
		User user = this.findById(message.getUserId());
		
		synchronized (user) {
			
			Token soldToken = message.getSoldToken();
			BigDecimal soldTokenQuantity = message.getSoldQuantity(); 
			
			Token boughtToken = message.getBoughtToken();
			BigDecimal boughtTokenQuantity = message.getBoughtQuantity();
			
			Map<Token, BigDecimal> tokenBalances = user.getBalances();
			
			// Modify the available balance of the sold_token to account for the difference between sold_quantity and the requested_quantity
			
			WithdrawBalanceRequest withdrawRequest = user.getRequestedAmounts().get(message.getOrderId());
			if (withdrawRequest == null) throw new Error("Missing withdraw request data for orderId: "+message.getOrderId());
			BigDecimal toDebitQuantity = soldTokenQuantity.subtract(withdrawRequest.getRequestedQuantity());
			
			BigDecimal updatedSoldTokenBalance = tokenBalances.get(soldToken).subtract(toDebitQuantity);			
			tokenBalances.put(soldToken, updatedSoldTokenBalance);
			
			// Increment the balance of the bought_token by the bought_quantity
			BigDecimal updatedBoughtTokenBalance = tokenBalances.get(boughtToken).add(boughtTokenQuantity);
			tokenBalances.put(boughtToken, updatedBoughtTokenBalance);
			
			// remove withdrawRequest for this orderId 
			user.getRequestedAmounts().remove(message.getOrderId());
			
			// persist updated account
			this.save(user);
			
		}
		
	}


	
	/*
	 * Returns user data from datasource. Override in subclasses to cache user data
	 */
	public User findById(Long userId) {
		
		return userDataSource.findUserById(userId);
	}
	
	
	/*
	 * Persist the user to the datasource. Override in subclasses to cache user data.
	 */
	public void save(User user) {
		userDataSource.save(user);
		
	}

}
