package com.ethe.home.sevice;

import java.util.List;
import java.util.Map;

public interface HomeService {
	
	public Map<String, String> newAccount(Map<String, String> paramMap);

	public List<Map<String, String>> accountList(Map<String, String> paramMap);
	
	public List<Map<String, String>> getBalance(Map<String, String> paramMap);
	
}
