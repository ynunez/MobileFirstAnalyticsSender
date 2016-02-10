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
package com.yoelnunez.mobilefirst.analytics.util;

public class ServerContext {

    private String endpoint;
    private String username;
    private String password;

    public ServerContext() {
    }

    public ServerContext(String endpoint, String username, String password) {
        setEndpoint(endpoint);
        setUsername(username);
        setPassword(password);
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
