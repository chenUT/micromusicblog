package com.ece1779.group4.mmb.controller.mvc;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

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
