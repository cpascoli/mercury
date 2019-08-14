package com.blockchain.mercury.service;

import java.util.Map;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.blockchain.mercury.model.TradeSettlementMessage;


@Configuration
public class KafkaConsumerConfig {

	@Bean
	public ConsumerFactory<?, ?> kafkaConsumerFactory(KafkaProperties properties) {
	    Map<String, Object> consumerProperties = properties.buildConsumerProperties();
	    DefaultKafkaConsumerFactory<Object, Object> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProperties);
	    consumerFactory.setValueDeserializer( new JsonDeserializer(TradeSettlementMessage.class) );	       
	    return consumerFactory;
	}
	
}