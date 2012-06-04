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
package com.moresby.jettyspring.fourth.util;

import com.moresby.jettyspring.fourth.beans.dto.StationDTO;
import com.moresby.jettyspring.fourth.domain.Station;
import com.moresby.jettyspring.util.converter.Converter;
import com.moresby.jettyspring.util.converter.ConverterException;

/**
 * Converts a {@link Station} database entity to a {@link StationDTO} data transfer object.<br />
 * This is a singleton object, use the {@link #getInstance()}.
 *
 * @author Barnabas Sudy (barnabas.sudy@gmail.com)
 * @since 2012
 */
public final class StationToDtoConverter implements Converter<Station, StationDTO> {

    /** The singleton instance. */
    private static final StationToDtoConverter INSTANCE = new StationToDtoConverter();

    /** Hidden constructor of a Singleton object. */
    private StationToDtoConverter() { /* NOP */ }

    /** {@inheritDoc} */
    @Override
    public StationDTO convert(final Station from) throws ConverterException {
        if (from == null) {
            return null;
        }

        final StationDTO dto = new StationDTO(from.getId(), from.getName());
        return dto;
    }

    /**
     * @return The singleton instance.
     */
    public static StationToDtoConverter getInstance() {
        return INSTANCE;
    }

}
