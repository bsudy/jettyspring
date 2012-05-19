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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Assert;

/**
 * TODO javadoc.
 *
 * @author Barnabas Sudy (barnabas.sudy@gmail.com)
 * @since 2012
 */
public final class RestTestUtil {


    public static String doGet(final String requestPath) throws IOException {
        return doGet(requestPath, HttpURLConnection.HTTP_OK);
    }

    public static String doGet(final String requestPath, final int expectedResponseCode) throws IOException {
        return doRequest(JettyRunner.DEFAULT_PORT_NUMBER, JettyRunner.DEFAULT_PATH, requestPath, "GET", null, expectedResponseCode);
    }

    public static String doPost(final String requestPath, final String output) throws IOException {
        return doPost(requestPath, output, HttpURLConnection.HTTP_OK);
    }

    public static String doPost(final String requestPath, final String output, final int expectedResponseCode) throws IOException {
        return doRequest(JettyRunner.DEFAULT_PORT_NUMBER, JettyRunner.DEFAULT_PATH, requestPath, "POST", output, expectedResponseCode);
    }


    private static String doRequest(final int port, final String contextPath, final String requestPath, final String method, final String output, final int expectedResponseCode) throws IOException {

        final URL serverAddress = new URL("http://localhost:" + port + contextPath + requestPath);

        final HttpURLConnection connection = (HttpURLConnection)serverAddress.openConnection();
        connection.setRequestMethod(method);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setReadTimeout(10000);

        connection.connect();

        try {
            if (output != null) {
                final Writer writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write("");
                writer.flush();
            }

            final int responseCode = connection.getResponseCode();

            /* Response code assertion */
            Assert.assertEquals(expectedResponseCode, responseCode);


            final BufferedInputStream   bis = new BufferedInputStream(connection.getInputStream());
            final ByteArrayOutputStream baf = new ByteArrayOutputStream();
            int read = 0;
            final int bufSize = 1024;
            final byte[] buffer = new byte[bufSize];
            while(true){
                 read = bis.read(buffer);
                 if(read==-1){
                      break;
                 }
                 baf.write(buffer, 0, read);
            }

            return baf.toString();
        } finally {
            connection.disconnect();
        }

    }

    /** Hidden constructor of utility class. */
    private RestTestUtil() { /* NOP */ }
}
