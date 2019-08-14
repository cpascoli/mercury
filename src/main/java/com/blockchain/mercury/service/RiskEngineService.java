package com.blockchain.mercury.service;

import com.blockchain.mercury.model.User;
import com.blockchain.mercury.model.WithdrawBalanceRequest;
import com.blockchain.mercury.service.response.WithdrawBalanceResponse;

/**
 * The public API exposed by the Risk Engine Service. 
 * @author carlo
 *
 */
public interface RiskEngineService {

	public WithdrawBalanceResponse withdrawBalance(WithdrawBalanceRequest request);

	public User getUser(Long userId);
}
