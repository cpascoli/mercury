package com.blockchain.mercury.test.unit;

import static org.assertj.core.api.Assertions.assertThat;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.blockchain.mercury.model.Token;
import com.blockchain.mercury.model.User;
import com.blockchain.mercury.model.WithdrawBalanceRequest;
import com.blockchain.mercury.service.RiskEngineServiceImpl;
import com.blockchain.mercury.service.response.WithdrawBalanceResponse;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestAPITest {

	private static final Logger logger = Logger.getLogger(RestAPITest.class.getName());

	private TestRestTemplate restTemplate = new TestRestTemplate();

	@LocalServerPort
	private int randomServerPort;

	@MockBean
	private RiskEngineServiceImpl riskEngineService;

	@Before
	public void setUp() {

		Long orderId = 1l;
		Long userId = 100l;
		Token token = Token.BTC;
		BigDecimal amount = new BigDecimal("100.00");

		WithdrawBalanceRequest request = new WithdrawBalanceRequest(orderId, userId, token, amount);
		Mockito.when(riskEngineService.withdrawBalance(request)).thenReturn(WithdrawBalanceResponse.SUFFICIENT);

		Map<Token, BigDecimal> balances = new HashMap<Token, BigDecimal>();
		balances.put(Token.EUR, new BigDecimal("1000.00"));
		User user = new User(100l, balances);
		Mockito.when(riskEngineService.getUser(100l)).thenReturn(user);

	}

	@Test
	public void ping() {
		String url = "http://localhost:" + randomServerPort + "/api/ping";
		final ResponseEntity<String> entity = this.restTemplate.getForEntity(url, String.class);
		assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void getUser() {

		Long userId = 100l;
		BigDecimal expectedBalanceEUR = new BigDecimal("1000.00");

		final String url = "http://localhost:" + randomServerPort + "/api/user/" + userId;
		ResponseEntity<User> entity = this.restTemplate.getForEntity(url, User.class);

		assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
		User user = entity.getBody();
		assertThat(user.getBalances().get(Token.EUR)).isEqualTo(expectedBalanceEUR);
	}

	@Test
	public void postWithdrawBalance() throws Exception {

		Long orderId = 1l;
		Long userId = 100l;
		Token token = Token.BTC;
		BigDecimal requestQuantity = new BigDecimal("100.00");

		WithdrawBalanceRequest request = new WithdrawBalanceRequest(orderId, userId, token, requestQuantity);

		final String url = "http://localhost:" + randomServerPort + "/api/withdraw/";
		ResponseEntity<WithdrawBalanceResponse> entity = this.restTemplate.postForEntity(url, request, WithdrawBalanceResponse.class);

		assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
		WithdrawBalanceResponse response = entity.getBody();
		assertThat(response.getCode()).isEqualTo(WithdrawBalanceResponse.SUFFICIENT.getCode());

	}

}