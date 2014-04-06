package com.ece1779.group4.mmb.controller.mvc;	

import static com.ece1779.group4.mmb.dao.OfyService.ofy;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.ece1779.group4.mmb.model.Greeting;
import com.ece1779.group4.mmb.model.UserInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@Controller
@RequestMapping("/profile")
public class ProfileController {

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView welcome(ModelMap model) {
		
		 UserService userService = UserServiceFactory.getUserService(); 
		 String accountName = userService.getCurrentUser().getEmail();
	     UserInfo user = ofy().load().type(UserInfo.class).id(accountName).now();	
         if(user == null){
        	 ModelAndView regModel = new ModelAndView("register");
       	 	return regModel;
         }
		 //String userId = userService.getCurrentUser().getUserId();
		 ChannelService channelService = ChannelServiceFactory.getChannelService();
		
		 String token = channelService.createChannel(user.getAccountName());
		 
		 model.addAttribute("token", token);
		 model.addAttribute("message", "Hello "+user.getProfileName());
		 model.addAttribute("logouturl",userService.createLogoutURL("/login"));
		 
		 
		 
		 ModelAndView profModel = new ModelAndView("profile");
		 List<String> t = getList();
		 profModel.addObject("lists",t);
		 //profModel.addObject(attributeName, attributeValue)
         
		 //check whether user is a registered user
       	 return profModel;
	}
	
	private List<String> getList() {
		 
		List<String> list = new ArrayList<String>();
		list.add("List A");
		list.add("List B");
		list.add("List C");
		list.add("List D");
		list.add("List 1");
		list.add("List 2");
		list.add("List 3");
 
		return list;
 
	}
	
	@RequestMapping(method = RequestMethod.POST,
    		headers = {"Content-type=application/json"})
	public void postInfo(){
		Greeting g = new Greeting();
		g.setContent("test");
		g.setId(123l);
		
		UserService userService = UserServiceFactory.getUserService(); 
		String accountName = userService.getCurrentUser().getEmail();
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		String channelKey = accountName;
		
		ObjectWriter mapper = new ObjectMapper().writer();
		String json="{}";
		try {
			json = mapper.writeValueAsString(g);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		  
		channelService.sendMessage(new ChannelMessage(channelKey,json));
		
		//return new ResponseEntity<Greeting>(g,HttpStatus.OK);
	}
	
}
