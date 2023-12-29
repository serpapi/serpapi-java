# SerpApi Java Library

![test](https://github.com/serpapi/google-search-results-java/workflows/test/badge.svg)

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
    implementation 'com.github.serpapi:serpapi:1.0.0'
}
```

To list all the version available.
https://jitpack.io/api/builds/com.github.serpapi/serpapi

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

### Contributing

We love true open source, continuous integration and Test Drive Development (TDD). 
 We are using RSpec to test [our infrastructure around the clock](https://travis-ci.org/serpapi/google-search-results-ruby) to achieve the best QoS (Quality Of Service).
 
The directory test/ includes specification/examples.

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

