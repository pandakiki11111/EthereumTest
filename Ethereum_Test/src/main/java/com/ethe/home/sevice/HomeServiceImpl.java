package com.ethe.home.sevice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.NewAccountIdentifier;
import org.web3j.protocol.core.methods.response.EthAccounts;
import org.web3j.protocol.http.HttpService;

@Service("homeService")
public class HomeServiceImpl implements HomeService{
	
//	@Autowired
//	USERLISTDAOIMPL USERLISTDAO;
	
	@Override
	public Map<String, String> newAccount() {
		// TODO Auto-generated method stub
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("status", "0");
		
		Web3j web3 = Web3j.build(new HttpService("http://119.207.78.154:8545"));
		
		try {
			EthAccounts accounts = web3.ethAccounts().sendAsync().get();
			List<String> accountsList = accounts.getAccounts();

			for(int i = 0; i < accountsList.size(); i++){
				map.put("address"+i+"", accountsList.get(i));
			}
			
		    NewAccountIdentifier eth2 = null;
		    
			Admin admin = Admin.build(new HttpService("http://119.207.78.154:8545"));
			eth2 = admin.personalNewAccount("babys").sendAsync().get();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return map;
	}
	
	@Override
	public List<Map<String, String>> accountList() {
		// TODO Auto-generated method stub
		return null;
	}
}