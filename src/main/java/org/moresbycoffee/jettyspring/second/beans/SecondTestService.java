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
package org.moresbycoffee.jettyspring.second.beans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.moresbycoffee.jettyspring.second.dal.IFirstEntityDao;
import org.moresbycoffee.jettyspring.second.domain.FirstEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


/**
 * TODO javadoc.
 *
 * @author Barnabas Sudy (barnabas.sudy@gmail.com)
 * @since 2012
 */
@Component
@Transactional
public class SecondTestService implements ISecondTestService {


    private final IFirstEntityDao firstEntityDao;


    /**
     * @param firstEntityDao Data Access Object to use the persistence layer.
     */
    @Autowired
    public SecondTestService(final IFirstEntityDao firstEntityDao) {
        super();
        this.firstEntityDao = firstEntityDao;
    }

    /** {@inheritDoc} */
    @Override
    public void addNewEntity(final String name) {
        firstEntityDao.persist(new FirstEntity(name));
    }

    /** {@inheritDoc} */
    @Override
    public Collection<String> listNames() {
        final List<FirstEntity> entities = firstEntityDao.getAll();
        final List<String> names = new ArrayList<String>(entities.size());
        for (final FirstEntity entity : entities) {
            names.add(entity.getName());
        }

        return names;
    }

}
