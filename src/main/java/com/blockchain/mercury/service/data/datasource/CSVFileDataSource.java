package com.blockchain.mercury.service.data.datasource;

import java.io.BufferedReader;
import java.io.File;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.blockchain.mercury.model.User;
import com.blockchain.mercury.model.Token;


@Component
public class CSVFileDataSource implements DataSource {
	
	
	private final static String USER_BALANCES_CSV_FILE = "user-balances.csv";
	
	private final Map<Long, User> users;
	
	public CSVFileDataSource() throws IOException {		
		this.users = loadAccounts(USER_BALANCES_CSV_FILE);
	}
	

	@Override
	public User findUserById(Long userId) {
		return this.users.get(userId);
		
	}

	@Override
	public void save(User user) {
		this.users.put(user.getUserId(), user);
	}
	


	
	private Map<Long, User> loadAccounts(String fileName)  throws IOException {
		
		Map<Long, User> accounts = new LinkedHashMap<Long, User>();
		
		URL url = getClass().getClassLoader().getResource(fileName);
		if (url == null) {
			throw new IOException("csv file "+fileName+" not found");
		}
		
		File file = new File(url.getFile());
		BufferedReader csvReader = new BufferedReader(new FileReader(file));
		
		String row;
		while ((row = csvReader.readLine()) != null) {
		   
			String[] data = row.split(",");
		    Long userId = Long.valueOf(data[0]);
		  
		    Map<Token, BigDecimal> tokenBalances = new LinkedHashMap<Token, BigDecimal>();
		    tokenBalances.put( Token.USD, new BigDecimal(data[1]));
		    tokenBalances.put( Token.EUR, new BigDecimal(data[2]));
		    tokenBalances.put( Token.BTC, new BigDecimal(data[3]));
		    tokenBalances.put( Token.BCH, new BigDecimal(data[4]));
		    tokenBalances.put( Token.ETH, new BigDecimal(data[5]));
		    
		    User account = new User(userId, tokenBalances);
		    accounts.put(userId, account);
		   
		}
		csvReader.close();
		
		return accounts;
	}

	
}
