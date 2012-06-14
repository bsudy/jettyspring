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
package org.moresbycoffee.jettyspring.third;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.moresbycoffee.jettyspring.JettyRunner;
import org.moresbycoffee.jettyspring.RestTestUtil;
import org.moresbycoffee.jettyspring.second.domain.FirstEntity;
import org.moresbycoffee.jettyspring.second.spring.SecondTestConfiguration;
import org.moresbycoffee.jettyspring.second.spring.SecondTestRestController;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


/**
 * In this test I'll show how content can be generated into the <tt>test</tt>
 * database. I'll also show how a new database can be defined without persistence xml.
 *
 * @author Barnabas Sudy (barnabas.sudy@gmail.com)
 * @since 2012
 */
public class PrepareDatabaseTest {

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(PrepareDatabaseTest.class);

    private static EntityManagerFactory emf;

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
    @ComponentScan(basePackageClasses = SecondTestRestController.class, basePackages = {"org.moresbycoffee.jettyspring.second.beans", "org.moresbycoffee.jettyspring.second.dal" })
    @EnableTransactionManagement
    public static class TestConfiguration extends SecondTestConfiguration {

        /** {@inheritDoc} */
        @Override
        public EntityManagerFactory entityManagerFactory() {
            return emf;
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
        final Map<String, String> jpaProperties = new HashMap<String, String>();
        jpaProperties.put("hibernate.bytecode.use_reflection_optimizer", "false");
        jpaProperties.put("hibernate.archive.autodetection",             "class, hbm");
        jpaProperties.put("hibernate.hbm2ddl.auto",                      "update");
        jpaProperties.put("hibernate.connection.driver_class",           org.h2.Driver.class.getName());
        jpaProperties.put("hibernate.connection.url",                    "jdbc:h2:mem:jettyspring2;LOCK_MODE=1");
        jpaProperties.put("hibernate.dialect",                           org.hibernate.dialect.H2Dialect.class.getName());
        jpaProperties.put("hibernate.connection.password",               "admin");
        jpaProperties.put("hibernate.connection.username",               "admin");
        jpaProperties.put("hibernate.show_sql",                          "false");

        final LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setPersistenceProviderClass(org.hibernate.ejb.HibernatePersistence.class);
        entityManagerFactoryBean.setJpaPropertyMap(jpaProperties);
        entityManagerFactoryBean.setPackagesToScan("org.moresbycoffee.jettyspring.second.domain");
        entityManagerFactoryBean.setPersistenceUnitName("testUnit");
//        Persistence.getPersistenceUtil().
        entityManagerFactoryBean.afterPropertiesSet();

        emf = entityManagerFactoryBean.getObject();
//        emf = Persistence.createEntityManagerFactory("testUnit", properties);

//        emf = Persistence.createEntityManagerFactory("test");
        final EntityManager entityManager = emf.createEntityManager();
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.persist(new FirstEntity("Prepare Database Test 1."));
        transaction.commit();


        jettyServer = JettyRunner.startJetty(TestConfiguration.class);
    }

    /**
     * Stops the Jetty server and closes the {@link EntityManagerFactory} if it is still open.
     *
     * @see AfterClass
     */
    @AfterClass
    public static void stopJetty() {
        JettyRunner.stopJetty(jettyServer);
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    /**
     * Tests the {@link org.moresbycoffee.jettyspring.second.spring.SecondTestRestController#add(String) SecondTestRestController#add(String)} RESTful WS Service point.
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
     * Tests the {@link org.moresbycoffee.jettyspring.second.spring.SecondTestRestController#list() SecondTestRestController#list()} RESTful WS Service point.
     *
     * @throws IOException If communication error occurs
     */
    @Test
    public final void listTest() throws IOException {
        final String result = RestTestUtil.doGet("/list");

        LOG.info("Result:   " + result);
        Assert.assertEquals("Prepare Database Test 1.", result);
    }

}
