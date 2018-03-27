package com.ethe.home.sevice;

import java.util.HashMap;

import org.json.JSONObject;

public interface HomeService {
	
	public JSONObject apiCall(HashMap<String, String> map);
	
}
