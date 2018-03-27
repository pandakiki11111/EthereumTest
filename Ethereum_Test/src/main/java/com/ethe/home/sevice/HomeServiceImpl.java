package com.ethe.home.sevice;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger logger = LoggerFactory.getLogger(HomeServiceImpl.class);
	
	@Autowired(required=true)
	Properties info;
	
	@Autowired(required=true)
	Util util;
	
	@Override
	public JSONObject apiCall(HashMap<String, String> map) {

		JSONObject result = findMethod(map);

		return result;
	}
	
	private JSONObject findMethod(HashMap<String, String> paramMap) {

		//coin name check
		if("error".equals(util.coinNameCheck(paramMap, info).get("status"))){
			return  util.mapToJsonObject(util.coinNameCheck(paramMap, info));
		}
		
		String coinname = paramMap.get("coinname");
		
		//command check
		if(!paramMap.containsKey("command")){ //대소문자 구분 (<>ignore case)
			paramMap.put("status", "error");
			paramMap.put("error_message", "command is missing");
			return util.mapToJsonObject(paramMap);
		}
		
		String command = paramMap.get("command");
		
		//generate method name
		String methodName = (coinname+"_"+command).toUpperCase();
		
		logger.info("coin_name : " + coinname + ", command : " + command + ", method_name : " + methodName);

		//check method exist
		Method[] methods = this.getClass().getMethods();
		boolean hasMethod = false;
		
		for (Method m : methods) {
		  if (m.getName().equals(methodName)) {
			  hasMethod = true;
		  }
		}
		
		if(hasMethod == false){
			paramMap.put("status", "error");
			paramMap.put("error_message", "method undefind (check command) <"+command+">");
		}
		
		//excute method
		Object value = null;
		Class<?> type = new HashMap<String, String>().getClass();
		Method method = null;
		
		try {
			
			Class<?> c = Class.forName(this.getClass().getName());
			Object obj = c.newInstance();
			
			method = c.getDeclaredMethod(methodName, type, Properties.class); // (methodname, parameter type1, parameter type2...)
			method.setAccessible(true);
			
			value = method.invoke(obj, paramMap, info); //properties를 보내줘야함.. new instance 라서 생성이 안됨
			
		} catch (Exception e){
			e.printStackTrace();
			
			paramMap.put("status", "error");
			paramMap.put("error_message", e.getMessage());
			
			return util.mapToJsonObject(paramMap);
		}finally{
			method.setAccessible(false);
		}
		
		return (JSONObject) value;
	}
	
	/*
	 * 모네로
	 */
	
	@SuppressWarnings("unused")
	private JSONObject MON_GETNEWACCOUNT(HashMap<String, String> paramMap, Properties info){
		
		logger.info("MON_GETNEWACCOUNT");
		
		JSONObject result = new JSONObject();
		
		String url = info.getMonero_url();
		
		HashMap<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json; charset=UTF-8");
		
		JSONObject jsonParams = new JSONObject();
		
		jsonParams.put("jsonrpc", "2.0");
		jsonParams.put("id", "0");
		jsonParams.put("method", "make_integrated_address");
		jsonParams.put("params", new JSONObject().put("payment_id", (paramMap.containsKey("account")) ? paramMap.get("account") : ""));
		
		String params = jsonParams.toString();

		logger.info("params : " + params);
		
		try{
			String resultDecode = util.request(url, params, headers);
			
			if (!resultDecode.startsWith("error")) {
			    try {
					result = new ObjectMapper().readValue(resultDecode, JSONObject.class);
			    } catch (IOException e) {
			    	e.printStackTrace();
			    }
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	private Map<String, String> MON_GETBALANCE(HashMap<String, String> paramMap, Properties info) {
		
		Map<String, String> result = new HashMap<>();
		
		String url = info.getMonero_url()+"/json_rpc";
		
		HashMap<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json; charset=UTF-8");
		
		JSONObject jsonParams = new JSONObject();
		
		jsonParams.put("jsonrpc", "2.0");
		jsonParams.put("id", "0");
		jsonParams.put("method", "getbalance");
		
		String params = jsonParams.toString();

		String resultDecode = util.request(url, params, headers);
		
		if (!resultDecode.startsWith("error")) {
		    try {
				result = new ObjectMapper().readValue(resultDecode, HashMap.class);
		    } catch (IOException e) {
		    	e.printStackTrace();
		    }
		}
		return result;
	}
	
	
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