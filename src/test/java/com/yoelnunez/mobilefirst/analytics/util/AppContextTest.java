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
import org.junit.Before;
import org.junit.Test;

public class AppContextTest {
    private AppContext appContext;

    @Before
    public void init() {
        appContext = new AppContext();
    }

    @Test
    public void testSetAppName() {
        appContext.setAppName("HelloWorld");

        Assert.assertTrue(appContext.getAppName().equals("HelloWorld"));
    }

    @Test
    public void testSetAppVersion() {
        appContext.setAppVersion("5.1");

        Assert.assertTrue(appContext.getAppVersion().equals("5.1"));
    }

    @Test
    public void testSetDeviceOS() {
        appContext.setDeviceOS("android");

        Assert.assertTrue(appContext.getDeviceOS().equals("android"));
    }

    @Test
    public void testSetDeviceOSVersion() {
        appContext.setDeviceOSVersion("6.0");

        Assert.assertTrue(appContext.getDeviceOSVersion().equals("6.0"));
    }

    @Test
    public void testSetDeviceModel() {
        appContext.setDeviceModel("Nexus 5X");

        Assert.assertTrue(appContext.getDeviceModel().equals("Nexus 5X"));
    }

    @Test
    public void testSetDeviceID() {
        appContext.setDeviceID("123456");

        Assert.assertTrue(appContext.getDeviceID().equals("123456"));
    }

}
