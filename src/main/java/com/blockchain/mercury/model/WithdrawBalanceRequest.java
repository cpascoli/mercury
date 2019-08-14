package com.blockchain.mercury.model;

import java.math.BigDecimal;

public class WithdrawBalanceRequest {

	private Long orderId;
	private Long userId;
	private Token token;
	private BigDecimal requestedQuantity;
	
	public WithdrawBalanceRequest() {}
	
	public WithdrawBalanceRequest(Long orderId, Long userId, Token token, BigDecimal requestedQuantity) {
		super();
		this.orderId = orderId;
		this.userId = userId;
		this.token = token;
		this.requestedQuantity = requestedQuantity;
	}
	
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Token getToken() {
		return token;
	}
	public void setToken(Token token) {
		this.token = token;
	}
	public BigDecimal getRequestedQuantity() {
		return requestedQuantity;
	}
	public void setRequestedQuantity(BigDecimal requestedQuantity) {
		this.requestedQuantity = requestedQuantity;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((orderId == null) ? 0 : orderId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WithdrawBalanceRequest other = (WithdrawBalanceRequest) obj;
		if (orderId == null) {
			if (other.orderId != null)
				return false;
		} else if (!orderId.equals(other.orderId))
			return false;
		return true;
	}

}
