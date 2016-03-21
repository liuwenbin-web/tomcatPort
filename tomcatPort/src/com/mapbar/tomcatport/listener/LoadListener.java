package com.mapbar.tomcatport.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

public class LoadListener implements ServletContextListener{
	Logger logger = Logger.getLogger(this.getClass());
	
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("OK");
	}
	
	public void contextDestroyed(ServletContextEvent sce) {
		logger.info("LoadListener destroyed");
	}
}