## MobileFirst Custom Analytics Sender
Sending custom analytics from your MobileFirst Platform apps to the MobileFirst Operational Analytics Server be accomplished by using `WL.Analytics.log`, `WLAnalytics.log`, or `[[WLAnalytics sharedInstance] log]` for Hybrid/Cordova, Android, and iOS apps. However, sending custom analytics from third-party applications is currently not supported. By using this source code in this project you are able to send custom analytics to your MobileFirst Operational Analytics Server.

## Usage

Follow these steps to send custom analytics from your own applications

### Building the jar
This is a gradle based project so to build make sure you have gradle installed and as part of your executable path. To build, navigate into the root directory of the project and run `gradle build`. After a successful build, the generated .jar will be located in `./build/libs/analytics-api-0.1.1.jar`.

### Instrumenting your application

Once you have a jar, you need to add it to the build path of your external (third-party) application. Follow the sample code below for more details.

```java

import com.yoelnunez.mobilefirst.analytics.AnalyticsAPI;
import com.yoelnunez.mobilefirst.analytics.exceptions.MissingServerContextException;
import com.yoelnunez.mobilefirst.analytics.util.AppContext;
import com.yoelnunez.mobilefirst.analytics.util.ServerContext;
import org.json.JSONObject;

public class MyCustomApplication {
  public static void main(String[] args) {
    // should match the value of the `wl.analytics.url` jndi property in your server.xml file
    String analyticsEndpoint = "http://yoelnunez.com/analytics/v2"

    // IMPORTANT: setting the analytics server info
    AnalyticsAPI.setContext(new ServerContext(analyticsEndpoint, "myUsername", "myPassword"));

    try {

      // Attach some metadata to your logs, i.e., which app and device
      AppContext appContext = new AppContext();
      appContext.setAppName("CustomerApplication");
      appContext.setAppVersion("1.0");
      appContext.setDeviceID("my-device-id");
      appContext.setDeviceOS("Android");
      appContext.setDeviceOSVersion("6.0");
      appContext.setDeviceModel("Nexus 5X");

      // Analytics api instance
      AnalyticsAPI analytics = AnalyticsAPI.createInstance(appContext);


      JSONObject customer1 = new JSONObject();
      customer1.put("firstName", "Yoel");
      customer1.put("lastName", "Nunez");
      customer1.put("age", 25);

      // log custom data
      analytics.log("customer1", customer1);


      JSONObject customer2 = new JSONObject();
      customer2.put("firstName", "John");
      customer2.put("lastName", "Doe");
      customer2.put("age", 23);

      // log custom data
      analytics.log("customer2", customer2);


      // send data to analytics server
      AnalyticsAPI.send();

    } catch (MissingServerContextException e) {
      // analytics server endpoint details missing
    }
  }
}

```

## Supported Versions
IBM MobileFirst Platform Foundation 7.1

## License
Copyright 2016 Yoel Nunez

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
