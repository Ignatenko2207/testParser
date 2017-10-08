package com.mywork.finance;

import com.mywork.finance.settings.ServerSettings;
import com.mywork.finance.JettyServer;
import com.mywork.finance.FinanceApplication;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.rewrite.handler.RewriteHandler;
import org.eclipse.jetty.rewrite.handler.RewriteRegexRule;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.File;
import java.net.URL;

@Slf4j
public class JettyServer {

	public static XmlWebApplicationContext WEB_CONTEXT;

	// that folder contains Spring context
	public static final String SPRING_ROOT = "webapp";

	public static final String MVC_SERVLET_NAME = "rest";
	public static final String SPRING_SERVER_CONTEXT_FILENAME = "server-context.xml";

	private Server server;
	private boolean serverStarted;
	private ServletContextHandler contextHandler;

	@Autowired
	private ServerSettings serverSettings;

	private JettyServer() {
	}

	public ServletContext getServletContext() {
		return this.contextHandler.getServletContext();
	}

	public void start() throws ServletException {

		if (serverStarted) {
			return;
		}

		server = new Server();

		// start to add ssl data

		ServerConnector connector = null;

		if (serverSettings.isUseSSL()) {

			SslContextFactory contextFactory = new SslContextFactory();
			contextFactory.setKeyStorePath(serverSettings.getKeyStoreLocation());
			contextFactory.setKeyStorePassword(serverSettings.getKeyStorePassword());
			SslConnectionFactory sslConnectionFactory = new SslConnectionFactory(contextFactory, org.eclipse.jetty.http.HttpVersion.HTTP_1_1.toString());

			HttpConfiguration config = new HttpConfiguration();
			config.setSecureScheme("https");
			config.setSecurePort(serverSettings.getSslPort());
			config.setOutputBufferSize(32786);
			config.setRequestHeaderSize(8192);
			config.setResponseHeaderSize(8192);

			HttpConfiguration sslConfiguration = new HttpConfiguration(config);
			sslConfiguration.addCustomizer(new SecureRequestCustomizer());
			HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory(sslConfiguration);

			connector = new ServerConnector(server, sslConnectionFactory, httpConnectionFactory);
			connector.setPort(serverSettings.getSslPort());
		}
		// stop to add ssl data

		// Simple connector if SSL is OFF
		if (connector == null) {
			connector = new ServerConnector(server);
			connector.setPort(serverSettings.getPort());
			connector.setHost(serverSettings.getHost());
		}

		server.addConnector(connector);

		ResourceHandler webResourceHandler = new ResourceHandler();
		webResourceHandler.setDirectoriesListed(true);
		webResourceHandler.setWelcomeFiles(new String[] { "index.html" });
		webResourceHandler.setResourceBase("frontend/dist");
		// disable static files locking
		webResourceHandler.setMinMemoryMappedContentLength(-1);

		ResourceHandler filesResourceHandler = new ResourceHandler();
		filesResourceHandler.setDirectoriesListed(true);
		// disable static files locking
		filesResourceHandler.setMinMemoryMappedContentLength(-1);

		HandlerList handlers = new HandlerList();

		RewriteHandler rewrite = new RewriteHandler();
		rewrite.setRewriteRequestURI(true);
		rewrite.setRewritePathInfo(true);
		rewrite.setOriginalPathAttribute("requestedPath");

		RewriteRegexRule redirect = new RewriteRegexRule();
		redirect.setRegex("^/(?!api)(?!swagger)(?!v2)[^\\.]+$");
		redirect.setHandling(false);
		redirect.setReplacement("/index.html");
		redirect.setTerminating(false);

		rewrite.addRule(redirect);

		// end of rewrite urls

		handlers.addHandler(rewrite);

		handlers.addHandler(webResourceHandler);
		handlers.addHandler(filesResourceHandler);
		handlers.addHandler(getServletHandler());
		server.setHandler(handlers);

		WebSocketServerContainerInitializer.configureContext(getServletHandler());

		try {
			server.start();
		} catch (Exception e) {
			log.error("Failed to start server %s", e);
			System.exit(0);
		}

		// Флаг для проверки, что сервер стартовал
		serverStarted = true;
		log.info("Server started");
	}

	public ServletContextHandler getServletHandler() {
		if (contextHandler == null) {

			File tempDirectory = createFolderIfNotExists(serverSettings.getTempDirectory());
			createFolderIfNotExists(serverSettings.getGcLogsDirectory());

			contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
			contextHandler.setAttribute("javax.servlet.context.tempdir", tempDirectory);
			contextHandler.setClassLoader(Thread.currentThread().getContextClassLoader());
			contextHandler.setContextPath("/");

			WEB_CONTEXT = new XmlWebApplicationContext();
			WEB_CONTEXT.setConfigLocations(SPRING_SERVER_CONTEXT_FILENAME);
			WEB_CONTEXT.setParent(FinanceApplication.APP_CONTEXT);

			ServletHolder mvcServletHolder = new ServletHolder(MVC_SERVLET_NAME, new DispatcherServlet(WEB_CONTEXT));
			contextHandler.addServlet(mvcServletHolder, "/");
			contextHandler.setResourceBase(getBaseUrl());
			int contentSize = contextHandler.getMaxFormContentSize();
			int maxContentSize = 500 * 1000 * 1000;
			contextHandler.setMaxFormContentSize(maxContentSize);

			log.info("Max content size will be changed from %s to %s", contentSize, maxContentSize);

		}
		return contextHandler;
	}

	private File createFolderIfNotExists(String folderName) {
		if (folderName != null) {
			File tempDirectory = new File(folderName);
			if (!tempDirectory.exists()) {
				log.info("Creating a folder %s", folderName);
				tempDirectory.mkdirs();
			}
			return tempDirectory;
		} else {
			return null;
		}
	}

	public void join() throws InterruptedException {
		server.join();
	}

	public void stop() throws Exception {
		server.stop();
		serverStarted = false;
	}

	private String getBaseUrl() {
		URL webInfUrl = JettyServer.class.getClassLoader().getResource(SPRING_ROOT);
		if (webInfUrl == null) {
			throw new RuntimeException("Failed to find web application root: " + SPRING_ROOT);
		}
		return webInfUrl.toExternalForm();
	}

	public boolean isServerStarted() {
		return serverStarted;
	}

}
