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
package com.moresby.jettyspring;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

/**
 * Utility class to generate {@link EntityManagerFactory}s without <tt>persistence.xml</tt>
 *
 * @author Barnabas Sudy (barnabas.sudy@gmail.com)
 * @since 2012
 */
public final class EntityManagerFactoryUtil {

    /**
     * Generates a {@link EntityManagerFactory} to a H2 memory database without <tt>peristence.xml</tt>.
     *
     * @param databaseName The name of the database. (NonNull)
     * @param packagesToScan The name of packages to scan for entities. (NonNull)
     * @return The generated factory.
     */
    public static EntityManagerFactory createH2MemoryEntityManager(final String databaseName, final String... packagesToScan) {
        final Map<String, String> jpaProperties = new HashMap<String, String>();
        jpaProperties.put("hibernate.bytecode.use_reflection_optimizer", "false");
        jpaProperties.put("hibernate.archive.autodetection",             "class, hbm");
        jpaProperties.put("hibernate.hbm2ddl.auto",                      "update");
        jpaProperties.put("hibernate.connection.driver_class",           org.h2.Driver.class.getName());
        jpaProperties.put("hibernate.connection.url",                    "jdbc:h2:mem:" + databaseName + ";LOCK_MODE=1");
        jpaProperties.put("hibernate.dialect",                           org.hibernate.dialect.H2Dialect.class.getName());
        jpaProperties.put("hibernate.connection.password",               "admin");
        jpaProperties.put("hibernate.connection.username",               "admin");
        jpaProperties.put("hibernate.show_sql",                          "false");

        final LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setPersistenceProviderClass(org.hibernate.ejb.HibernatePersistence.class);
        entityManagerFactoryBean.setJpaPropertyMap(jpaProperties);
        entityManagerFactoryBean.setPackagesToScan(packagesToScan);
        entityManagerFactoryBean.setPersistenceUnitName(databaseName);
//        Persistence.getPersistenceUtil().
        entityManagerFactoryBean.afterPropertiesSet();

        return entityManagerFactoryBean.getObject();
    }

    /** Hidden constructor of utility class. */
    private EntityManagerFactoryUtil() { /* NOP */ }
}
