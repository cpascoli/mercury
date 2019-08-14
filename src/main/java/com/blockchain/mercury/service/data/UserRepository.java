package com.blockchain.mercury.service.data;

import com.blockchain.mercury.service.data.exception.InsufficientBalanceException;
import com.blockchain.mercury.model.TradeSettlementMessage;
import com.blockchain.mercury.model.WithdrawBalanceRequest;


public interface UserRepository {

	 public void handleWithdrawBalance(WithdrawBalanceRequest request) throws InsufficientBalanceException;
	 public void handleTradeSettlement(TradeSettlementMessage message);
	
}
