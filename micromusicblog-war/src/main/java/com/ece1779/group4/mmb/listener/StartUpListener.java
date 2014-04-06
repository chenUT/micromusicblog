package com.ece1779.group4.mmb.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.ece1779.group4.mmb.model.Greeting;
import com.googlecode.objectify.ObjectifyService;

public class StartUpListener implements ServletContextListener{

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		//TODO Register all objectify entities
//		ObjectifyService.register(Greeting.class);
		
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
//		ObjectifyService.reset();
	}
	
}
