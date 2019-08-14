package com.blockchain.mercury.rest;

import java.util.logging.Logger;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.blockchain.mercury.model.User;
import com.blockchain.mercury.model.WithdrawBalanceRequest;
import com.blockchain.mercury.service.RiskEngineServiceImpl;
import com.blockchain.mercury.service.response.WithdrawBalanceResponse;

@RestController   
@RequestMapping("/api")
public class RestContoller {

	private static final Logger logger = Logger.getLogger(RestContoller.class.getName());

	@Autowired
	private RiskEngineServiceImpl riskEngineService;
	
	
	@PostMapping(path = "/withdraw")
	public @ResponseBody WithdrawBalanceResponse withdrawBalance(@RequestBody WithdrawBalanceRequest request) {

		logger.info("POST /withdraw - orderId: "+request.getOrderId());		
		WithdrawBalanceResponse response = riskEngineService.withdrawBalance(request);		
		return response;
	}
	
	
	@GetMapping(path = "/user/{userId}")
	public @ResponseBody User getBalance(@PathVariable("userId") Long userId) {

		logger.info("GET /user/"+userId);	
		User user = riskEngineService.getUser(userId);
		return user;
	}


	@RequestMapping(value = "/ping", method = RequestMethod.GET)
	public ResponseEntity<String> ping() {
    	
    	logger.info("GET /ping");
		ResponseEntity<String> entity = new ResponseEntity<>("OK", HttpStatus.OK);
	    return entity;
    }

}
