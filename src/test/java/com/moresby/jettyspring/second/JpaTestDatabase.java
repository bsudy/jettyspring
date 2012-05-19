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
 * TODO javadoc.
 *
 * @author Barnabas Sudy (barnabas.sudy@gmail.com)
 * @since 2012
 */
public class JpaTestDatabase {

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(FirstTest.class);


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

    private static Server jettyServer = null;

    /**
     * @see BeforeClass
     * @throws Exception If error occurs during the jetty start or application deployment.
     */
    @BeforeClass
    public static void startJetty() throws Exception {
        jettyServer = JettyRunner.startJetty(TestConfiguration.class);
    }

    /** @see AfterClass */
    @AfterClass
    public static void stopJetty() {
        JettyRunner.stopJetty(jettyServer);
    }

    @Test
    public final void listTest() throws IOException {
        final String result = RestTestUtil.doGet("/list");

        LOG.info("Result:   " + result);
        Assert.assertTrue(result.isEmpty());
    }

}
