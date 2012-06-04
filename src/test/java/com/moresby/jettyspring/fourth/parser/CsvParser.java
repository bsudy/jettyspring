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
package com.moresby.jettyspring.fourth.parser;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import au.com.bytecode.opencsv.CSVReader;

import com.moresby.jettyspring.fourth.domain.Line;
import com.moresby.jettyspring.fourth.domain.Station;
import com.moresby.jettyspring.fourth.domain.Zone;
import com.moresby.jettyspring.util.converter.Converters;

/**
 * Utility class to parse a Csv file and store the content in database.
 *
 * @author Barnabas Sudy (barnabas.sudy@gmail.com)
 * @since 2012
 */
public final class CsvParser {

    /** Hidden constructor of Utility class. */
    private CsvParser() { /* NOP */ }

    /**
     * TODO javadoc.
     *
     * @param reader The csv content reader.
     * @param entityManager The entitymanager to reach the persistence context.
     * @throws IOException If error occurs during csv reading.
     */
    public static void parseCsv(final Reader reader, final EntityManager entityManager) throws IOException {

        final CSVReader csvReader = new CSVReader(reader);

        final Map<Integer, Zone> zones = new HashMap<Integer, Zone>();
        final Map<String, Line>  lines = new HashMap<String, Line>();

        String [] nextLine;
        if (csvReader.readNext() == null) {
            throw new IOException("There is not enough data in the csv file");
        }
        while ((nextLine = csvReader.readNext()) != null) {

            final String  stationName = nextLine[0];
            final String  lineNames   = nextLine[1];
            final Integer zoneNumber  = Integer.valueOf(nextLine[2]);

            final String[] lineNameArray = Converters.<String, String>convertArray(lineNames.split(","), Converters.TRIM, String.class);

            /* Find or create zone */
            final Zone zone;
            if (zones.containsKey(zoneNumber)) {
                zone = zones.get(zoneNumber);
            } else {
                zone = new Zone(zoneNumber);
                entityManager.persist(zone);
            }

            /* Create station */
            final Station station = new Station(stationName, zone);
            entityManager.persist(station);
            zone.getStations().add(station);

            /* Assign to lines */
            for (final String lineName : lineNameArray) {
                final Line line;
                if (lines.containsKey(lineName)) {
                    line = lines.get(lineName);
                } else {
                    line = new Line(lineName);
                    entityManager.persist(line);
                }

                station.getLines().add(line);
                line.getStations().add(station);


            }


        }

    }

}
