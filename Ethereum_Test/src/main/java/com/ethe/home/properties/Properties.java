package com.ethe.home.properties;

import java.util.Arrays;
import java.util.List;

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
	
	@Value("#{properties['coinnames']}")
	private String COINNAMES;
	
	//coin name list
	public String COIN_MON = "MON";
	
	//command list (확인용)
	public String METHOD_NEW_ACCOUT = "getnewaccount";
	public String METHOD_GET_WALLET_ADDRESS = "getwalletaddress";
	public String METHOD_GET_BALANCE = "getbalance";
	public String METHOD_WITHDRAWAL_COIN = "withdrawalcoin";

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
		return MONERO_IP + ":" + MONERO_PORT + "/json_rpc";
	}
	
	public List<String> getCoinNames(){
		List<String> list = Arrays.asList(COINNAMES.toUpperCase().split(":"));
		return list;
	}
}
