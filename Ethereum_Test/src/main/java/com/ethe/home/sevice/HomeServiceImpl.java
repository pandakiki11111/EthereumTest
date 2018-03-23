package com.ethe.home.sevice;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import com.ethe.util.HttpRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;

@Service("homeService")
public class HomeServiceImpl implements HomeService{
	
	@Autowired(required=true)
	Properties props;
	
	@Override
	public Map<String, String> newAccount(String coinname) {
		// TODO Auto-generated method stub
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("status", "0");
		
		if("ethe".equals(coinname)) etheNewAccount(map);
//		if("monera".equals(coinname)) moneraNewAccount(map);
		
		return map;
	}
	
	public List<Map<String, String>> accountList() {
		
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
		
//		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//		
//		try {
//			//방안 1
//			Map<String, String> headers = new HashMap<>();
//			headers.put("Access-Control-Allow-Origin", "*");
//	        
//			JsonRpcHttpClient jrac = new JsonRpcHttpClient(new URL(dao.getMonero_url()+"/json_rpc"), headers);
//			jrac.setContentType("application/json");
//			String param = "{'jsonrpc':'2.0','id':'0','method':'getbalance'}";
//			
//			jrac.invoke("post",new Object[] {param}, byteArrayOutputStream);
//
//			JsonNode node = jrac.getObjectMapper().readTree(byteArrayOutputStream.toString(StandardCharsets.UTF_8.name()));
//			
//			Iterator<String> itr = node.fieldNames();
//		      
//		     while(itr.hasNext()) {
//		        Object element = itr.next();
//		        System.out.println(element.toString()+" "+node.get(element.toString()));
//		     }
//		     
//		} catch (Exception e) {
//			e.printStackTrace();
//		} catch (Throwable t){
//			t.printStackTrace();
//		}
//		
		
		//방안2 socket 통신
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		HashMap<String, Object> params2 = new HashMap<String, Object>();
		
		params.put("jsonrpc", "2.0");
		params.put("id", "0");
		params.put("method", "make_integrated_address");
		params2.put("payment_id", "panda");
		params.put("params", params2);
		
		String url = props.getMonero_url()+"/json_rpc";
		
		HashMap<String, String> headers = new HashMap<>();
		headers.put("Access-Control-Allow-Origin", "*");
		headers.put("jsonrpc", "2.0");
		headers.put("id", "0");
		headers.put("method", "make_integrated_address");
		headers.put("Content-Type", "application/json-rpc");
		
		String resultDecode = Util.request(url, "POST", params, headers);
		
		if (!resultDecode.startsWith("error")) {
		    Map<String, Object> result;
		    try {
				result = new ObjectMapper().readValue(resultDecode, HashMap.class);
		
				System.out.println(result.get("status"));
		
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