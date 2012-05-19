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
 * Utility class consisting of helper methods to test RESTful
 * web services.<br>
 * The class provides a couple of utility methods to create
 * HTTP requests, checks the response code and returns the
 * content.<br>
 * This class uses the {@link JettyRunner}'s constants as default values.
 *
 * @author Barnabas Sudy (barnabas.sudy@gmail.com)
 * @since 2012
 */
public final class RestTestUtil {

    /** The timeout after the request fails. */
    private static final int TIMEOUT = 10000;

    /**
     * Sends a <tt>GET</tt> request to service accessed on <tt>requestPath</tt>.<br>
     * The context path is set from the {@link JettyRunner#DEFAULT_PATH}.<br>
     * The port number is also picked up from {@link JettyRunner#DEFAULT_PORT_NUMBER}.<br>
     *
     * @param requestPath The service path. (NonNull)
     * @return The response body. (NonNull)
     * @throws IOException If communication error occurs.
     * @throws AssertionError If the request fails.
     */
    public static String doGet(final String requestPath) throws IOException {
        return doGet(requestPath, HttpURLConnection.HTTP_OK);
    }

    /**
     * Sends a <tt>GET</tt> request to service accessed on <tt>requestPath</tt>.<br>
     * The context path is set from the {@link JettyRunner#DEFAULT_PATH}.<br>
     * The port number is also picked up from {@link JettyRunner#DEFAULT_PORT_NUMBER}.<br>
     *
     * @param requestPath The service path. (NonNull)
     * @param expectedResponseCode The expected response code. (NonNull)
     * @return The response body. (NonNull)
     * @throws IOException If communication error occurs.
     * @throws AssertionError If the response code is not match to the expcetedResponseCode.
     */
    public static String doGet(final String requestPath, final int expectedResponseCode) throws IOException  {
        return doRequest(JettyRunner.DEFAULT_PORT_NUMBER, JettyRunner.DEFAULT_PATH, requestPath, "GET", null, expectedResponseCode);
    }

    /**
     * Sends a <tt>POST</tt> request to service accessed on <tt>requestPath</tt> using the output
     * as the body content.<br>
     * The context path is set from the {@link JettyRunner#DEFAULT_PATH}.<br>
     * The port number is also picked up from {@link JettyRunner#DEFAULT_PORT_NUMBER}.<br>
     *
     * @param requestPath The service path. (NonNull)
     * @param output The request body content. (Nullable)
     * @return The response body. (NonNull)
     * @throws IOException If communication error occurs.
     * @throws AssertionError If the request fails.
     */
    public static String doPost(final String requestPath, final String output) throws IOException {
        return doPost(requestPath, output, HttpURLConnection.HTTP_OK);
    }

    /**
     * Sends a <tt>POST</tt> request to service accessed on <tt>requestPath</tt> using the output
     * as the body content.<br>
     * The context path is set from the {@link JettyRunner#DEFAULT_PATH}.<br>
     * The port number is also picked up from {@link JettyRunner#DEFAULT_PORT_NUMBER}.<br>
     *
     * @param requestPath The service path. (NonNull)
     * @param output The request body content. (Nullable)
     * @param expectedResponseCode The expected response code. (NonNull)
     * @return The response body. (NonNull)
     * @throws IOException If communication error occurs.
     * @throws AssertionError If the response code is not match to the expcetedResponseCode.
     */
    public static String doPost(final String requestPath, final String output, final int expectedResponseCode) throws IOException {
        return doRequest(JettyRunner.DEFAULT_PORT_NUMBER, JettyRunner.DEFAULT_PATH, requestPath, "POST", output, expectedResponseCode);
    }


    /**
     * Sends a request to service accessed on <tt>requestPath</tt> using the output
     * as the body content.<br>
     *
     * @param port The port number. (NonNull)
     * @param contextPath The context path. (NonNull)
     * @param requestPath The service path. (NonNull)
     * @param method The method. (NonNull)
     * @param output The request body content. (Nullable)
     * @param expectedResponseCode The expected response code. (NonNull)
     * @return The response body. (NonNull)
     * @throws IOException If communication error occurs.
     * @throws AssertionError If the response code is not match to the expcetedResponseCode.
     */
    public static String doRequest(final int port, final String contextPath, final String requestPath, final String method, final String output, final int expectedResponseCode) throws IOException {

        final URL serverAddress = new URL("http://localhost:" + port + contextPath + requestPath);

        final HttpURLConnection connection = (HttpURLConnection) serverAddress.openConnection();
        connection.setRequestMethod(method);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setReadTimeout(TIMEOUT);

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
            while (true) {
                 read = bis.read(buffer);
                 if (read == -1) {
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
