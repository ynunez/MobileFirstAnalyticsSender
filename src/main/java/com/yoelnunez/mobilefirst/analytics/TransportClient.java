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

import com.yoelnunez.mobilefirst.analytics.util.ServerContext;
import org.apache.commons.codec.binary.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TransportClient {

    private static TransportClient instance = null;

    static synchronized TransportClient getInstance(ServerContext serverContext) {
        if (instance == null) {
            instance = new TransportClient(serverContext);
        }

        return instance;
    }

    private HttpClient httpClient;
    private ServerContext serverContext;

    private PoolingHttpClientConnectionManager cm;

    private TransportClient(ServerContext context) {
        serverContext = context;

        cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(200);
        cm.setDefaultMaxPerRoute(20);


        httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .build();

        eventQueue = new ArrayBlockingQueue<Runnable>(100);
        threadPool = new ThreadPoolExecutor(4, 4, 0L, TimeUnit.MILLISECONDS, eventQueue);

    }

    private ArrayBlockingQueue<Runnable> eventQueue;
    private ThreadPoolExecutor threadPool;


    private void awaitEndpoint() {
        while (eventQueue.size() > 70) {
            try {
                cm.closeExpiredConnections();

                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
        }
    }


    void send(final byte[] payload) {
        awaitEndpoint();
        sendRequest(payload);
    }


    private void sendRequest(final byte[] payload) {
        threadPool.submit(new Runnable() {
            public void run() {
                String credentials = serverContext.getUsername() + ":" + serverContext.getPassword();

                HttpPost post = new HttpPost(serverContext.getEndpoint());
                post.addHeader("Content-Type", "application/json");
                post.addHeader("Authorization", "Basic " + new String(Base64.encodeBase64(credentials.getBytes())));

                post.setEntity(new ByteArrayEntity(payload));

                try {
                    httpClient.execute(post);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
