/**
 * Copyright 2016 Yoel Nunez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yoelnunez.mobilefirst.analytics;

import com.yoelnunez.mobilefirst.analytics.exceptions.MissingServerContextException;
import com.yoelnunez.mobilefirst.analytics.util.AppContext;

import com.yoelnunez.mobilefirst.analytics.util.ServerContext;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class AnalyticsAPI {

    private static ServerContext serverContext = null;
    private static HttpClient httpClient = null;
    private static LogQueueProcessor logQueueProcessor;

    public static AnalyticsAPI createInstance(AppContext context, boolean clear) throws MissingServerContextException {
        if (serverContext == null) {
            throw new MissingServerContextException("Server context missing, analytics reporting will fail.");
        }

        if (httpClient == null) {
            HttpClientBuilder builder = HttpClientBuilder.create()
                    .setMaxConnTotal(10);

            httpClient = builder.build();
        }

        TransportClient transportClient = TransportClient.getInstance(serverContext);

        logQueueProcessor = LogQueueProcessor.getInstance(context, transportClient);

        if (clear) {
            logQueueProcessor.clear();
        }

        return new AnalyticsAPI();
    }

    public static AnalyticsAPI createInstance(AppContext context) throws MissingServerContextException {
        return createInstance(context, false);
    }

    public static void setContext(ServerContext context) {
        serverContext = context;
    }

    public static void setHttpClient(HttpClient client) {
        httpClient = client;
    }

    public static HttpClient getHttpClient() {
        return httpClient;
    }

    private AnalyticsAPI() {
    }

    public void log(String message, String metadata) {
        log(message, new JSONObject(metadata));
    }

    public void log(String message, JSONObject metadata) {
        JSONObject logObject = new JSONObject();
        logObject.put("msg", message);

        metadata.put("$src", "jar");
        metadata.put("$arguments", (new JSONArray()).put(message));


        logObject.put("metadata", metadata);

        logObject.put("level", "ANALYTICS");
        logObject.put("pkg", "wl.analytics");

        logObject.put("timestamp", System.currentTimeMillis());

        logQueueProcessor.pushLog(logObject);
    }

    public JSONArray getLogs() {
        return logQueueProcessor.getLogs();
    }

    public static void send() {
        try {
            logQueueProcessor.send();
        } catch (IOException ignored) {
        }
    }
}
