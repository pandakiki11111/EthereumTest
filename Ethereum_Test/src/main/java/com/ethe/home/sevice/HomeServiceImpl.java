package com.ethe.home.sevice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.NewAccountIdentifier;
import org.web3j.protocol.core.methods.response.EthAccounts;
import org.web3j.protocol.http.HttpService;

import com.ethe.util.Util;
import com.ethe.home.properties.Properties;

@Service("homeService")
public class HomeServiceImpl implements HomeService{
	
	@Autowired(required=true)
	Properties props;
	
	@Override
	public Map<String, String> newAccount(Map<String, String> paramMap) {
		
		Map<String, String> map = new HashMap<String,String>();
		
		if("error".equals(Util.coinNameCheck(paramMap).get("status"))){
			return paramMap;
		}
		
		String coinname = paramMap.get("coinname").toUpperCase();
		
		if(props.COIN_ETH.equals(coinname)) etheNewAccount(map);
//		if(props.COIN_MON.equals(coinname)) moneraNewAccount(map);
		
		return map;
	}
	
	public List<Map<String, String>> accountList(Map<String, String> paramMap) {
		
		List<Map<String, String>> resultList = new ArrayList<>();
		
		try {
			
			Web3j web3 = Web3j.build(new HttpService(props.getEthereum_url()));
			
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
	
	public List<Map<String, String>> getBalance(Map<String, String> paramMap) {
		
		String coinname = paramMap.get("coinname");
		
		if("monero".equals(coinname)); getMoneroBalance(paramMap);
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private void getMoneroBalance(Map<String, String> paramMap) {
		JSONObject jsonParams = new JSONObject();
		
		jsonParams.put("jsonrpc", "2.0");
		jsonParams.put("id", "0");
		jsonParams.put("method", "getbalance");
//		jsonParams.put("params", new JSONObject().put("payment_id",""));
//		49GDLPceRyEP6pE8nDhxUjeTeX9qZeKwQBid3QUfmvFBVbpgLhvbwhFJAM27Ut4hESAdaj3R2Sni49LsPwbP9QxgPxyypkm
		
		String params = jsonParams.toString();
		
		System.out.println("params "+params);
		
		String url = props.getMonero_url()+"/json_rpc";
		
		HashMap<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json; charset=UTF-8");
		
		String resultDecode = Util.request(url,params, headers);
		
		if (!resultDecode.startsWith("error")) {
		    Map<String, Object> result;
		    try {
				result = new ObjectMapper().readValue(resultDecode, HashMap.class);
		
				JSONObject json = new JSONObject();
				for( Map.Entry<String, Object> entry : result.entrySet() ) {
					String key = entry.getKey();
					Object value = entry.getValue();
					json.put(key, value);
				}
				
				System.out.println("json String " + json.toString());
				
		    } catch (IOException e) {
			e.printStackTrace();
		    }
		}
		
	}
	
	private void etheNewAccount(Map<String, String> map) {
		
		try {
		    NewAccountIdentifier eth2 = null;
		    
			Admin admin = Admin.build(new HttpService(props.getEthereum_url()));
			eth2 = admin.personalNewAccount("babys").sendAsync().get();
			
			map.put("address", String.valueOf(eth2.getId()));
			map.put("id", eth2.getAccountId());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}