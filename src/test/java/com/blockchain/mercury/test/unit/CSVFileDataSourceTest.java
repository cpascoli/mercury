package com.blockchain.mercury.test.unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.blockchain.mercury.model.Token;
import com.blockchain.mercury.model.User;
import com.blockchain.mercury.service.data.datasource.CSVFileDataSource;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { CSVFileDataSource.class })
public class CSVFileDataSourceTest {

	@Autowired
	private CSVFileDataSource csvFileDataSource;

	@Test
	public void testUSDBalance() throws Exception {

		Long userId = 100l;
		BigDecimal expected = new BigDecimal("0");

		User user = csvFileDataSource.findUserById(userId);
		BigDecimal balance = user.getBalances().get(Token.USD);
		assertThat(balance).isEqualTo(expected);

	}

	@Test
	public void testEURBalance() throws Exception {

		Long userId = 100l;
		BigDecimal expected = new BigDecimal("3128.39");

		User user = csvFileDataSource.findUserById(userId);
		BigDecimal balance = user.getBalances().get(Token.EUR);
		assertThat(balance).isEqualTo(expected);

	}

	@Test
	public void testBTCBalance() throws Exception {

		Long userId = 100l;
		BigDecimal expected = new BigDecimal("81.807344");

		User user = csvFileDataSource.findUserById(userId);
		BigDecimal balance = user.getBalances().get(Token.BTC);
		assertThat(balance).isEqualTo(expected);

	}
}
