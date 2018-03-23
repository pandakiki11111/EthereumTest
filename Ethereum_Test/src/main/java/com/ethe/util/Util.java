package com.ethe.util;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;

public class Util {
	
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
	

	/**
	 * http request 요청
	 */
	public static String request(String strHost, String strMemod, HashMap<String, Object> rgParams,  HashMap<String, String> httpHeaders) {
    	String response = "";

		// SSL
		if (strHost.startsWith("https://")) {
		    HttpRequest request = HttpRequest.get(strHost);
		    // Accept all certificates
		    request.trustAllCerts();
		    // Accept all hostnames
		    request.trustAllHosts();
		}
	
		if (strMemod.toUpperCase().equals("HEAD")) {
		} else {
		    HttpRequest request = null;
	
		    // POST/GET
		    if (strMemod.toUpperCase().equals("POST")) {
				request = new HttpRequest(strHost, "POST");
				request.readTimeout(300000);
				request.contentType("application/json-rpc");
		
				System.out.println("POST ==> " + request.url());
		
				if (httpHeaders != null && !httpHeaders.isEmpty()) {
				    request.headers(httpHeaders);
				    System.out.println(httpHeaders.toString());
				}
				
				System.out.println("content type => "+request.contentType());
				
				if (rgParams != null && !rgParams.isEmpty()) {
				    request.form(rgParams);
				    System.out.println(rgParams.toString());
				}
		    } else {
				request = HttpRequest.get(strHost
					+ mapToQueryString(rgParams));
				request.readTimeout(10000);
		
				System.out.println("Response was: " + response);
		    }
		    
		    if (request.ok()) {
			response = request.body();
		    } else {
			response = "error : " + request.code() + ", message : "
				+ request.body();
		    }
		    request.disconnect();
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
}
