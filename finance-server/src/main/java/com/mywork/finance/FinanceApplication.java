package com.mywork.finance;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mywork.finance.utils.plasticine.ClasspathResourceManager;
import com.mywork.finance.utils.plasticine.LogbackConfigureLoader;
import com.mywork.finance.JettyServer;

public class FinanceApplication {

	private static ClasspathResourceManager resourceManager = ClasspathResourceManager.getResourceManager();

	public static final AbstractApplicationContext APP_CONTEXT = new ClassPathXmlApplicationContext("webapp/app-context.xml");

	public static void main(String args[]) throws Exception {
		LogbackConfigureLoader.initializeLogging(resourceManager, "logback.xml", "jul.properties");
		JettyServer jettyServer = (JettyServer) APP_CONTEXT.getBean("jettyServer");
		jettyServer.start();
	}
}