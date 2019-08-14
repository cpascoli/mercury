package com.blockchain.mercury.service.response;

public final class WithdrawBalanceResponse {

	public static final WithdrawBalanceResponse INSUFFICIENT = new WithdrawBalanceResponse(ResponseCode.INSUFFICIENT_BALANCE);
	public static final WithdrawBalanceResponse SUFFICIENT = new WithdrawBalanceResponse(ResponseCode.SUFFICIENT_BALANCE);

	public enum ResponseCode {INSUFFICIENT_BALANCE, SUFFICIENT_BALANCE};
	
	private final ResponseCode code;
	
	public WithdrawBalanceResponse() {
		this.code = ResponseCode.INSUFFICIENT_BALANCE;
	}
	
	public WithdrawBalanceResponse(ResponseCode code) {
		this.code = code;
	}
	
	public ResponseCode getCode() {
		return this.code;
	}


}
