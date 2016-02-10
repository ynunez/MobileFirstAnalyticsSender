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


import org.junit.Assert;
import org.junit.Test;

public class ServerContextTest {

    @Test
    public void testConstructor(){

        ServerContext config = new ServerContext("http://yourhost/analytics/v2", "demo", "password");

        Assert.assertTrue(config.getEndpoint().equals("http://yourhost/analytics/v2"));
        Assert.assertTrue(config.getUsername().equals("demo"));
        Assert.assertTrue(config.getPassword().equals("password"));
    }

    @Test
    public void testSetEndpoint(){

        ServerContext config = new ServerContext();

        config.setEndpoint("http://yourhost/analytics/v2");

        Assert.assertTrue(config.getEndpoint().equals("http://yourhost/analytics/v2"));
    }

    @Test
    public void testSetUsername(){

        ServerContext config = new ServerContext();
        config.setUsername("demo");

        Assert.assertTrue(config.getUsername().equals("demo"));
    }

    @Test
    public void testSetPassword(){

        ServerContext config = new ServerContext();
        config.setPassword("password");

        Assert.assertTrue(config.getPassword().equals("password"));
    }
}
