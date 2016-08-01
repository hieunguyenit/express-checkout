package com.mbv.mca.checkout.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
 
@Controller
@RequestMapping("/hello.htm")
public class HelloWorldController{
 
	@RequestMapping(method = RequestMethod.GET)
	public String helloWorld(ModelMap model){
 
		model.addAttribute("msg", "hello world");
 
		return "hello";
	}
}