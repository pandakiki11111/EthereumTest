package com.ethe.home.sevice;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
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
	public JSONObject apiCall(JSONObject param) {
		return findMethod(param);
	}
	
	/**
	 * ※ parameter 체크 후 method 명 생성하여 method 실행 [coninname / command 필수]
	 *   
	 * @param paramMap
	 * @return
	 */
	private JSONObject findMethod(JSONObject param) {
		
		//coin name check
		if("error".equals(util.coinNameCheck(param, info).get("status"))){
			return util.coinNameCheck(param, info);
		}
		
		//command check
		if(!param.has("command")){ //대소문자 구분함 (<>ignore case)
			
			param.put("status", "error");
			param.put("error_message", "command is missing");
			
			return param;
		}

		//get coinname and command from parameter
		String coinname = param.get("coinname").toString(); 
		String command = param.get("command").toString();
		
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
			param.put("status", "error");
			param.put("error_message", "method undefind (check command) <"+command+">");
			
			return param;
		}
		
		//excute method
		Method method = null;
		Object result = null;
		
		try {
			
			Class<?> c = Class.forName(this.getClass().getName());
			Object obj = c.newInstance();
			
			method = c.getDeclaredMethod(methodName, JSONObject.class, Properties.class); // (methodname, parameter type1, parameter type2...)
			method.setAccessible(true);
			
			result = method.invoke(obj, param, info); //properties를 보내줘야함.. new instance 라서 생성이 안됨
			
		} catch (Exception e){
			e.printStackTrace();
			
			param.put("status", "error");
			param.put("error_message", e.getMessage());
			
			return param;
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
		jsonParams.put("id", "0"); // json rpc response bring this id
		
		String params = jsonParams.toString();

		logger.info("params : " + params);
		
		try{
			String resultDecode = util.request(url, params, headers);
			
			if (!resultDecode.startsWith("error")) {
				Map<String, String> resultMap;
		    	resultMap = new ObjectMapper().readValue(resultDecode, HashMap.class);
		    	result = util.mapToJsonObject(resultMap);
			}else{
				result.put("status", "error");
				result.put("error_message", resultDecode.split("@#")[1]);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		logger.info(result.toString());
		
		return result;
	}
	
	/**
	 * monero open wallet
	 * API 사용전 wallet을 open 해야한다
	 * @param param
	 * @param info
	 * @return
	 */
	private JSONObject monero_open_wallet(JSONObject param, Properties info){
		
		JSONObject jsonParams = new JSONObject();
		JSONObject data = new JSONObject();
		
		data.put("filename", param.has("account") ? param.get("account") : "");
		data.put("password", param.has("password") ? param.get("password") : "");
		
		jsonParams.put("method", "open_wallet");
		jsonParams.put("params", data);
		
		return monero_request(jsonParams, info);
		
	}
	
	/**
	 * 신규 address 생성
	 * 
	 * @param paramMap
	 * @param info
	 * @return
	 */
	@SuppressWarnings({ "unused" })
	private JSONObject MON_GETNEWACCOUNT(JSONObject param, Properties info){

		JSONObject jsonParams = new JSONObject();
		JSONObject data = new JSONObject();
		
		data.put("filename", param.has("account") ? param.get("account") : "");
		data.put("password", param.has("password") ? param.get("password") : "");
		data.put("language", param.has("language") ? param.get("language") : "English");
		
		jsonParams.put("method", "create_wallet");
		jsonParams.put("params", data);
		
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
	private JSONObject MON_GETWALLETADDRESS(JSONObject param, Properties info) {
		
		JSONObject open = monero_open_wallet(param, info);
		if(!open.has("id") || !"0".equals(open.get("id").toString())) return open;
		
		JSONObject jsonParams = new JSONObject();
		
		jsonParams.put("method", "getaddress");
		
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
	private JSONObject MON_GETBALANCE(JSONObject param, Properties info) {
		
		JSONObject open = monero_open_wallet(param, info);
		if(!open.has("id") || !"0".equals(open.get("id").toString())) return open;
		
		param.put("method", "getbalance");
		
		JSONObject result = new JSONObject(monero_request(param, info).toString());
		
		System.out.println(result.toString());
		System.out.println(result.getJSONObject("result").get("balance"));
		
		//setting result data
		if(result.getJSONObject("result").has("balance")) result.getJSONObject("result").put("balance", new Util().toMonero(info.unit_piconero, (double) result.getJSONObject("result").get("balance")));
		if(result.getJSONObject("result").has("balance")) result.getJSONObject("result").put("unlocked_balance", new Util().toMonero(info.unit_piconero, (double) result.getJSONObject("result").get("unlocked_balance")));
		
		return result;
	}
	
	
	/**
	 * 송금
	 * 
	 * @param paramMap
	 * @param info
	 * @return
	 */
	@SuppressWarnings({ "unused" })
	private JSONObject MON_WITHDRAWALCOIN(JSONObject param, Properties info) {
		
		JSONObject open = monero_open_wallet(param, info);
		if(!open.has("id") || !"0".equals(open.get("id").toString())) return open;
		
		//주소와 금액 destination Setting
		JSONObject destination = new JSONObject();
		
		JSONObject params = new JSONObject(param.toString());
		JSONArray dataArray =  new JSONArray(params.get("data").toString());
		
		for(int i = 0; i < dataArray.length(); i++){
			destination.put("amount", dataArray.getJSONObject(i).getDouble("amount"));
			destination.put("address", dataArray.getJSONObject(i).getString("toaddress"));
		}
		
		//params setting
		JSONObject dataParams = new JSONObject();
		
		dataParams.put("mixin", 4); // total 5 signatures (checkout monero ring signature)
		dataParams.put("get_tx_key", true); //Return the transaction key after sending
		dataParams.put("destinations", destination);
		
		JSONObject jsonParams = new JSONObject();
		
		jsonParams.put("method", "transfer");
		jsonParams.put("params", dataParams);
		
		return monero_request(jsonParams, info);
	}
	
	@SuppressWarnings("unused")
	private JSONObject MON_GETTRANSACTION(JSONObject param, Properties info) {
		JSONObject open = monero_open_wallet(param, info);
		if(!open.has("id") || !"0".equals(open.get("id").toString())) return open;
		
		//params setting
		JSONObject dataParams = new JSONObject();
		dataParams.put("pool", true);
		
		JSONObject jsonParams = new JSONObject();
		
		jsonParams.put("method", "get_transfers");
		jsonParams.put("params", dataParams);
		
		JSONObject result = monero_request(param, info);
		
		//setting result data
		if(result.has("result.pool.amount")) result.put("result.pool.amount", new Util().toMonero(info.unit_piconero, (double) result.get("result.pool.amount")));
		
		return result;
	}
}