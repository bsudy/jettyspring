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
package org.moresbycoffee.jettyspring.fourth;

import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.io.InputStreamReader;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.moresbycoffee.jettyspring.EntityManagerFactoryUtil;
import org.moresbycoffee.jettyspring.JettyRunner;
import org.moresbycoffee.jettyspring.RestTestUtil;
import org.moresbycoffee.jettyspring.fourth.parser.CsvParser;
import org.moresbycoffee.jettyspring.fourth.spring.FourthTestConfiguration;
import org.moresbycoffee.jettyspring.fourth.spring.FourthTestRestController;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.jayway.jsonassert.JsonAssert;


/**
 * <p>I'll show in this test how the long database generation code eliminated and moved into CSV.
 * There are three ways to do it, the first uses a csv parser which can take a csv file and
 * can create the relating entities. The second uses a built in csv parser like H2 db-s
 * (<a href="http://www.h2database.com/html/tutorial.html#csv">http://www.h2database.com/html/tutorial.html#csv</a>).
 * And last but not least (what I never tried) to use a csv based database driver:
 * <a href="http://csvjdbc.sourceforge.net/">http://csvjdbc.sourceforge.net/</a>.</p>
 * <p>In this case I'll use the first solution, so I'll write a {@link CsvParser} which can
 * take a csv file.</p>
 *
 * @author Barnabas Sudy (barnabas.sudy@gmail.com)
 * @since 2012
 */
public class PrepareDatabaseByCSVTest {

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(PrepareDatabaseByCSVTest.class);

    /**
     * Test configuration. This class is overdefining the {@link FourthTestConfiguration}'s original
     * {@link EntityManagerFactory}. Therefore the test will be able to use the a generated memory based
     * persistence unit instead of the a real one.
     *
     * @author Barnabas Sudy (barnabas.sudy@gmail.com)
     * @since 2012
     */
    @Configuration
    @EnableWebMvc
    @ComponentScan(basePackageClasses = FourthTestRestController.class, basePackages = {"org.moresbycoffee.jettyspring.fourth.beans", "org.moresbycoffee.jettyspring.fourth.dal" })
    @EnableTransactionManagement
    public static class TestConfiguration extends FourthTestConfiguration {

        /** {@inheritDoc} */
        @Override
        public EntityManagerFactory entityManagerFactory() {
            return emf;
        }

    }

    /** The entity manager factory to create the database and then use it in the test. */
    private static EntityManagerFactory emf;

    /** The static variable for the Jetty server. */
    private static Server jettyServer = null;

    /**
     * Starts the Jetty and deploys the application.<br>
     * <strong>WARNING:</strong> All the tests of the class will use the same Jetty and application so
     * the test should not modify the state of the application.<br>
     *
     * @see BeforeClass
     * @throws Exception If error occurs during the jetty start or application deployment.
     */
    @BeforeClass
    public static void startJetty() throws Exception {


        emf = EntityManagerFactoryUtil.createH2MemoryEntityManager("perpMultiTableCSVTest", "org.moresbycoffee.jettyspring.fourth.domain");

        final EntityManager entityManager = emf.createEntityManager();

        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        CsvParser.parseCsv(new InputStreamReader(PrepareDatabaseByCSVTest.class.getClassLoader().getResourceAsStream("london.csv")), entityManager);

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
     * Tests the {@link org.moresbycoffee.jettyspring.fourth.spring.FourthTestRestController#listStations() FourthTestRestController#listStations()} RESTful WS Service point.
     *
     * @throws IOException If communication error occurs
     */
    @Test
    public final void listStationTest() throws IOException {
        final String result = RestTestUtil.doGet("/listStations");

        LOG.info("Result:   " + result);

        JsonAssert.with(result).assertThat("$..name", hasItems("London Bridge", "Bermondsey", "Canada Water"));
    }


}
