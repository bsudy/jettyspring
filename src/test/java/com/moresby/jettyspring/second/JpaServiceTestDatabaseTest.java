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
package com.moresby.jettyspring.second;

import java.io.IOException;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.moresby.jettyspring.JettyRunner;
import com.moresby.jettyspring.RestTestUtil;
import com.moresby.jettyspring.first.FirstTest;
import com.moresby.jettyspring.second.spring.SecondTestConfiguration;
import com.moresby.jettyspring.second.spring.SecondTestRestController;

/**
 * <p><strong>Test description:</strong> This class shows how the configuration can be partly
 * replaced. I'm replacing only the {@link EntityManagerFactory} but in a normal project you
 * can replace the properties file.<br>
 * In this case I've added a new <tt>test</tt> persistence unit to the persistence.xml, which is
 * also not good in production project, you have to use your properties file for all
 * database properties...<br>
 * The <tt>test</tt> database is a memory database, it is newly generated every time you run this
 * test class.
 * </p>
 * <p>We lost our prepared database which is not good. In the next test we will find a solution
 * for this problem.</p>
 * TODO Add the next test.
 *
 * <p>This test class starts a Jetty server and deploys the application.
 * The application connects to the <tt>"test"</tt> memory database.</p>
 * <p><strong>WARNING:</strong>Do not run the {@link #addTest()} test because it will add a
 * new line to the database and the {@link #listTest()} will fail.</p>
 *
 * @author Barnabas Sudy (barnabas.sudy@gmail.com)
 * @since 2012
 */
public class JpaServiceTestDatabaseTest {

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(FirstTest.class);


    /**
     * Test configuration. This class is overdefining the {@link SecondTestConfiguration}'s original
     * {@link EntityManagerFactory}. Therefore the test will be able to use the <tt>test</tt> persistence
     * unit instead of the <tt>default</tt> one.
     *
     * @author Barnabas Sudy (barnabas.sudy@gmail.com)
     * @since 2012
     */
    @Configuration
    @EnableWebMvc
    @ComponentScan(basePackageClasses = SecondTestRestController.class, basePackages = {"com.moresby.jettyspring.second.beans", "com.moresby.jettyspring.second.dal" })
    @EnableTransactionManagement
    public static class TestConfiguration extends SecondTestConfiguration {

        /** {@inheritDoc} */
        @Override
        public EntityManagerFactory entityManagerFactory() {
            return Persistence.createEntityManagerFactory("test");
        }

    }

    /** The static variable for the Jetty server. */
    private static Server jettyServer = null;

    /**
     * Starts the Jetty and deploys the application.<br>
     * <strong>WARNING:</strong> All the tests will use the same Jetty and application so
     * the test should not modify the state of the application.<br>
     *
     * @see BeforeClass
     * @throws Exception If error occurs during the jetty start or application deployment.
     */
    @BeforeClass
    public static void startJetty() throws Exception {
        jettyServer = JettyRunner.startJetty(TestConfiguration.class);
    }

    /**
     * Stops the Jetty server.
     *
     * @see AfterClass
     */
    @AfterClass
    public static void stopJetty() {
        JettyRunner.stopJetty(jettyServer);
    }

    /**
     * Tests the {@link com.moresby.jettyspring.second.spring.SecondTestRestController#add(String) SecondTestRestController#add(String)} RESTful WS Service point.
     *
     * <p><strong>WARNING:</strong>This test is ignored
     * because there is no guarantee order of the test running
     * and the {@link #listTest()} needs an empty database.</p>
     *
     * @throws IOException If communication error occurs
     */
    @Test
    @Ignore
    public final void addTest() throws IOException {
        final String result = RestTestUtil.doGet("/add?name=testEntity");

        LOG.info("Result:   " + result);
        LOG.info("Expected: " + "Done!");
        Assert.assertTrue("Done!".equals(result));

    }

    /**
     * Tests the {@link com.moresby.jettyspring.second.spring.SecondTestRestController#list() SecondTestRestController#list()} RESTful WS Service point.
     *
     * @throws IOException If communication error occurs
     */
    @Test
    public final void listTest() throws IOException {
        final String result = RestTestUtil.doGet("/list");

        LOG.info("Result:   " + result);
        Assert.assertTrue(result.isEmpty());
    }

}
