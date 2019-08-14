package com.blockchain.mercury.test.unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.blockchain.mercury.model.Token;
import com.blockchain.mercury.model.WithdrawBalanceRequest;
import com.blockchain.mercury.service.RiskEngineService;
import com.blockchain.mercury.service.RiskEngineServiceImpl;
import com.blockchain.mercury.service.data.CachedUserRepository;
import com.blockchain.mercury.service.data.exception.InsufficientBalanceException;
import com.blockchain.mercury.service.response.WithdrawBalanceResponse;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { RiskEngineServiceImpl.class, CachedUserRepository.class })
public class WithdrawBalanceAPITest {


	@Autowired
	private RiskEngineService riskEngineService;

	@MockBean
	private CachedUserRepository userRepository;

	@Before
	public void setUp() throws Exception {

		Mockito.doNothing().when(userRepository)
				.handleWithdrawBalance(new WithdrawBalanceRequest(1l, 100l, Token.EUR, new BigDecimal("1000.00")));

		Mockito.doThrow(new InsufficientBalanceException()).when(userRepository)
				.handleWithdrawBalance(new WithdrawBalanceRequest(2l, 101l, Token.USD, new BigDecimal("1000.00")));

	}

	@Test
	public void testWithdrawalBelowBalance() {

		Long orderId = 1l;
		Long userId = 100l;
		Token token = Token.EUR;
		BigDecimal requestedQuantity = new BigDecimal("1000.00");

		WithdrawBalanceRequest request = new WithdrawBalanceRequest(orderId, userId, token, requestedQuantity);
		WithdrawBalanceResponse response = riskEngineService.withdrawBalance(request);

		assertThat(response.getCode()).isEqualTo(WithdrawBalanceResponse.SUFFICIENT.getCode());
	}

	@Test
	public void testWithdrawalAboveBalance() {

		Long orderId = 2l;
		Long userId = 101l;
		Token token = Token.USD;
		BigDecimal requestedQuantity = new BigDecimal("1000.00");

		WithdrawBalanceRequest request = new WithdrawBalanceRequest(orderId, userId, token, requestedQuantity);
		WithdrawBalanceResponse response = riskEngineService.withdrawBalance(request);

		assertThat(response.getCode()).isEqualTo(WithdrawBalanceResponse.INSUFFICIENT.getCode());
	}

}
