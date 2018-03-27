package com.ethe.home;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ethe.home.sevice.HomeServiceImpl;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	@Autowired
	HomeServiceImpl homeService;
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	//simple jsp test page
	@RequestMapping(value = "/", method = RequestMethod.POST)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate ); 
		
		return "home";
	}
	
	@RequestMapping(value = "/api", method = RequestMethod.POST)
	public ModelAndView CoreAPI(Model model, @RequestParam HashMap<String, String> map){
		logger.info("API Call");
		
		ModelAndView mv = new ModelAndView();
		mv.setViewName("jsonView");
		
		mv.addObject(homeService.apiCall(map));

		return mv;
	}
}
