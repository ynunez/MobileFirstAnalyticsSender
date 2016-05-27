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
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class AnalyticsAPI {

    private static ServerContext serverContext = null;
    private static HttpClient httpClient = null;
    private static Map<JSONObject, JSONArray> logs;

    public static AnalyticsAPI createInstance(AppContext context) throws MissingServerContextException {
        if(serverContext == null) {
            throw new MissingServerContextException("Server context missing, analytics reporting will fail.");
        }

        if(httpClient == null) {
            HttpClientBuilder builder = HttpClientBuilder.create();

            httpClient = builder.build();
        }

        logs = new HashMap<JSONObject, JSONArray>();

        return new AnalyticsAPI(context);
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

    private JSONArray instanceLogs = new JSONArray();
    private JSONObject appMetadata = new JSONObject();

    private AnalyticsAPI(AppContext context) {
        loadAppMetaData(context);

        if(logs.get(appMetadata) == null) {
            logs.put(appMetadata, instanceLogs);
        } else {
            instanceLogs = logs.get(appMetadata);
        }
    }

    private void loadAppMetaData(AppContext context) {
        appMetadata.put("appName", context.getAppName());
        appMetadata.put("appVersion", context.getAppVersion());

        appMetadata.put("systemName", context.getDeviceOS());
        appMetadata.put("model", context.getDeviceModel());
        appMetadata.put("systemVersion", context.getDeviceOSVersion());
        appMetadata.put("deviceId", context.getDeviceID());
    }

    public JSONObject getAppMetadata() {
        return appMetadata;
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

        instanceLogs.put(logObject);
    }

    public JSONArray getLogs() {
        return instanceLogs;
    }

    public static void send() {
        JSONArray payload = new JSONArray();

        for(Map.Entry<JSONObject, JSONArray> set : logs.entrySet()) {
            JSONObject doc = new JSONObject();
            doc.put("worklight_data", set.getKey());
            doc.put("_type", "RawMfpAppLogs");
            doc.put("client_logs", set.getValue());

            payload.put(doc);
        }

        String credentials = serverContext.getUsername() + ":" + serverContext.getPassword();

        HttpPost post = new HttpPost(serverContext.getEndpoint());
        post.addHeader("Content-Type","application/json");
        post.addHeader("Authorization", "Basic " + new String(Base64.encodeBase64(credentials.getBytes())));

        HttpEntity entity = new ByteArrayEntity(payload.toString().getBytes());
        post.setEntity(entity);

        try {
            httpClient.execute(post);

            logs.clear();
        } catch (IOException ignored) {}
    }
}
