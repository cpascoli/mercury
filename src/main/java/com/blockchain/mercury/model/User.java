package com.blockchain.mercury.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class User {
	

	private Long userId;
	private Map<Token, BigDecimal> balances;
	
	/*
	 * A map of WithdrawBalanceRequest for in-flight transactions keyed on WithdrawBalanceRequest's orderId
	 */
	private Map<Long, WithdrawBalanceRequest> requestedAmounts = new HashMap<Long, WithdrawBalanceRequest> ();
	
	public User() {}
	
	public User(Long userId, Map<Token, BigDecimal> balances) {
		super();
		this.userId = userId;
		this.balances = balances;
	}
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Map<Token, BigDecimal> getBalances() {
		return balances;
	}
	public void setBalances(Map<Token, BigDecimal> balances) {
		this.balances = balances;
	}
	public Map<Long, WithdrawBalanceRequest> getRequestedAmounts() {
		return requestedAmounts;
	}
	public void setRequestedAmounts(Map<Long, WithdrawBalanceRequest> requestedAmounts) {
		this.requestedAmounts = requestedAmounts;
	}

	
}
