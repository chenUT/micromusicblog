package com.ece1779.group4.mmb.controller.mvc;

import static com.ece1779.group4.mmb.dao.OfyService.ofy;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ece1779.group4.mmb.model.UserInfo;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@RequestMapping("/")
@Controller
public class IndexController {
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView welcome() throws IOException{
		ModelAndView mav = new ModelAndView("index");
		return mav;
	}
}
