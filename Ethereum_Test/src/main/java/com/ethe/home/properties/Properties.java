package com.ethe.home.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class Properties {
	@Value("#{properties['ethereum.ip']}")
	private String ETHEREUM_IP;
	
	@Value("#{properties['ethereum.port']}")
	private String ETHEREUM_PORT;
	
	@Value("#{properties['monero.ip']}")
	private String MONERO_IP;
	
	@Value("#{properties['monero.port']}")
	private String MONERO_PORT;

	public String getETHEREUM_IP() {
		return ETHEREUM_IP;
	}

	public String getETHEREUM_PORT() {
		return ETHEREUM_PORT;
	}

	public String getEthereum_url() {
		return ETHEREUM_IP + ":" + ETHEREUM_PORT;
	}

	public String getMONERO_IP() {
		return MONERO_IP;
	}

	public String getMONERO_PORT() {
		return MONERO_PORT;
	}

	public String getMonero_url() {
		return MONERO_IP + ":" + MONERO_PORT;
	}
}
