package com.ece1779.group4.mmb.controller.mvc;


import static com.ece1779.group4.mmb.dao.OfyService.ofy;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ece1779.group4.mmb.model.UserInfo;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;


@Controller
@RequestMapping("/login")
public class LoginController {

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public void userLogin( HttpServletRequest req, 
	        HttpServletResponse resp) throws IOException{
		
		UserService userService = UserServiceFactory.getUserService();

        String thisURL = req.getRequestURI();

        resp.setContentType("text/html");
        //user is logged in we redirect user profile page
        if (req.getUserPrincipal() != null) {
            resp.getWriter().println("<p>Hello, " +
                                     req.getUserPrincipal().getName() +" "
                                     + userService.getCurrentUser().getUserId()+" "+userService.getCurrentUser().getEmail()+
                                     "!  You can <a href=\"" +
                                     userService.createLogoutURL(thisURL) +
                                     "\">sign out</a>.</p>");     

            //using google account service get user email
            String accountName = userService.getCurrentUser().getEmail();
            UserInfo user = ofy().load().type(UserInfo.class).id(accountName).now();
            
            if(user == null){
                 resp.sendRedirect("/register");
            }
            else{
                 resp.sendRedirect("/profile");
            }
            
            //redirect to userprofile controller
        } else {
            resp.getWriter().println("<p>Please <a href=\"" +
                                     userService.createLoginURL(thisURL) +
                                     "\">sign in</a>.</p>");
        }
	}
	
}
