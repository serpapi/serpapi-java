# Google Search Results JAVA API

![test](https://github.com/serpapi/google-search-results-java/workflows/test/badge.svg)

This Java package enables to scrape and parse Google, Bing and Baidu search results using [SerpApi](https://serpapi.com). Feel free to fork this repository to add more backends.

This project is an implementation of SerpApi in Java 7.
This code depends on GSON for efficient JSON processing.
The HTTP response are converted to JSON Object.

An example is provided in the test.
@see src/test/java/GoogleSearchImplementationTest.java

[The full documentation is available here.](https://serpapi.com/search-api)

## Requirements

Runtime:
 - Java / JDK 8+ (https://www.java.com/en/download/)
   Older version of java do not support HTTPS protocol. 
   The SSLv3 is buggy which leads to Java raising this exception: javax.net.ssl.SSLHandshakeException

For development:
 - Gradle 6.7+ (https://gradle.org/install/) 

## Maven / Gradle support

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

Note: jitpack.io enables to download maven package directly from github release.

## Quick start

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
        parameter.put("apikey", args[0]);

        // Create search
        SerpApi serpapi = new SerpApi(parameter);

        try {
            // Execute search
            JsonObject data = search.getJson();

            // Decode response
            JsonArray results = (JsonArray) data.get("local_results");
            JsonObject first_result = results.get(0).getAsJsonObject();
            System.out.println("first coffee: " + first_result.get("title").getAsString() + " in " + location);
        } catch (SerpApiException e) {
            System.out.println("oops exception detected!");
            e.printStackTrace();
        }
    }
}
```

This example runs a search about "coffee" using your secret api key.

The Serp API service (backend)
 - searches on Google using the query: q = "coffee"
 - parses the messy HTML responses
 - return a standardized JSON response
The class GoogleSearch
 - Format the request to Serp API server
 - Execute GET http request
 - Parse JSON into Ruby Hash using JSON standard library provided by Ruby
Et voila..

Alternatively, you can search:
 - Bing using BingSearch class
 - Baidu using BaiduSearch class

See the playground to generate your code.
 https://serpapi.com/playground

## Example
 * [How to set SERP API key](#how-to-set-serp-api-key)
 * [Search API capability](#search-api-capability)
 * [Example by specification](#example-by-specification)
 * [Location API](#location-api)
 * [Search Archive API](#search-archive-api)
 * [Account API](#account-api)

## How to set SERP API key
The Serp API key can be set globally using a singleton pattern.
```java
GoogleSearch.serp_api_key_default = "Your Private Key"
search = GoogleSearch(parameter)
```
Or the Serp API key can be provided for each query.

```java
search = GoogleSearch(parameter, "Your Private Key")
```

## Example with all params and all outputs

```java
query_parameter = {
  "q": "query",
  "google_domain": "Google Domain",
  "location": "Location Requested",
  "device": device,
  "hl": "Google UI Language",
  "gl": "Google Country",
  "safe": "Safe Search Flag",
  "num": "Number of Results",
  "start": "Pagination Offset",
  "serp_api_key": "Your SERP API Key",
  "tbm": "nws|isch|shop",
  "tbs": "custom to be search criteria",
  "async": true|false,    // allow async request - non-blocker
  "output": "json|html",  // output format
}

query = GoogleSearch.new(query_parameter)
query.parameter.put("location", "Austin,Texas")

String html_results = query.getHtml()
JsonObject json_results = query.getJson()
```

### Example by specification

We love true open source, continuous integration and Test Drive Development (TDD). 
 We are using RSpec to test [our infrastructure around the clock](https://travis-ci.org/serpapi/google-search-results-ruby) to achieve the best QoS (Quality Of Service).
 
The directory test/ includes specification/examples.

To run the test:
```gradle test```


### Location API

```java
GoogleSearch search = new GoogleSearch(new HashMap<String, String());
JsonArray locationList = search.getLocation("Austin", 3);
System.out.println(locationList.toString());
```
it prints the first 3 location matching Austin (Texas, Texas, Rochester)

### Search Archive API

Let's run a search to get a search_id.
```java
Map<String, String> parameter = new HashMap<>();
parameter.put("q", "Coffee");
parameter.put("location", "Austin,Texas");

GoogleSearch search = new GoogleSearch(parameter);
JsonObject result = search.getJson();
int search_id = result.get("search_metadata").getAsJsonObject().get("id").getAsInt();
```

Now let retrieve the previous search from the archive.
```java
JsonObject archived_result = search.getSearchArchive(search_id);
System.out.println(archived_result.toString());
```
it prints the search from the archive.

### Account API
Get account API
```java
GoogleSearch.serp_api_key_default = "Your Private Key"
GoogleSearch search = new GoogleSearch();
JsonObject info = search.getAccount();
System.out.println(info.toString());
```
it prints your account information.

## Build project

### How to build from the source ?

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

### How to test ?

```bash
make test
```

### Conclusion

This service supports Google Images, News, Shopping.
To enable a type of search, the field tbm (to be matched) must be set to:

 * isch: Google Images API.
 * nws: Google News API.
 * shop: Google Shopping API.
 * any other Google service should work out of the box.
 * (no tbm parameter): regular Google Search.

[The full documentation is available here.](https://serpapi.com/search-api)

Issue
---
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

Changelog
---
- 1.0.0 - Fully revisit API naming convention, and generalize client usage to match serpapi.com latest development

Source
---
 * http://www.baeldung.com/java-http-request
 * https://github.com/google/gson

Author
---
Victor Benarbia - victor@serpapi.com
