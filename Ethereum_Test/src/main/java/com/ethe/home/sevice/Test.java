package com.ethe.home.sevice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.NewAccountIdentifier;
import org.web3j.protocol.core.methods.response.EthAccounts;
import org.web3j.protocol.http.HttpService;

import com.ethe.home.properties.Properties;

public class Test {
	
	@Autowired(required=true)
	Properties info;
	
	/*
	 * 이더리움
	 */
	
	@SuppressWarnings("unused")
	private void etheNewAccount(Map<String, String> map) {
		
		try {
		    NewAccountIdentifier eth2 = null;
		    
			Admin admin = Admin.build(new HttpService(info.getEthereum_url()));
			eth2 = admin.personalNewAccount("babys").sendAsync().get();
			
			map.put("address", String.valueOf(eth2.getId()));
			map.put("id", eth2.getAccountId());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unused")
	private List<Map<String, String>> etheAccountList(Map<String, String> paramMap) {
		
		List<Map<String, String>> resultList = new ArrayList<>();
		
		try {
			
			Web3j web3 = Web3j.build(new HttpService(info.getEthereum_url()));
			
			EthAccounts accounts = web3.ethAccounts().sendAsync().get();
			List<String> accountsList = accounts.getAccounts();

			for(int i = 0; i < accountsList.size(); i++){
				resultList.get(i).put("address("+i+")", accountsList.get(i));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resultList;
	}
}
