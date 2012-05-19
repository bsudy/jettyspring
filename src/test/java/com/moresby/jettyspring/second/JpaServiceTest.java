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

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mortbay.jetty.Server;

import com.moresby.jettyspring.JettyRunner;
import com.moresby.jettyspring.RestTestUtil;
import com.moresby.jettyspring.first.FirstTest;
import com.moresby.jettyspring.second.spring.SecondTestConfiguration;

/**
 * <p><strong>Test description:</strong> This class tests the application with the default database.
 * In a normal project it's obviously not good therefore in the next test
 * {@link JpaServiceTestDatabaseTest} we will replace it.</p>
 *
 * <p>This test class starts a Jetty server and deploys the application.
 * The application connects to the "default" database. The default database
 * is a h2 database in the jettyspring.h2.db file.
 * The database is prepared, a "TestDBEntity" entity has been added to it.</p>
 * <p><strong>WARNING:</strong>Do not run the {@link #addTest()} test because it will add a
 * new line to the database and the {@link #listTest()} will fail.</p>
 *
 * @author Barnabas Sudy (barnabas.sudy@gmail.com)
 * @since 2012
 */
public class JpaServiceTest {

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(FirstTest.class);

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
        jettyServer = JettyRunner.startJetty(SecondTestConfiguration.class);
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
     * and the {@link #listTest()} needs the prepared database.</p>
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
        Assert.assertTrue(result.equals("TestDBEntity"));
    }

}
