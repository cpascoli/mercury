package com.blockchain.mercury.model;

import java.math.BigDecimal;

public class TradeSettlementMessage {

	private Long orderId;
	private Long userId;	
	private Token boughtToken;
	private BigDecimal boughtQuantity;
	private Token soldToken;
	private BigDecimal soldQuantity;

	public TradeSettlementMessage() {
		super();
	}

	public TradeSettlementMessage(Long orderId, Long userId, Token boughtToken, BigDecimal boughtQuantity, Token soldToken, BigDecimal soldQuantity) {
		super();
		this.orderId = orderId;
		this.userId = userId;
		this.boughtToken = boughtToken;
		this.boughtQuantity = boughtQuantity;
		this.soldToken = soldToken;
		this.soldQuantity = soldQuantity;
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

	public Token getBoughtToken() {
		return boughtToken;
	}

	public void setBoughtToken(Token boughtToken) {
		this.boughtToken = boughtToken;
	}

	public BigDecimal getBoughtQuantity() {
		return boughtQuantity;
	}

	public void setBoughtQuantity(BigDecimal boughtQuantity) {
		this.boughtQuantity = boughtQuantity;
	}

	public Token getSoldToken() {
		return soldToken;
	}

	public void setSoldToken(Token soldToken) {
		this.soldToken = soldToken;
	}

	public BigDecimal getSoldQuantity() {
		return soldQuantity;
	}

	public void setSoldQuantity(BigDecimal soldQuantity) {
		this.soldQuantity = soldQuantity;
	}

	@Override
	public String toString() {
		return "TradeSettlement [userId=" + userId + ", boughtToken=" + boughtToken + ", boughtQuantity="
				+ boughtQuantity + ", soldToken=" + soldToken + ", soldQuantity=" + soldQuantity + "]";
	}

}
