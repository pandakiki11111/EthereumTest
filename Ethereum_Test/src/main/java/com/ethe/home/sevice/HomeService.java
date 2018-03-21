package com.ethe.home.sevice;

import java.util.List;
import java.util.Map;

public interface HomeService {
	
	public Map<String, String> newAccount();

	public List<Map<String, String>> accountList();
	
}
