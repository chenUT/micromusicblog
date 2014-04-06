package com.ece1779.group4.mmb;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@Controller
@RequestMapping("/hello")
public class HelloController {
	
	@RequestMapping(method = RequestMethod.GET)
	   public String printHello(ModelMap model) {
		UserService userService = UserServiceFactory.getUserService();
	      model.addAttribute("message", "Hello "+userService.getCurrentUser().getEmail());
	      return "hello";
	   }

}
