
# SerpApi Java Library

[![serpapi-java](https://github.com/serpapi/serpapi-java/actions/workflows/gradle.yml/badge.svg)](https://github.com/serpapi/serpapi-java/actions/workflows/gradle.yml)
[![](https://jitpack.io/v/serpapi/serpapi-java.svg)](https://jitpack.io/#serpapi/serpapi-java)

Integrate search data into your Java application. This library is the official wrapper for [SerpApi](https://serpapi.com).

SerpApi supports Google, Google Maps, Google Shopping, Baidu, Yandex, Yahoo, eBay, App Stores, and more.

[The full documentation is available here.](https://serpapi.com/search-api)

## Installation 

Using Maven / Gradle.

Edit your `build.gradle` file:
```gradle
repositories {
    maven { url "https://jitpack.io" }
}

dependencies {
    implementation 'com.github.serpapi:serpapi-java:1.1.0'
}
```

To list all available versions:
https://jitpack.io/api/builds/com.github.serpapi/serpapi-java

or you can download the jar file from https://github.com/serpapi/serpapi-java/releases

Note: JitPack builds Maven artifacts from GitHub releases and tags.

## Usage

To try the library quickly, use the demo project:
```bash
git clone https://github.com/serpapi/serpapi-java.git
cd serpapi-java/demo
make all SERPAPI_KEY='<your private key>'
```
Use quotes if your key contains shell-special characters. You need a SerpApi account to obtain a key: https://serpapi.com/dashboard

`demo/src/main/java/demo/App.java`:
```javapublic 
class App {
    public static void main(String[] args) {
        String apiKey = System.getenv("SERPAPI_KEY");

        // set search location
        String location = "Austin,Texas";
        String engine = "google";
        System.out.println("find the first coffee shop in " + location + " using " + engine);

        Map<String, String> auth = new HashMap<>();
        auth.put("engine", engine);
        auth.put("api_key", apiKey);

        // create client
        SerpApi serpapi= new SerpApi(auth);

        // create search parameters
        Map<String, String> parameter = new HashMap<>();
        parameter.put("q", "Coffee");
        parameter.put("location", location);

        // perform search
        try {
            // get search results
            JsonObject data = serpapi.search(parameter);
            JsonArray organic = data.getAsJsonArray("organic_results");
            JsonObject first = organic.get(0).getAsJsonObject();
            System.out.println("First result: " + first.get("title").getAsString() + " (search near " + location + ")");
        } catch (SerpApiException e) {
            System.out.println("SerpApi request failed.");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
```

The [SerpApi.com API Documentation](https://serpapi.com/search-api) contains a list of all the possible parameters that can be passed to the API.


## Documentation

- [SerpApi Search API](https://serpapi.com/search-api) — parameters, engines, and response formats  
- After cloning, run `./gradlew javadoc` and open `build/docs/javadoc/index.html` for this library’s Javadoc.

## Requirements

This library uses [Gson](https://github.com/google/gson) for JSON and returns responses as Gson `JsonObject` / `JsonArray`.

**This repository** is built and tested with **JDK 21** and the **Gradle wrapper** (`./gradlew`, currently Gradle 8.5). Use the wrapper so you do not need a separate Gradle install.

**Consumers** of the JitPack artifact should run a JVM whose version is at least the **bytecode level** of the release you depend on (releases from this branch target **Java 21**).


### Location API
```java
SerpApi serpapi = new SerpApi();

Map<String, String> parameter = new HashMap<String, String>();
parameter.put("q", "Austin");
parameter.put("limit", "3");
JsonArray location = serpapi.location(parameter);
System.out.println(location.get(0).getAsJsonObject().get("name").getAsString());
// Prints the first matching name among up to 3 results (see LocationApiTest for a JUnit example).
```

[LocationApiTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/LocationApiTest.java)

### Search Archive API

Run a search to obtain a `search_id`.
```java
Map<String, String> auth = new HashMap<>();
auth.put("api_key", "your_api_key");
SerpApi serpapi = new SerpApi(auth);

Map<String, String> parameter = new HashMap<>();
parameter.put("q", "Coffee");
parameter.put("location", "Austin, Texas, United States");
parameter.put("hl", "en");
parameter.put("gl", "us");
parameter.put("google_domain", "google.com");
parameter.put("safe", "active");
parameter.put("start", "10");
parameter.put("device", "desktop");
JsonObject results = serpapi.search(parameter);
```

Retrieve the same search from the archive:
```java
// now search in the archive
String id = results.getAsJsonObject("search_metadata").getAsJsonPrimitive("id").getAsString();

// retrieve search from the archive with speed for free
JsonObject archive = serpapi.searchArchive(id);
System.out.println(archive.toString());
```
The archived JSON matches the original search result. In tests, the key is supplied via `System.getenv("SERPAPI_KEY")`; see `SerpApiTest.java`.

[SerpApiTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/SerpApiTest.java)

### Account API

```java
Map<String, String> parameter = new HashMap<>();
parameter.put("api_key", "your_api_key");

SerpApi serpapi = new SerpApi(parameter);
JsonObject account = serpapi.account();
System.out.println(account.toString());
```
it prints your account information.

[AccountApiTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/AccountApiTest.java)

### Markdown output

```java
Map<String, String> auth = new HashMap<>();
auth.put("api_key", "your_api_key");
auth.put("engine", "google");
SerpApi serpapi = new SerpApi(auth);

Map<String, String> parameter = new HashMap<>();
parameter.put("q", "coffee");
System.out.println(serpapi.markdown(parameter));
```
it prints the results as raw markdown, intended for LLM and agent consumption.

[MarkdownApiTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/MarkdownApiTest.java)

## Examples in Java

### Search bing
```java
// setup serpapi client
Map<String, String> auth = new HashMap<>();
auth.put("api_key", "your_api_key");
SerpApi client = new SerpApi(auth);

// run search
Map<String, String> parameter = new HashMap<>();
parameter.put("engine", "bing");
parameter.put("q", "coffee");
JsonObject results = client.search(parameter);
System.out.println(results.toString());
```

 * source code: [src/test/java/serpapi/example/BingTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/example/BingTest.java)
see: [https://serpapi.com/bing-search-api](https://serpapi.com/bing-search-api)

### Search baidu
```java
// setup serpapi client
Map<String, String> auth = new HashMap<>();
auth.put("api_key", "your_api_key");
SerpApi client = new SerpApi(auth);

// run search
Map<String, String> parameter = new HashMap<>();
parameter.put("engine", "baidu");
parameter.put("q", "coffee");
JsonObject results = client.search(parameter);
System.out.println(results.toString());
```

 * source code: [src/test/java/serpapi/example/BaiduTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/example/BaiduTest.java)
see: [https://serpapi.com/baidu-search-api](https://serpapi.com/baidu-search-api)

### Search yahoo
```java
// setup serpapi client
Map<String, String> auth = new HashMap<>();
auth.put("api_key", "your_api_key");
SerpApi client = new SerpApi(auth);

// run search
Map<String, String> parameter = new HashMap<>();
parameter.put("engine", "yahoo");
parameter.put("p", "coffee");
JsonObject results = client.search(parameter);
System.out.println(results.toString());
```

 * source code: [src/test/java/serpapi/example/YahooTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/example/YahooTest.java)
see: [https://serpapi.com/yahoo-search-api](https://serpapi.com/yahoo-search-api)

### Search youtube
```java
// setup serpapi client
Map<String, String> auth = new HashMap<>();
auth.put("api_key", "your_api_key");
SerpApi client = new SerpApi(auth);

// run search
Map<String, String> parameter = new HashMap<>();
parameter.put("engine", "youtube");
parameter.put("search_query", "coffee");
JsonObject results = client.search(parameter);
System.out.println(results.toString());
```

 * source code: [src/test/java/serpapi/example/YoutubeTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/example/YoutubeTest.java)
see: [https://serpapi.com/youtube-search-api](https://serpapi.com/youtube-search-api)

### Search walmart
```java
// setup serpapi client
Map<String, String> auth = new HashMap<>();
auth.put("api_key", "your_api_key");
SerpApi client = new SerpApi(auth);

// run search
Map<String, String> parameter = new HashMap<>();
parameter.put("engine", "walmart");
parameter.put("query", "coffee");
JsonObject results = client.search(parameter);
System.out.println(results.toString());
```

 * source code: [src/test/java/serpapi/example/WalmartTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/example/WalmartTest.java)
see: [https://serpapi.com/walmart-search-api](https://serpapi.com/walmart-search-api)

### Search ebay
```java
// setup serpapi client
Map<String, String> auth = new HashMap<>();
auth.put("api_key", "your_api_key");
SerpApi client = new SerpApi(auth);

// run search
Map<String, String> parameter = new HashMap<>();
parameter.put("engine", "ebay");
parameter.put("_nkw", "coffee");
JsonObject results = client.search(parameter);
System.out.println(results.toString());
```

 * source code: [src/test/java/serpapi/example/EbayTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/example/EbayTest.java)
see: [https://serpapi.com/ebay-search-api](https://serpapi.com/ebay-search-api)

### Search naver
```java
// setup serpapi client
Map<String, String> auth = new HashMap<>();
auth.put("api_key", "your_api_key");
SerpApi client = new SerpApi(auth);

// run search
Map<String, String> parameter = new HashMap<>();
parameter.put("engine", "naver");
parameter.put("query", "coffee");
JsonObject results = client.search(parameter);
System.out.println(results.toString());
```

 * source code: [src/test/java/serpapi/example/NaverTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/example/NaverTest.java)
see: [https://serpapi.com/naver-search-api](https://serpapi.com/naver-search-api)

### Search home depot
```java
// setup serpapi client
Map<String, String> auth = new HashMap<>();
auth.put("api_key", "your_api_key");
SerpApi client = new SerpApi(auth);

// run search
Map<String, String> parameter = new HashMap<>();
parameter.put("engine", "home_depot");
parameter.put("q", "table");
JsonObject results = client.search(parameter);
System.out.println(results.toString());
```

 * source code: [src/test/java/serpapi/example/HomeDepotTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/example/HomeDepotTest.java)
see: [https://serpapi.com/home-depot-search-api](https://serpapi.com/home-depot-search-api)

### Search apple app store
```java
// setup serpapi client
Map<String, String> auth = new HashMap<>();
auth.put("api_key", "your_api_key");
SerpApi client = new SerpApi(auth);

// run search
Map<String, String> parameter = new HashMap<>();
parameter.put("engine", "apple_app_store");
parameter.put("term", "coffee");
JsonObject results = client.search(parameter);
System.out.println(results.toString());
```

 * source code: [src/test/java/serpapi/example/AppleAppStoreTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/example/AppleAppStoreTest.java)
see: [https://serpapi.com/apple-app-store](https://serpapi.com/apple-app-store)

### Search duckduckgo
```java
// setup serpapi client
Map<String, String> auth = new HashMap<>();
auth.put("api_key", "your_api_key");
SerpApi client = new SerpApi(auth);

// run search
Map<String, String> parameter = new HashMap<>();
parameter.put("engine", "duckduckgo");
parameter.put("q", "coffee");
JsonObject results = client.search(parameter);
System.out.println(results.toString());
```

 * source code: [src/test/java/serpapi/example/DuckduckgoTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/example/DuckduckgoTest.java)
see: [https://serpapi.com/duckduckgo-search-api](https://serpapi.com/duckduckgo-search-api)

### Search google
```java
// setup serpapi client
Map<String, String> auth = new HashMap<>();
auth.put("api_key", "your_api_key");
SerpApi client = new SerpApi(auth);

// run search
Map<String, String> parameter = new HashMap<>();
parameter.put("engine", "google");
parameter.put("q", "coffee");
parameter.put("engine", "google");
JsonObject results = client.search(parameter);
System.out.println(results.toString());
```

 * source code: [src/test/java/serpapi/example/GoogleTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/example/GoogleTest.java)
see: [https://serpapi.com/search-api](https://serpapi.com/search-api)

### Search google scholar
```java
// setup serpapi client
Map<String, String> auth = new HashMap<>();
auth.put("api_key", "your_api_key");
SerpApi client = new SerpApi(auth);

// run search
Map<String, String> parameter = new HashMap<>();
parameter.put("engine", "google_scholar");
parameter.put("q", "coffee");
JsonObject results = client.search(parameter);
System.out.println(results.toString());
```

 * source code: [src/test/java/serpapi/example/GoogleScholarTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/example/GoogleScholarTest.java)
see: [https://serpapi.com/google-scholar-api](https://serpapi.com/google-scholar-api)

### Search google autocomplete
```java
// setup serpapi client
Map<String, String> auth = new HashMap<>();
auth.put("api_key", "your_api_key");
SerpApi client = new SerpApi(auth);

// run search
Map<String, String> parameter = new HashMap<>();
parameter.put("engine", "google_autocomplete");
parameter.put("q", "coffee");
JsonObject results = client.search(parameter);
System.out.println(results.toString());
```

 * source code: [src/test/java/serpapi/example/GoogleAutocompleteTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/example/GoogleAutocompleteTest.java)
see: [https://serpapi.com/google-autocomplete-api](https://serpapi.com/google-autocomplete-api)

### Search google product
```java
// setup serpapi client
Map<String, String> auth = new HashMap<>();
auth.put("api_key", "your_api_key");
SerpApi client = new SerpApi(auth);

// run search
Map<String, String> parameter = new HashMap<>();
parameter.put("engine", "google_product");
parameter.put("q", "coffee");
parameter.put("product_id", "4887235756540435899");
JsonObject results = client.search(parameter);
System.out.println(results.toString());
```

 * source code: [src/test/java/serpapi/example/GoogleProductTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/example/GoogleProductTest.java)

see: [https://serpapi.com/google-product-api](https://serpapi.com/google-product-api)

### Search google reverse image
```java
// setup serpapi client
Map<String, String> auth = new HashMap<>();
auth.put("api_key", "your_api_key");
SerpApi client = new SerpApi(auth);

// run search
Map<String, String> parameter = new HashMap<>();
parameter.put("engine", "google_reverse_image");
parameter.put("image_url", "https://i.imgur.com/5bGzZi7.jpg");
JsonObject results = client.search(parameter);
System.out.println(results.toString());
```

 * source code: [src/test/java/serpapi/example/GoogleReverseImageTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/example/GoogleReverseImageTest.java)
see: [https://serpapi.com/google-reverse-image](https://serpapi.com/google-reverse-image)

### Search google events
```java
// setup serpapi client
Map<String, String> auth = new HashMap<>();
auth.put("api_key", "your_api_key");
SerpApi client = new SerpApi(auth);

// run search
Map<String, String> parameter = new HashMap<>();
parameter.put("engine", "google_events");
parameter.put("q", "coffee");
JsonObject results = client.search(parameter);
System.out.println(results.toString());
```

 * source code: [src/test/java/serpapi/example/GoogleEventsTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/example/GoogleEventsTest.java)
see: [https://serpapi.com/google-events-api](https://serpapi.com/google-events-api)

### Search google maps
```java
// setup serpapi client
Map<String, String> auth = new HashMap<>();
auth.put("api_key", "your_api_key");
SerpApi client = new SerpApi(auth);

// run search
Map<String, String> parameter = new HashMap<>();
parameter.put("engine", "google_maps");
parameter.put("q", "pizza");
parameter.put("ll", "@40.7455096,-74.0083012,15.1z");
parameter.put("type", "search");
JsonObject results = client.search(parameter);
System.out.println(results.toString());
```

 * source code: [src/test/java/serpapi/example/GoogleMapsTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/example/GoogleMapsTest.java)
see: [https://serpapi.com/google-maps-api](https://serpapi.com/google-maps-api)

### Search google jobs
```java
// setup serpapi client
Map<String, String> auth = new HashMap<>();
auth.put("api_key", "your_api_key");
SerpApi client = new SerpApi(auth);

// run search
Map<String, String> parameter = new HashMap<>();
parameter.put("engine", "google_jobs");
parameter.put("q", "coffee");
JsonObject results = client.search(parameter);
System.out.println(results.toString());
```

 * source code: [src/test/java/serpapi/example/GoogleJobsTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/example/GoogleJobsTest.java)
see: [https://serpapi.com/google-jobs-api](https://serpapi.com/google-jobs-api)

### Search google play
```java

// setup serpapi client
Map<String, String> auth = new HashMap<>();
auth.put("api_key", "your_api_key");
SerpApi client = new SerpApi(auth);

// run search
Map<String, String> parameter = new HashMap<>();
parameter.put("engine", "google_play");
parameter.put("q", "kite");
parameter.put("store", "apps");
JsonObject results = client.search(parameter);
JsonArray sections = results.getAsJsonArray("organic_results");
int appCount = 0;
for (JsonElement section : sections) {
  JsonObject sectionObj = section.getAsJsonObject();
  if (sectionObj.has("items") && sectionObj.get("items").isJsonArray()) {
    appCount += sectionObj.getAsJsonArray("items").size();
  }
}
System.out.println(results.toString());
```

 * source code: [src/test/java/serpapi/example/GooglePlayTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/example/GooglePlayTest.java)
see: [https://serpapi.com/google-play-api](https://serpapi.com/google-play-api)

### Search google images
```java
// setup serpapi client
Map<String, String> auth = new HashMap<>();
auth.put("api_key", "your_api_key");
SerpApi client = new SerpApi(auth);

// run search
Map<String, String> parameter = new HashMap<>();
parameter.put("engine", "google_images");
parameter.put("engine", "google_images");
parameter.put("tbm", "isch");
parameter.put("q", "coffee");
JsonObject results = client.search(parameter);
System.out.println(results.toString());
```

 * source code: [src/test/java/serpapi/example/GoogleImagesTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/example/GoogleImagesTest.java)
see: [https://serpapi.com/images-results](https://serpapi.com/images-results)


## Migration from google-search-results-java

If you are upgrading from the legacy [`google-search-results-java`](https://github.com/serpapi/google-search-results-java) library, here is a summary of what changed.

### Dependency

```gradle
// before
implementation 'com.github.serpapi:google-search-results-java:2.0.0'

// after
implementation 'com.github.serpapi:serpapi-java:1.1.0'
```

### Class and method renames

| Old (`google-search-results-java`) | New (`serpapi-java`) |
|------------------------------------|----------------------|
| `GoogleSearch` | `SerpApi` |
| `SerpApiSearch` | `SerpApi` |
| `client.getJson()` | `client.search(parameter)` |
| `client.getHtml()` | `client.html(parameter)` |
| `client.getSearchArchive(id)` | `client.searchArchive(id)` |
| `client.getAccount()` | `client.account()` |
| `client.getLocation(parameter)` | `client.location(parameter)` |
| `SerpApiSearchException` | `SerpApiException` |

### Example

```java
// before
Map<String, String> parameter = new HashMap<>();
parameter.put("q", "coffee");
parameter.put("api_key", "your_api_key");
GoogleSearch search = new GoogleSearch(parameter);
JsonObject results = search.getJson();

// after
Map<String, String> auth = new HashMap<>();
auth.put("api_key", "your_api_key");
SerpApi client = new SerpApi(auth);

Map<String, String> parameter = new HashMap<>();
parameter.put("q", "coffee");
parameter.put("engine", "google");
JsonObject results = client.search(parameter);
```

### Contributing

We use JUnit, **GitHub Actions** (see [workflow](https://github.com/serpapi/serpapi-java/blob/master/.github/workflows/gradle.yml)), and Gradle.

Run the full test suite locally (integration tests call the live API when a key is present):

```bash
export SERPAPI_KEY='your_key'   # optional: without it, many tests skip; some tests require the key and will fail if unset
./gradlew test
```

Regenerate `README.md` from the template after editing examples:

```bash
make readme   # requires Ruby `erb`
```

#### How to build from source

Clone the repository:
```bash
git clone https://github.com/serpapi/serpapi-java.git
cd serpapi-java
```

Build (use the wrapper):
```bash
./gradlew build
```

The main library JAR is under `build/libs/` (for example `serpapi-1.1.0.jar`, name follows `version` in `build.gradle`). Copy it into your project’s `lib/` directory if you are not using Maven/Gradle dependency resolution.

## TLS / HTTPS and older JVMs
### Symptom

`javax.net.ssl.SSLHandshakeException`

### Cause

SerpApi is served over **HTTPS (TLS)**. Very old JRE/JDK builds may lack the TLS versions or cipher suites required to connect.

### Solution

Use a **current JDK** (this project is tested on **JDK 21**). On macOS you can select an installed JDK, for example:

```sh
/usr/libexec/java_home -V
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
java -version
```

On Windows, install a current JDK from your vendor and point `JAVA_HOME` at it.

### Inspiration
 * https://www.baeldung.com/java-http-request
 * https://github.com/google/gson
 
## License
MIT license

## Changelog
- 1.1.0 — Java 21, Gradle 8.x; ongoing API and example updates
- 1.0.0 — Revisit API naming and align the client with serpapi.com

