package com.ethe.util;

import java.math.BigInteger;

import org.json.JSONObject;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;

public class util {
	
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
}
