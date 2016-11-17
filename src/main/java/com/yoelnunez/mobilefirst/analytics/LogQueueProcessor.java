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

import com.yoelnunez.mobilefirst.analytics.util.AppContext;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LogQueueProcessor {
    private static LogQueueProcessor instance;

    static synchronized LogQueueProcessor getInstance(AppContext appContext, TransportClient transportClient) {
        if (instance == null) {
            instance = new LogQueueProcessor(appContext, transportClient);
        }

        return instance;
    }

    private static final int QUEUE_SIZE = 200;

    private static final Map<AppContext, JSONArray> logs = new HashMap<AppContext, JSONArray>();
    private static int counter = 0;

    private AppContext appContext;
    private TransportClient transportClient;

    private LogQueueProcessor(AppContext appContext, TransportClient transportClient) {
        this.appContext = appContext;
        this.transportClient = transportClient;
    }

    public JSONArray getLogs() {
        synchronized (logs) {
            return logs.get(appContext);
        }
    }

    public void pushLog(JSONObject log) {
        synchronized (logs) {
            if (!logs.containsKey(appContext)) {
                logs.put(appContext, new JSONArray());
            }


            logs.get(appContext).put(log);
        }
        counter++;
    }

    public void clear() {
        synchronized (logs) {
            logs.clear();
            counter = 0;
        }
    }


    public void send() throws IOException {
        if (counter >= QUEUE_SIZE) {

            synchronized (logs) {
                JSONArray payload = new JSONArray();

                for (Map.Entry<AppContext, JSONArray> set : logs.entrySet()) {
                    JSONObject doc = new JSONObject();
                    doc.put("worklight_data", set.getKey().toJSON());
                    doc.put("_type", "RawMfpAppLogs");
                    doc.put("client_logs", set.getValue());

                    payload.put(doc);
                }

                clear();

                transportClient.send(payload.toString().getBytes());
            }

        }
    }
}
