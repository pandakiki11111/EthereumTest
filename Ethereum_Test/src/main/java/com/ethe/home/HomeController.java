package com.ethe.home;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.ethe.home.sevice.HomeServiceImpl;
import com.ethe.util.Util;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	@Autowired
	HomeServiceImpl homeService;
	
	@Autowired(required=true)
	Util util;
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	//simple jsp test page
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate ); 
		
		return "home";
	}
	
	@RequestMapping(value = "/api", method = RequestMethod.POST)
	public ModelAndView CoreAPI(Model model, @RequestBody Map<String, String> param){
//		/@RequestParam HashMap<String, String> map
		logger.info("API Call");
		
		ModelAndView mv = new ModelAndView();
		mv.setViewName("jsonView");
		
		JSONObject result = homeService.apiCall(new Util().mapToJsonObject(param));
		if(!result.has("status")) result.put("status", "success");
		
		mv.addObject("data", result.toMap());

		return mv;
	}
}
