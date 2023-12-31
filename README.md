
# SerpApi Java Library

![test](https://github.com/serpapi/serpapi-java/workflows/test/badge.svg) [![](https://jitpack.io/v/serpapi/serpapi-java.svg)](https://jitpack.io/#serpapi/serpapi-java)

Integrate search data into your Java application. This library is the official wrapper for SerpApi (https://serpapi.com).

SerpApi supports Google, Google Maps, Google Shopping, Baidu, Yandex, Yahoo, eBay, App Stores, and more.

[The full documentation is available here.](https://serpapi.com/search-api)

## Installation 

Using Maven / Gradle.

Edit your build.gradle file
```java
repositories {
    maven { url "https://jitpack.io" }
}

dependencies {
    implementation 'com.github.serpapi:serpapi-java:1.0.0'
}
```

To list all the version available.
https://jitpack.io/api/builds/com.github.serpapi/serpapi-java

or you can download the jar file from https://github.com/serpapi/serpapi-java.git

Note: jitpack.io enables to download maven package directly from github release.

## Usage

To get started with this project in Java. 
We provided a fully working example.
```bash
git clone https://github.com/serpapi/serpapi-java.git
cd serpapi-java/demo
make run api_key=<your private key>
```
Note: You need an account with SerpApi to obtain this key from: https://serpapi.com/dashboard

file: demo/src/main/java/demo/App.java
```java
public class App {
    public static void main(String[] args) throws SerpApiException {
        if(args.length != 1) {
            System.out.println("Usage: app <secret api key>");
            System.exit(1);
        }

        String location = "Austin,Texas";
        System.out.println("find the first Coffee in " + location);

        // parameters
        Map<String, String> parameter = new HashMap<>();
        parameter.put("q", "Coffee");
        parameter.put("location", location);
        parameter.put("api_key", args[0]);

        // Create search
        SerpApi serpapi = new SerpApi(parameter);

        try {
            // Execute search
            JsonObject data = serpapi.search(parameter);

            // Decode response
            JsonArray results = data.get("local_results").getAsJsonObject().get("places").getAsJsonArray();
            JsonObject first_result = results.get(0).getAsJsonObject();
            System.out.println("first coffee: " + first_result.get("title").getAsString() + " in " + location);
        } catch (SerpApiException e) {
            System.out.println("oops exception detected!");
            e.printStackTrace();
        }
    }
}
```

The [SerpApi.com API Documentation](https://serpapi.com/search-api) contains a list of all the possible parameters that can be passed to the API.


## Documentation

Documentation is [available on Read the Docs](https://serpapi-python.readthedocs.io/en/latest/).

## Requirements

This project is an implementation of SerpApi in Java 7.
This code depends on GSON for efficient JSON processing.
The HTTP response are converted to JSON Object.

An example is provided in the test.
@see src/test/java/SerpApiImplementationTest.java

Runtime:
 - Java / JDK 8+ (https://www.java.com/en/download/)
   Older version of java do not support HTTPS protocol. 
   The SSLv3 is buggy which leads to Java raising this exception: javax.net.ssl.SSLHandshakeException

For development:
 - Gradle 6.7+ (https://gradle.org/install/) 


### Location API

```java
Map<String, String> parameter = new HashMap<String, String>();
parameter.put("api_key", System.getenv("API_KEY"));
SerpApi serpapi = new SerpApi(parameter);

parameter.put("q", "Austin");
parameter.put("limit", "3");
JsonArray location = serpapi.location(parameter);
System.out.println(location.toString());
```
it prints the first 3 location matching Austin (Texas, Texas, Rochester)

### Search Archive API

Let's run a search to get a search_id.
```java
Map<String, String> parameter = new HashMap<>();
parameter.put("api_key", System.getenv("API_KEY"));
parameter.put("q", "Coffee");
parameter.put("location", "Austin, Texas, United States");
parameter.put("hl", "en");
parameter.put("gl", "us");
parameter.put("google_domain", "google.com");
parameter.put("safe", "active");
parameter.put("start", "10");
parameter.put("num", "10");
parameter.put("device", "desktop");

SerpApi serpapi = new SerpApi(parameter);
JsonObject results = serpapi.search(parameter);



```

Now let retrieve the previous search from the archive.
```java
// now search in the archive
String id = results.getAsJsonObject("search_metadata").getAsJsonPrimitive("id").getAsString();

// retrieve search from the archive with speed for free
JsonObject archive = serpapi.searchArchive(id);
System.out.println(archive.toString());
```
it prints the search from the archive which matches 1:1 to previous search results.

### Account API
Get account API
```java
Map<String, String> parameter = new HashMap<>();
parameter.put("api_key", "your_api_key");

SerpApi serpapi = new SerpApi(parameter);
JsonObject account = serpapi.account();
System.out.println(account.toString());
```
it prints your account information.

## Examples in java

### Search bing
```java
Map<String, String> parameter = new HashMap<>();
parameter.put("engine", "bing");
parameter.put("api_key", "your_api_key");

SerpApi serpapi = new SerpApi(parameter);

// set search parameter
parameter.put("q", "coffee");

JsonObject results = serpapi.search(parameter);
System.out.println(results.toString());
```

 * source code: [src/test/java/serpapi/BingTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/BingTest.java)
see: [https://serpapi.com/bing-search-api](https://serpapi.com/bing-search-api)

### Search baidu
```java
Map<String, String> parameter = new HashMap<>();
parameter.put("engine", "baidu");
parameter.put("api_key", "your_api_key");

SerpApi serpapi = new SerpApi(parameter);

// set search parameter
parameter.put("q", "coffee");

JsonObject results = serpapi.search(parameter);
System.out.println(results.toString());
```

 * source code: [src/test/java/serpapi/BaiduTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/BaiduTest.java)
see: [https://serpapi.com/baidu-search-api](https://serpapi.com/baidu-search-api)

### Search yahoo
```java
Map<String, String> parameter = new HashMap<>();
parameter.put("engine", "yahoo");
parameter.put("api_key", "your_api_key");

SerpApi serpapi = new SerpApi(parameter);

// set search parameter
parameter.put("p", "coffee");

JsonObject results = serpapi.search(parameter);
System.out.println(results.toString());
```

 * source code: [src/test/java/serpapi/YahooTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/YahooTest.java)
see: [https://serpapi.com/yahoo-search-api](https://serpapi.com/yahoo-search-api)

### Search youtube
```java
Map<String, String> parameter = new HashMap<>();
parameter.put("engine", "youtube");
parameter.put("api_key", "your_api_key");

SerpApi serpapi = new SerpApi(parameter);

// set search parameter
parameter.put("search_query", "coffee");

JsonObject results = serpapi.search(parameter);
System.out.println(results.toString());
```

 * source code: [src/test/java/serpapi/YoutubeTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/YoutubeTest.java)
see: [https://serpapi.com/youtube-search-api](https://serpapi.com/youtube-search-api)

### Search walmart
```java
Map<String, String> parameter = new HashMap<>();
parameter.put("engine", "walmart");
parameter.put("api_key", "your_api_key");

SerpApi serpapi = new SerpApi(parameter);

// set search parameter
parameter.put("query", "coffee");

JsonObject results = serpapi.search(parameter);
System.out.println(results.toString());
```

 * source code: [src/test/java/serpapi/WalmartTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/WalmartTest.java)
see: [https://serpapi.com/walmart-search-api](https://serpapi.com/walmart-search-api)

### Search ebay
```java
Map<String, String> parameter = new HashMap<>();
parameter.put("engine", "ebay");
parameter.put("api_key", "your_api_key");

SerpApi serpapi = new SerpApi(parameter);

// set search parameter
parameter.put("_nkw", "coffee");

JsonObject results = serpapi.search(parameter);
System.out.println(results.toString());
```

 * source code: [src/test/java/serpapi/EbayTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/EbayTest.java)
see: [https://serpapi.com/ebay-search-api](https://serpapi.com/ebay-search-api)

### Search naver
```java
Map<String, String> parameter = new HashMap<>();
parameter.put("engine", "naver");
parameter.put("api_key", "your_api_key");

SerpApi serpapi = new SerpApi(parameter);

// set search parameter
parameter.put("query", "coffee");

JsonObject results = serpapi.search(parameter);
System.out.println(results.toString());
```

 * source code: [src/test/java/serpapi/NaverTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/NaverTest.java)
see: [https://serpapi.com/naver-search-api](https://serpapi.com/naver-search-api)

### Search home depot
```java
Map<String, String> parameter = new HashMap<>();
parameter.put("engine", "home_depot");
parameter.put("api_key", "your_api_key");

SerpApi serpapi = new SerpApi(parameter);

// set search parameter
parameter.put("q", "table");

JsonObject results = serpapi.search(parameter);
System.out.println(results.toString());
```

 * source code: [src/test/java/serpapi/HomeDepotTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/HomeDepotTest.java)
see: [https://serpapi.com/home-depot-search-api](https://serpapi.com/home-depot-search-api)

### Search apple app store
```java
Map<String, String> parameter = new HashMap<>();
parameter.put("engine", "apple_app_store");
parameter.put("api_key", "your_api_key");

SerpApi serpapi = new SerpApi(parameter);

// set search parameter
parameter.put("term", "coffee");

JsonObject results = serpapi.search(parameter);
System.out.println(results.toString());
```

 * source code: [src/test/java/serpapi/AppleAppStoreTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/AppleAppStoreTest.java)
see: [https://serpapi.com/apple-app-store](https://serpapi.com/apple-app-store)

### Search duckduckgo
```java
Map<String, String> parameter = new HashMap<>();
parameter.put("engine", "duckduckgo");
parameter.put("api_key", "your_api_key");

SerpApi serpapi = new SerpApi(parameter);

// set search parameter
parameter.put("q", "coffee");

JsonObject results = serpapi.search(parameter);
System.out.println(results.toString());
```

 * source code: [src/test/java/serpapi/DuckduckgoTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/DuckduckgoTest.java)
see: [https://serpapi.com/duckduckgo-search-api](https://serpapi.com/duckduckgo-search-api)

### Search google
```java
Map<String, String> parameter = new HashMap<>();
parameter.put("engine", "google");
parameter.put("api_key", "your_api_key");

SerpApi serpapi = new SerpApi(parameter);

// set search parameter
parameter.put("q", "coffee");
parameter.put("engine", "google");

JsonObject results = serpapi.search(parameter);
System.out.println(results.toString());
```

 * source code: [src/test/java/serpapi/GoogleTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/GoogleTest.java)
see: [https://serpapi.com/search-api](https://serpapi.com/search-api)

### Search google scholar
```java
Map<String, String> parameter = new HashMap<>();
parameter.put("engine", "google_scholar");
parameter.put("api_key", "your_api_key");

SerpApi serpapi = new SerpApi(parameter);

// set search parameter
parameter.put("q", "coffee");

JsonObject results = serpapi.search(parameter);
System.out.println(results.toString());
```

 * source code: [src/test/java/serpapi/GoogleScholarTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/GoogleScholarTest.java)
see: [https://serpapi.com/google-scholar-api](https://serpapi.com/google-scholar-api)

### Search google autocomplete
```java
Map<String, String> parameter = new HashMap<>();
parameter.put("engine", "google_autocomplete");
parameter.put("api_key", "your_api_key");

SerpApi serpapi = new SerpApi(parameter);

// set search parameter
parameter.put("q", "coffee");

JsonObject results = serpapi.search(parameter);
System.out.println(results.toString());
```

 * source code: [src/test/java/serpapi/GoogleAutocompleteTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/GoogleAutocompleteTest.java)
see: [https://serpapi.com/google-autocomplete-api](https://serpapi.com/google-autocomplete-api)

### Search google product
```java
Map<String, String> parameter = new HashMap<>();
parameter.put("engine", "google_product");
parameter.put("api_key", "your_api_key");

SerpApi serpapi = new SerpApi(parameter);

// set search parameter
parameter.put("q", "coffee");
parameter.put("product_id", "4887235756540435899");

JsonObject results = serpapi.search(parameter);
System.out.println(results.toString());
```

 * source code: [src/test/java/serpapi/GoogleProductTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/GoogleProductTest.java)
see: [https://serpapi.com/google-product-api](https://serpapi.com/google-product-api)

### Search google reverse image
```java
Map<String, String> parameter = new HashMap<>();
parameter.put("engine", "google_reverse_image");
parameter.put("api_key", "your_api_key");

SerpApi serpapi = new SerpApi(parameter);

// set search parameter
parameter.put("image_url", "https://i.imgur.com/5bGzZi7.jpg");
parameter.put("max_results", "1");

JsonObject results = serpapi.search(parameter);
System.out.println(results.toString());
```

 * source code: [src/test/java/serpapi/GoogleReverseImageTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/GoogleReverseImageTest.java)
see: [https://serpapi.com/google-reverse-image](https://serpapi.com/google-reverse-image)

### Search google events
```java
Map<String, String> parameter = new HashMap<>();
parameter.put("engine", "google_events");
parameter.put("api_key", "your_api_key");

SerpApi serpapi = new SerpApi(parameter);

// set search parameter
parameter.put("q", "coffee");

JsonObject results = serpapi.search(parameter);
System.out.println(results.toString());
```

 * source code: [src/test/java/serpapi/GoogleEventsTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/GoogleEventsTest.java)
see: [https://serpapi.com/google-events-api](https://serpapi.com/google-events-api)

### Search google local services
```java
Map<String, String> parameter = new HashMap<>();
parameter.put("engine", "google_local_services");
parameter.put("api_key", "your_api_key");

SerpApi serpapi = new SerpApi(parameter);

// set search parameter
parameter.put("q", "electrician");
parameter.put("data_cid", "6745062158417646970");

JsonObject results = serpapi.search(parameter);
System.out.println(results.toString());
```

 * source code: [src/test/java/serpapi/GoogleLocalServicesTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/GoogleLocalServicesTest.java)
see: [https://serpapi.com/google-local-services-api](https://serpapi.com/google-local-services-api)

### Search google maps
```java
Map<String, String> parameter = new HashMap<>();
parameter.put("engine", "google_maps");
parameter.put("api_key", "your_api_key");

SerpApi serpapi = new SerpApi(parameter);

// set search parameter
parameter.put("q", "pizza");
parameter.put("ll", "@40.7455096,-74.0083012,15.1z");
parameter.put("type", "search");

JsonObject results = serpapi.search(parameter);
System.out.println(results.toString());
```

 * source code: [src/test/java/serpapi/GoogleMapsTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/GoogleMapsTest.java)
see: [https://serpapi.com/google-maps-api](https://serpapi.com/google-maps-api)

### Search google jobs
```java
Map<String, String> parameter = new HashMap<>();
parameter.put("engine", "google_jobs");
parameter.put("api_key", "your_api_key");

SerpApi serpapi = new SerpApi(parameter);

// set search parameter
parameter.put("q", "coffee");

JsonObject results = serpapi.search(parameter);
System.out.println(results.toString());
```

 * source code: [src/test/java/serpapi/GoogleJobsTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/GoogleJobsTest.java)
see: [https://serpapi.com/google-jobs-api](https://serpapi.com/google-jobs-api)

### Search google play
```java
Map<String, String> parameter = new HashMap<>();
parameter.put("engine", "google_play");
parameter.put("api_key", "your_api_key");

SerpApi serpapi = new SerpApi(parameter);

// set search parameter
parameter.put("q", "kite");
parameter.put("store", "apps");
parameter.put("max_results", "2");

JsonObject results = serpapi.search(parameter);
System.out.println(results.toString());
```

 * source code: [src/test/java/serpapi/GooglePlayTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/GooglePlayTest.java)
see: [https://serpapi.com/google-play-api](https://serpapi.com/google-play-api)

### Search google images
```java
Map<String, String> parameter = new HashMap<>();
parameter.put("engine", "google_images");
parameter.put("api_key", "your_api_key");

SerpApi serpapi = new SerpApi(parameter);

// set search parameter
parameter.put("engine", "google_images");
parameter.put("tbm", "isch");
parameter.put("q", "coffee");

JsonObject results = serpapi.search(parameter);
System.out.println(results.toString());
```

 * source code: [src/test/java/serpapi/GoogleImagesTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/GoogleImagesTest.java)
see: [https://serpapi.com/images-results](https://serpapi.com/images-results)


### Contributing

We love true open source, continuous integration and Test Drive Development (TDD). 
 We are using JUnit test, Git action, gradle [our infrastructure around the clock](https://travis-ci.org/serpapi/serpapi-java) to achieve the best QoS (Quality Of Service).
 
To run the test:
```gradle test```

#### How to build from the source ?

You must clone this repository.
```bash
git clone https://github.com/serpapi/serpapi-java.git
```
Build the jar file.
```bash
gradle build
```
Copy the jar to your project lib/ directory.
```bash
cp build/libs/serpapi-java.jar path/to/yourproject/lib
```

## Java limitation
### SSL handshake error.
#### symptom

javax.net.ssl.SSLHandshakeException

#### cause
SerpApi is using HTTPS / SSLv3. Older JVM version do not support this protocol because it's more recent. 

#### solution
Upgrade java to 1.8_201+ (which is recommended by Oracle).

 * On OSX you can switch versino of Java.
```sh
export JAVA_HOME=`/usr/libexec/java_home -v 1.8.0_201`
java -version
```

 * On Windows manually upgrade your JDK / JVM to the latest.

### Inspiration
 * http://www.baeldung.com/java-http-request
 * https://github.com/google/gson
 
## License
MIT license

## Changelog
- 1.0.0 - Fully revisit API naming convention, and generalize client usage to match serpapi.com latest development

