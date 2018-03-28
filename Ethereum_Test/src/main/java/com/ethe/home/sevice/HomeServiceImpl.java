package com.ethe.home.sevice;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
		return findMethod(map);
	}
	
	/**
	 * ※ parameter 체크 후 method 명 생성하여 method 실행 [coninname / command 필수]
	 *   
	 * @param paramMap
	 * @return
	 */
	private JSONObject findMethod(HashMap<String, String> paramMap) {
		
		/* 
			check coin name availability >> check parameter has command 
			>> generate method name with coin name and command ( form : coinname_methodname )
			>> check class has method by method name >> excute method by method name 
		*/
		
		//coin name check
		if("error".equals(util.coinNameCheck(paramMap, info).get("status"))){
			return util.mapToJsonObject(util.coinNameCheck(paramMap, info));
		}
		
		//command check
		if(!paramMap.containsKey("command")){ //대소문자 구분함 (<>ignore case)
			
			paramMap.put("status", "error");
			paramMap.put("error_message", "command is missing");
			
			return util.mapToJsonObject(paramMap);
		}

		//get coinname and command from parameter
		String coinname = paramMap.get("coinname"); 
		String command = paramMap.get("command");
		
		//generate method name
		String methodName = (coinname+"_"+command).toUpperCase();
		
		logger.info("coin_name : " + coinname + ", command : " + command + ", method_name : " + methodName);

		//check method exist in class
		Method[] methods = this.getClass().getDeclaredMethods(); // getMethods => returns only public methods
		boolean hasMethod = false;

		for (Method m : methods) {
		  if (m.getName().equals(methodName)) {
			  hasMethod = true;
		  }
		}
		
		if(hasMethod == false){
			paramMap.put("status", "error");
			paramMap.put("error_message", "method undefind (check command) <"+command+">");
			
			return  util.mapToJsonObject(paramMap);
		}
		
		//excute method
		Method method = null;
		Object result = null;
		
		//return parameter type class
		Map<String, String> map = new HashMap<String, String>(); 
		Class<?> type = map.getClass();
		
		try {
			
			Class<?> c = Class.forName(this.getClass().getName());
			Object obj = c.newInstance();
			
			method = c.getDeclaredMethod(methodName, type, Properties.class); // (methodname, parameter type1, parameter type2...)
			method.setAccessible(true);
			
			result = method.invoke(obj, paramMap, info); //properties를 보내줘야함.. new instance 라서 생성이 안됨
			
		} catch (Exception e){
			e.printStackTrace();
			
			paramMap.put("status", "error");
			paramMap.put("error_message", e.getMessage());
			
			return util.mapToJsonObject(paramMap);
		}finally{
			method.setAccessible(false); // it works after catch statement
		}
		
		return (JSONObject) result;
	}
	
	/*
	 * 모네로
	 */
	
	/**
	 * 모네로 코어 wallet rpc api 요청
	 * 
	 * @param jsonParams
	 * @param info
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private JSONObject monero_request(JSONObject jsonParams, Properties info){
		Util util = new Util();
		JSONObject result = new JSONObject();
		
		String url = info.getMonero_url();
		
		HashMap<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json; charset=UTF-8");
		
		jsonParams.put("jsonrpc", "2.0");
		
		String params = jsonParams.toString();

		logger.info("params : " + params);
		
		try{
			String resultDecode = util.request(url, params, headers);
			
			if (!resultDecode.startsWith("error")) {
				Map<String, String> resultMap;
		    	resultMap = new ObjectMapper().readValue(resultDecode, HashMap.class);
		    	result = util.mapToJsonObject(resultMap);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		logger.info(result.toString());
		
		return result;
	}
	
	/**
	 * 신규 address 생성
	 * 
	 * @param paramMap
	 * @param info
	 * @return
	 */
	@SuppressWarnings({ "unused" })
	private JSONObject MON_GETNEWACCOUNT(HashMap<String, String> paramMap, Properties info){

		JSONObject jsonParams = new JSONObject();
		
		jsonParams.put("id", "0");
		jsonParams.put("method", "make_integrated_address");
		jsonParams.put("params", new JSONObject().put("payment_id", paramMap.containsKey("account") ? paramMap.get("account") : ""));
		logger.info("account : " + (paramMap.containsKey("account") ? paramMap.get("account") : ""));
		
		return monero_request(jsonParams, info);
	}
	
	/**
	 * 밸런스 값 가져오기
	 * 
	 * @param paramMap
	 * @param info
	 * @return
	 */
	@SuppressWarnings({ "unused" })
	private JSONObject MON_GETBALANCE(HashMap<String, String> paramMap, Properties info) {
		JSONObject jsonParams = new JSONObject();
		
		jsonParams.put("id", "0");
		jsonParams.put("method", "getbalance");
		
		return monero_request(jsonParams, info);
	}
	
	/**
	 * wallet 주소 가져오기
	 * 
	 * @param paramMap
	 * @param info
	 * @return
	 */
	@SuppressWarnings("unused")
	private JSONObject MON_GETWALLETADDRESS(HashMap<String, String> paramMap, Properties info) {
		JSONObject jsonParams = new JSONObject();
		
		jsonParams.put("id", "0");
		jsonParams.put("method", "getaddress");
		
		return monero_request(jsonParams, info);
	}
	
	/**
	 * 송금 (우선은 1:1 로 되었음.. 기획에 따라 변경)
	 * 
	 * @param paramMap
	 * @param info
	 * @return
	 */
	@SuppressWarnings("unused")
	private JSONObject MON_WITHDRAWALCOIN(HashMap<String, String> paramMap, Properties info) {
		JSONObject jsonParams = new JSONObject();
		
		jsonParams.put("id", "0");
		jsonParams.put("method", "transfer");
		
		//주소와 금액 Setting
		Map<String, String> destination = new HashMap<>();
		
		destination.put("amount", paramMap.get("amount"));
		destination.put("toaddress", paramMap.get("toaddress"));
		
		//params setting
		JSONObject dataParams = new JSONObject();
		
		dataParams.put("mixin", 4); // total 5 signatures (checkout monero ring signature)
		dataParams.put("get_tx_key", true); //Return the transaction key after sending
		dataParams.put("destinations", destination);
		
		jsonParams.put("params", dataParams);
		
		logger.info(jsonParams.toString());
		
		return monero_request(jsonParams, info);
	}
}