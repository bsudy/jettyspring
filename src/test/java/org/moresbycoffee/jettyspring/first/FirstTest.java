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
package org.moresbycoffee.jettyspring.first;

import java.io.IOException;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.moresbycoffee.jettyspring.JettyRunner;
import org.moresbycoffee.jettyspring.RestTestUtil;
import org.moresbycoffee.jettyspring.first.beans.FirstBean;
import org.moresbycoffee.jettyspring.first.spring.FirstTestConfiguration;


/**
 * <p><strong>Test description:</strong> This test shows how an Spring MVC
 * application can be deployed into Jetty.</p>
 *
 * <p>The test deploys the Spring MVC application using the {@link FirstTestConfiguration}.
 * The Jetty start and deploy process is implemented in the {@link JettyRunner} because is
 * a common functionality what can be shared between tests.</p>
 *
 * @author Barnabas Sudy (barnabas.sudy@gmail.com)
 * @since 2012
 */
public class FirstTest {

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
        jettyServer = JettyRunner.startJetty(FirstTestConfiguration.class);
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
     * Tests the {@link org.moresbycoffee.jettyspring.second.spring.SecondTestRestController#add(String) SecondTestRestController#add(String)} RESTful WS Service point.
     *
     * @throws IOException If communication error occurs
     */
    @Test
    public void testFirstBean() throws IOException {
        final String result = RestTestUtil.doGet("/firstBeanTest");

        LOG.info("Result:   " + result);
        LOG.info("Expected: " + FirstBean.TEST_STRING);
        Assert.assertTrue(FirstBean.TEST_STRING.equals(result));

    }


}
