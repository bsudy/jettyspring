/*
 * Moresby Coffee Bean
 *
 * Copyright (c) 2012, Barnabas Sudy (barnabas.sudy@gmail.com)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */
package org.moresbycoffee.jettyspring;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.webapp.WebAppContext;

/**
 * Utility class to start and stop embedded Jetty server.<br>
 * <a href="http://jetty.codehaus.org/jetty/">http://jetty.codehaus.org/jetty/</a>.<br>
 * <br>
 * The class provides three way to start a Jetty server and deploy a (Spring MVC) application.
 * <ul>
 * <li>Annotation driven deployment using {@link org.springframework.context.annotation.Configuration Configuration} class
 *     ({@link org.springframework.web.context.support.AnnotationConfigWebApplicationContext AnnotationConfigWebApplicationContext})</li>
 * <li>Classic xml based using servlet-context and root-context xml-s.</li>
 * <li>Generic using Web resource directory with web.xml.</li>
 * </ul>
 *
 * @author Barnabas Sudy (barnabas.sudy@gmail.com)
 * @since 2012
 */
public final class JettyRunner {

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(JettyRunner.class);

    /** The port number of the test Jetty server. */
    protected static final int DEFAULT_PORT_NUMBER = 9876;

    /** The default context path of test deployment. */
    protected static final String DEFAULT_PATH = "/jettyspringtest";

    /**
     * Starts Jetty server on the {@link #DEFAULT_PORT_NUMBER} port and
     * deploys the given configuration class on {@link #DEFAULT_PATH} path.
     * <p>
     * <strong>WARNING:</strong> The Web resource directory is not defined.
     * Don't use this if you have anything in the web resource
     * directory what you want to use.
     * </p>
     *
     * @param configuration The spring {@link org.springframework.context.annotation.Configuration configuration} class.
     * @return The started Jetty server. (NonNull)
     * @throws Exception If Jetty server start or deploy fails.
     */
    public static Server startJetty(final Class<?> configuration) throws Exception {
        return startJetty(configuration, DEFAULT_PATH, DEFAULT_PORT_NUMBER);
    }

    /**
     * Starts Jetty server on the port
     * and deploys the configuration on path.
     * <p>
     * <strong>WARNING:</strong> The Web resource directory is not defined.
     * Don't use this if you have anything in the web resource
     * directory what you want to use.
     * </p>
     *
     * @param configuration The spring {@link Configuration configuration} class. (NonNull)
     * @param contextPath The web application context path. (NonNull)
     * @param port The port number. (NonNull)
     * @return The Jetty server. (NonNull)
     * @throws Exception If Jetty server start or deploy fails.
     */
    public static Server startJetty(final Class<?> configuration, final String contextPath, final int port) throws Exception {
        Server jettyServer = null;
        try {
            jettyServer = new Server();

            final SocketConnector conn = new SocketConnector();
            conn.setPort(port);
            jettyServer.setConnectors(new Connector[] {conn });

            final Context context = new Context(jettyServer, contextPath);

            final ServletHolder servletHolder = new ServletHolder(org.springframework.web.servlet.DispatcherServlet.class);

            servletHolder.setInitParameter("contextClass", "org.springframework.web.context.support.AnnotationConfigWebApplicationContext");
            servletHolder.setInitParameter("contextConfigLocation", configuration.getName());

            context.addServlet(servletHolder, "/");

            jettyServer.setHandler(context);
            jettyServer.start();
            LOG.info("Started: " + jettyServer.isStarted());
            return jettyServer;
        } catch (final Exception ex) {
            stopJetty(jettyServer);
            throw ex;
        }

    }

    //servletContextXml: /WEB-INF/spring/appServlet/servlet-context.xml
    //rootContextXml: /WEB-INF/spring/root-context.xml
    /**
     * Start a Jetty server and deploys the spring MVC application
     * defined in servletContextXml and rootContextXml.<br>
     * <p>
     * <strong>WARNING:</strong> The Web resource directory is not defined.
     * Don't use this if you have anything in the web resource
     * directory what you want to use.
     * </p>
     *
     * @param servletContextXml The servlet context spring xml. (NonNull)
     * @param rootContextXml The root application context spring xml. (NonNull)
     * @param contextPath The context path where the application will run. (NonNull)
     * @param port The port number. (NonNull)
     * @return The Jetty server. (NonNull)
     * @throws Exception If Jetty server start or deploy fails.
     */
    public static Server startJetty(final String servletContextXml, final String rootContextXml, final String contextPath, final int port) throws Exception {
        Server jettyServer = null;
        try {
            jettyServer = new Server();

            final SocketConnector conn = new SocketConnector();
            conn.setPort(port);
            jettyServer.setConnectors(new Connector[] {conn });

            final Context context = new Context(jettyServer, contextPath);

            final Map<String, String> initParams = new HashMap<String, String>();
            initParams.put("contextConfigLocation", rootContextXml);

            context.setInitParams(initParams);

            context.addEventListener(new org.springframework.web.context.ContextLoaderListener());

            final ServletHolder servletHolder = new ServletHolder(org.springframework.web.servlet.DispatcherServlet.class);
            servletHolder.setInitParameter("contextConfigLocation", servletContextXml);
            context.addServlet(servletHolder, "/");

            jettyServer.setHandler(context);
            jettyServer.start();
            LOG.info("Started: " + jettyServer.isStarted());
            return jettyServer;
        } catch (final Exception ex) {
            stopJetty(jettyServer);
            throw ex;
        }
    }

    /**
     * Starts a Jetty server and deploys the web application defined
     * in the given web resource directory.<br>
     * The <tt>WEB-INF/web.xml</tt> will be process during the deployment.
     *
     * @param webDirPath The web resource directory. (NonNull)
     * @param contextPath The context path where the application will run. (NonNull)
     * @param port The port number. (NonNull)
     * @return The Jetty server. (NonNull)
     * @throws Exception If Jetty server start or deploy fails.
     */
    public static Server startJetty(final String webDirPath, final String contextPath, final int port) throws Exception {
        Server jettyServer = null;
        try {
            jettyServer = new Server();

            final SocketConnector conn = new SocketConnector();
            conn.setPort(port);
            jettyServer.setConnectors(new Connector[] {conn });

            final WebAppContext brokerAppContext = new WebAppContext();
            brokerAppContext.setContextPath(contextPath);
            brokerAppContext.setWar(webDirPath);

            jettyServer.setHandler(brokerAppContext);

            jettyServer.start();
            LOG.info("Started: " + jettyServer.isStarted());
            return jettyServer;
        } catch (final Exception ex) {
            stopJetty(jettyServer);
            throw ex;
        }
    }

    /**
     * Stops the given jettyServer if it exists.
     *
     * @param jettyServer The jetty server. (Nullable)
     */
    public static void stopJetty(final Server jettyServer) {
        if (jettyServer != null) {
            try {
                jettyServer.stop();
                LOG.info("Jetty stopped");
            } catch (final Exception e1) {
                LOG.info("Exception occurred during stopping jetty.", e1);
            }
        }
    }

    /** Hidden constructor of utility class. */
    private JettyRunner() { /* NOP */ }

}
