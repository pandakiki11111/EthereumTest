package com.ethe.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;

import com.ethe.home.properties.Properties;

public class Util {
	
	@Autowired(required=true)
	static
	Properties props;
	
	/** 
	 * address 와 password 를 수동으로 생성
	 * 
	 * String seed = UUID.randomUUID().toString();
     * JSONObject result = process(seed);
     * 
     * @param seed
     * @return JSONObject
     */
	@SuppressWarnings("unused")
	private static JSONObject generateAccountInfo(String seed){

        JSONObject processJson = new JSONObject();

        try {
           ECKeyPair ecKeyPair = Keys.createEcKeyPair();
           BigInteger privateKeyInDec = ecKeyPair.getPrivateKey();

           String sPrivatekeyInHex = privateKeyInDec.toString(16);

           WalletFile aWallet = Wallet.createLight(seed, ecKeyPair);
           String sAddress = aWallet.getAddress();

           processJson.put("address", "0x" + sAddress);
           processJson.put("privatekey", sPrivatekeyInHex);

       } catch (Exception e) {
    	   e.printStackTrace();
       }

       return processJson;
	}
	
	public static String request(String string_url, String params,  HashMap<String, String> headers){
		
		 String response = "";
		
		try {
			
			URL url = new URL(string_url);
			URLConnection con = url.openConnection();
			HttpURLConnection http = (HttpURLConnection)con;
			http.setRequestMethod("POST");
			http.setDoOutput(true);
			
			byte[] out = params.getBytes(StandardCharsets.UTF_8);
			int length = out.length;

			http.setFixedLengthStreamingMode(length);
			http.setRequestProperty("Content-Type", "application/json-rpc; charset=UTF-8");
			http.connect();
			try(OutputStream os = http.getOutputStream()) {
			    os.write(out);
			}
			
			InputStream in = http.getInputStream();
			
			StringBuilder textBuilder = new StringBuilder();
		    try (Reader reader = new BufferedReader(new InputStreamReader(in, Charset.forName(StandardCharsets.UTF_8.name())))) {
		        int c = 0;
		        while ((c = reader.read()) != -1) {
		            textBuilder.append((char) c);
		        }
		    }
		    
		    response = textBuilder.toString();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			
		}
		
		return response;
	}
	
	/**
	 * map 을 query 문자로 변경
	 */
	public static String mapToQueryString(Map<String, Object> map) {
		StringBuilder string = new StringBuilder();
	
		if (map.size() > 0) {
		    string.append("?");
		}
	
		for (Entry<String, Object> entry : map.entrySet()) {
		    string.append(entry.getKey());
		    string.append("=");
		    string.append(entry.getValue());
		    string.append("&");
		}
	
		return string.toString();
    }
	
	public static Map<String, String> coinNameCheck(Map<String, String> map){
		
		if(map.isEmpty()){
			map.put("status", "error");
			map.put("error_message", "parameter is empty");
		}else if(!map.containsKey("coinname")){
			map.put("status", "error");
			map.put("error_message", "coinname is empty");
		}else if(props.getCoinNames().contains(map.get("coinname").toUpperCase())){
			map.put("status", "error");
			map.put("error_message", "coinname is invalid");
		}else{
			map.put("status", "success");
		}
		
		return map;
	}
}
