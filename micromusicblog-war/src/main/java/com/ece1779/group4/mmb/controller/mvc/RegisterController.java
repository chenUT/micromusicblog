package com.ece1779.group4.mmb.controller.mvc;

import static com.ece1779.group4.mmb.dao.OfyService.ofy;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.ece1779.group4.mmb.model.UserInfo;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Key;

@Controller
@RequestMapping("/register")
public class RegisterController {
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView welcome(ModelMap model,
			HttpServletRequest req, 
	        HttpServletResponse resp) throws IOException {
		
	    UserService userService = UserServiceFactory.getUserService();
	    String userAccountName = userService.getCurrentUser().getEmail();
	    UserInfo user = ofy().load().key(Key.create(UserInfo.class,userAccountName)).now();
	    if(user != null){
	    	resp.sendRedirect("/profile");
	    	return null;
	    }
	    else{
	    	return new ModelAndView("register");
	    }

	    
		//ChannelService channelService = ChannelServiceFactory.getChannelService();
//		 String token = channelService.createChannel("publicChannelMMB");
//		 model.addAttribute("token", token);
//		 model.addAttribute("message", "Hello "+userService.getCurrentUser().getEmail());
//		 model.addAttribute("logouturl",userService.createLogoutURL("/login"));
		
	}
}
