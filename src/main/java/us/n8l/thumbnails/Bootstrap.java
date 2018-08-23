package us.n8l.thumbnails;

import ch.qos.logback.classic.Level;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

class Bootstrap {
  private static final Capture capture = new Capture();

  public static void main(String[] args) throws Exception {
    Logger log = LoggerFactory.getLogger(Bootstrap.class);
    // this seems harder than log4j :( -- clip found online
    ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger("org.eclipse")).setLevel(Level.toLevel("info"));
    Server server = new Server();
    ServerConnector connector = new ServerConnector(server);
    connector.setPort(8080);
    server.setConnectors(new Connector[]{connector});

    ServletContextHandler context = new ServletContextHandler();
    context.setContextPath("/");
    context.addServlet(IndexServlet.class, getBasePath());
    context.addServlet(GetThumbnailServlet.class, getBasePath() + "t");

    HandlerCollection handlers = new HandlerCollection();
    handlers.setHandlers(new Handler[]{context, new DefaultHandler()});
    server.setHandler(handlers);
    log.info("BaseURL was set to : " + getBaseUrl());
    server.start();
    server.join();
  }

  static String getBasePath() {
    String basePath = System.getProperty("BASE_PATH");
    return (basePath == null) ? "/" : basePath;
  }

  @Nonnull
  static String getBaseUrl() {
    String baseUri = System.getProperty("BASE_URI");
    return (baseUri == null) ? "http://localhost:8080" + getBasePath() : baseUri + getBasePath();
  }

  public static Capture getCapture() {
    return capture;
  }
}