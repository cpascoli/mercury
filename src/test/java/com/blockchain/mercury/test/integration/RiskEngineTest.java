package com.blockchain.mercury.test.integration;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;

import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.junit4.SpringRunner;

import com.blockchain.mercury.model.Token;
import com.blockchain.mercury.model.TradeSettlementMessage;
import com.blockchain.mercury.model.User;
import com.blockchain.mercury.model.WithdrawBalanceRequest;
import com.blockchain.mercury.service.response.WithdrawBalanceResponse;


@EnableKafka
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RiskEngineTest {

	
	private static final Logger logger = Logger.getLogger(RiskEngineTest.class.getName());

	
	@Value("${kafka.topic.mercury}")
	private String mercuryTopic;

	@ClassRule
	public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(1, true, 1, "mercury.t");


	@Autowired 
	private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
	
    @Autowired
    private KafkaTemplate<String, TradeSettlementMessage> kafkaTemplate;
	

	private TestRestTemplate restTemplate = new TestRestTemplate();
    
    @LocalServerPort
    private int randomServerPort;
 
    public RiskEngineTest() {}
    

    @Before
    public void setUp() throws Exception {
       for (MessageListenerContainer messageListenerContainer : kafkaListenerEndpointRegistry.getListenerContainers()) {
          ContainerTestUtils.waitForAssignment(messageListenerContainer, embeddedKafka.getPartitionsPerTopic());
       }
       logger.info("started embedded kafka");	
    }
    
    
	@Test
	public void testTradeWorflow_InsufficientBalance() throws Exception {

		Long userId = 100l;
		Long orderId = 1l;
		
		Token sellToken = Token.USD;
		BigDecimal initialSellTokenBalance = new BigDecimal("0");
		BigDecimal requestQuantity = new BigDecimal("100.00");

		// check initial sell token balance		
		User user = getUser(userId);
		assertThat( user.getBalances().get(sellToken), is(initialSellTokenBalance));
	     
		// Send withdraw request
		WithdrawBalanceRequest request = new WithdrawBalanceRequest(orderId, userId, sellToken, requestQuantity);
		WithdrawBalanceResponse response = this.postWithdrawBalance(request);	

		// Verify the token balance was insufficent
		assertThat(response.getCode(), is(WithdrawBalanceResponse.INSUFFICIENT.getCode()) );
		
	}

	
	@Test
	public void testTradeWorflow_SufficientBalance_SameRequestedAndSoldAmounts() throws Exception {

		Long userId = 110l;
		Long orderId = 2l;
		
		Token sellToken = Token.USD;
		Token buyToken = Token.BTC;
		BigDecimal initialSellTokenBalance = new BigDecimal("26917.44");
		BigDecimal initialBuyTokenBalance = new BigDecimal("71.659122");
		
		BigDecimal requestQuantity = new BigDecimal("100.00");
		BigDecimal soldQuantity = new BigDecimal("100.00");
		BigDecimal boughtQuantity = new BigDecimal("0.01");
		
		BigDecimal expectedWithdrawTokenBalance = initialSellTokenBalance.subtract(requestQuantity);
		BigDecimal expectedSellTokenBalance = initialSellTokenBalance.subtract(soldQuantity);
		BigDecimal expectedBuyTokenBalance = initialBuyTokenBalance.add(boughtQuantity);
		
		runIntegrationTest(userId, orderId, sellToken, buyToken, initialSellTokenBalance, requestQuantity, soldQuantity,
				boughtQuantity, expectedWithdrawTokenBalance, expectedSellTokenBalance, expectedBuyTokenBalance);
 
	}
	
	
	
	@Test
	public void testTradeWorflow_SufficientBalance_DifferentRequestedAndSoldAmounts() throws Exception {

		Long userId = 111l;
		Long orderId = 1l;
		
		Token sellToken = Token.USD;
		Token buyToken = Token.BTC;
		BigDecimal initialSellTokenBalance = new BigDecimal("34277.24");
		BigDecimal initialBuyTokenBalance = new BigDecimal("60.345651");
		
		BigDecimal requestQuantity = new BigDecimal("100.00");
		BigDecimal soldQuantity = new BigDecimal("105.00");
		BigDecimal boughtQuantity = new BigDecimal("0.01");
		
		BigDecimal expectedWithdrawTokenBalance = initialSellTokenBalance.subtract(requestQuantity);
		BigDecimal expectedSellTokenBalance = initialSellTokenBalance.subtract(soldQuantity);
		BigDecimal expectedBuyTokenBalance = initialBuyTokenBalance.add(boughtQuantity);
		
		runIntegrationTest(userId, orderId, sellToken, buyToken, initialSellTokenBalance, requestQuantity, soldQuantity,
				boughtQuantity, expectedWithdrawTokenBalance, expectedSellTokenBalance, expectedBuyTokenBalance);
 
	}
	
	


	private void runIntegrationTest(Long userId, Long orderId, Token sellToken, Token buyToken,
			BigDecimal initialSellTokenBalance, BigDecimal requestQuantity, BigDecimal soldQuantity,
			BigDecimal boughtQuantity, BigDecimal expectedWithdrawTokenBalance, BigDecimal expectedSellTokenBalance,
			BigDecimal expectedBuyTokenBalance) throws Exception, InterruptedException {
		
		// check initial sell token balance		
		User user = getUser(userId);
		assertThat( user.getBalances().get(sellToken), is(initialSellTokenBalance));
	     
		// Send withdraw request
		WithdrawBalanceRequest request = new WithdrawBalanceRequest(orderId, userId, sellToken, requestQuantity);
		WithdrawBalanceResponse response = this.postWithdrawBalance(request);	

		assertThat(response.getCode(), is(WithdrawBalanceResponse.SUFFICIENT.getCode()) );

		// check withdraw amount has been taken
		user = getUser(userId);
		assertThat( user.getBalances().get(sellToken), is(expectedWithdrawTokenBalance));

		
		// Send trade settlement message
		TradeSettlementMessage message = new TradeSettlementMessage(orderId, userId, 
				buyToken, boughtQuantity, 
				sellToken, soldQuantity);
		
		logger.info("sending message=" + message.toString() + " to topic '" + mercuryTopic + "'");
		kafkaTemplate.send(mercuryTopic, message);
	

		// wait for trade settlement message to be handled
		//FIXME we should use a callback rather than sleep here to know when the message has been handled by the RiskEngine
		Thread.sleep(2_000);
		
		// check buy and sell token balances after trade settled
		user = getUser(userId);
		assertThat(user.getBalances().get(sellToken), is(expectedSellTokenBalance));
		assertThat(user.getBalances().get(buyToken), is(expectedBuyTokenBalance));
	}

	
	
	private User getUser(final Long userId) throws Exception {

		final String url = "http://localhost:"+randomServerPort+"/api/user/"+userId;
		
		ResponseEntity<User> response  = restTemplate.getForEntity(url, User.class);
		User user = response.getBody();
        return user;
   
	}
	
	
	private WithdrawBalanceResponse postWithdrawBalance(final WithdrawBalanceRequest request) throws Exception {

		final String url = "http://localhost:"+randomServerPort+"/api/withdraw";
		
		ResponseEntity<WithdrawBalanceResponse> result = this.restTemplate.postForEntity(url, request, WithdrawBalanceResponse.class);
		WithdrawBalanceResponse response = result.getBody();
		return response;

	}

}
