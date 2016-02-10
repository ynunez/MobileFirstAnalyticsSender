/**
 *  Copyright 2016 Yoel Nunez
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.yoelnunez.mobilefirst.analytics;

import com.yoelnunez.mobilefirst.analytics.exceptions.MissingServerContextException;
import com.yoelnunez.mobilefirst.analytics.util.AppContext;
import com.yoelnunez.mobilefirst.analytics.util.ServerContext;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AnalyticsAPITest {
    private ServerContext serverContext;
    private AppContext appContext;

    @Before
    public void init() {
        serverContext = new ServerContext("http://mobilefirst/analytics/v2", "demo", "password");

        appContext = new AppContext();
        appContext.setDeviceID("x123456d");
    }

    @Test(expected = MissingServerContextException.class)
    public void testInitMissingContext() throws MissingServerContextException {
        AnalyticsAPI.createInstance(appContext);
    }


    @Test
    public void testInitSettingContext() throws MissingServerContextException {
        AnalyticsAPI.setContext(serverContext);
        AnalyticsAPI api = AnalyticsAPI.createInstance(appContext);

        org.junit.Assert.assertNotNull("analytics api should be initialized", api);
    }

    @Test
    public void testLogCount() throws MissingServerContextException {
        AnalyticsAPI.setContext(serverContext);
        AnalyticsAPI api = AnalyticsAPI.createInstance(appContext);

        api.log("hello-world1", new JSONObject());
        api.log("hello-world2", new JSONObject());

        Assert.assertEquals("should contain 2 logs", api.getLogs().length(), 2);
    }

    @Test
    public void testLogMetadata() throws MissingServerContextException {
        AnalyticsAPI.setContext(serverContext);
        AnalyticsAPI api = AnalyticsAPI.createInstance(appContext);

        JSONObject log = new JSONObject();
        log.put("myCustomKey", "helloValueForKey");

        api.log("hello-world", log);

        JSONObject logDataObject = (JSONObject) api.getLogs().get(0);

        Assert.assertTrue("should contain timestamp", logDataObject.has("timestamp"));

        Assert.assertEquals("should have message \"hello-world\" ", logDataObject.optString("msg", null), "hello-world");
        Assert.assertEquals("should have level \"ANALYTICS\"", logDataObject.optString("level", null), "ANALYTICS");
        Assert.assertEquals("should have package \"wl.analytics\"", logDataObject.optString("pkg", null), "wl.analytics");

        JSONObject logMetadata = logDataObject.optJSONObject("metadata");

        Assert.assertNotNull("should contain logDataObject", logMetadata);
        Assert.assertEquals("metadata should contain custom key", logMetadata.optString("myCustomKey", null), "helloValueForKey");
        Assert.assertEquals("metadata should have $src \"jar\"", logMetadata.optString("$src", null), "jar");
        Assert.assertTrue("metadata should have $arguments array with a single element", logMetadata.optJSONArray("$arguments").length() == 1);
    }


    @Test
    public void testSettingHttpClient() throws MissingServerContextException {
        HttpClient httpClient = new HttpClientMock();
        AnalyticsAPI.setHttpClient(httpClient);

        Assert.assertEquals("should have http client", AnalyticsAPI.getHttpClient(), httpClient);
    }

    @Test
    public void testSettingDefaultHttpClient() throws MissingServerContextException {
        // reset http client
        AnalyticsAPI.setHttpClient(null);

        AnalyticsAPI.setContext(serverContext);
        AnalyticsAPI.createInstance(appContext);

        org.junit.Assert.assertNotNull("analytics api should be initialized", AnalyticsAPI.getHttpClient());
    }

    @Test
    public void testSendingLogs() throws MissingServerContextException {
        // reset http client
        AnalyticsAPI.setHttpClient(new HttpClientMock(){
            @Override
            public HttpResponse execute(HttpUriRequest request) throws IOException {

                HttpEntityEnclosingRequestBase requestBase = (HttpEntityEnclosingRequestBase)request;

                InputStream requestInputStream = requestBase.getEntity().getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(requestInputStream));
                StringBuilder body = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    body.append(line);
                }
                reader.close();



                JSONArray requestBody = new JSONArray(body.toString());

                Assert.assertEquals("should contain only one element", requestBody.length(), 1);

                JSONObject logPayload = requestBody.getJSONObject(0);

                Assert.assertEquals("should have log type \"RawMfpAppLogs\"", logPayload.get("_type"), "RawMfpAppLogs");

                Assert.assertTrue("should contain key \"worklight_data\"", logPayload.has("worklight_data"));

                Assert.assertEquals("should contain one log", logPayload.getJSONArray("client_logs").length(), 1);


                return null;
            }
        });

        AnalyticsAPI.setContext(serverContext);
        AnalyticsAPI api = AnalyticsAPI.createInstance(appContext);


        JSONObject log = new JSONObject();
        log.put("myCustomKey", "helloValueForKey");

        api.log("hello-world", log);

        AnalyticsAPI.send();
    }


    public static class HttpClientMock implements HttpClient {
        @Override
        public HttpParams getParams() {
            return null;
        }

        @Override
        public ClientConnectionManager getConnectionManager() {
            return null;
        }

        @Override
        public HttpResponse execute(HttpUriRequest request) throws IOException, ClientProtocolException {
            return null;
        }

        @Override
        public HttpResponse execute(HttpUriRequest request, HttpContext context) throws IOException, ClientProtocolException {
            return null;
        }

        @Override
        public HttpResponse execute(HttpHost target, HttpRequest request) throws IOException, ClientProtocolException {
            return null;
        }

        @Override
        public HttpResponse execute(HttpHost target, HttpRequest request, HttpContext context) throws IOException, ClientProtocolException {
            return null;
        }

        @Override
        public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
            return null;
        }

        @Override
        public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException, ClientProtocolException {
            return null;
        }

        @Override
        public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
            return null;
        }

        @Override
        public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException, ClientProtocolException {
            return null;
        }
    }
}
