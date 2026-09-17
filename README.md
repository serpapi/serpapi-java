# SerpApi Java Library

[![serpapi-java](https://github.com/serpapi/serpapi-java/actions/workflows/gradle.yml/badge.svg)](https://github.com/serpapi/serpapi-java/actions/workflows/gradle.yml)
[![JitPack](https://jitpack.io/v/serpapi/serpapi-java.svg)](https://jitpack.io/#serpapi/serpapi-java)

Integrate search data into your AI workflow, RAG, fine-tuning, or Java application using this official [SerpApi Java SDK](https://serpapi.com/integrations/java).

[SerpApi](https://serpapi.com/) supports Google, Google Maps, Google Shopping, Baidu, Yandex, Yahoo, eBay, App Stores, and many more.

## Installation 

Installation of the serpapi-java package is done using Maven / Gradle.

Any new project should include these lines in a `build.gradle` file. 

```gradle
repositories {
    maven { url "https://jitpack.io" }
}

dependencies {
    implementation 'com.github.serpapi:serpapi-java:1.2.0'
}
```

To list all available versions see:
https://jitpack.io/api/builds/com.github.serpapi/serpapi-java

or you can download the jar file from https://github.com/serpapi/serpapi-java/releases.

_Note: JitPack builds Maven artifacts from GitHub releases and tags._

## Quickstart Tutorial

[Create a SerpApi account](https://serpapi.com/dashboard) to get your API key, then store it in an environment variable:

```export SERPAPI_KEY="your_api_key"```

To try the library quickly, use the demo project:
```bash
git clone https://github.com/serpapi/serpapi-java.git
cd serpapi-java/demo
make all SERPAPI_KEY='<your private key>'
```
Use quotes if your key contains shell-special characters. 

The `serpapi-java` package is already installed inside the `build.gradle` file of this cloned `serpapi-java` repository.

So, in this tutorial, no extra setup is needed. 
For future projects please refer to the above provided 'Installation' section.

`demo/src/main/java/demo/App.java`:
```java
class App {
    public static void main(String[] args) {
        String apiKey = System.getenv("SERPAPI_KEY");
        String location = "Austin,Texas";
        String engine = "google";
        System.out.println("find the first coffee shop in " + location + " using " + engine);

        Map<String, String> auth = new HashMap<>();
        auth.put("engine", engine);
        auth.put("api_key", apiKey);
        SerpApi serpapi = new SerpApi(auth);

        Map<String, String> parameter = new HashMap<>();
        parameter.put("q", "Coffee");
        parameter.put("location", location);

        try {
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

## Features

- Asynchronous searches for submitting non-blocking jobs and retrieving completed results from the Search Archive API
- Search results stored as a [Gson](https://github.com/google/gson) for JSON and returns responses as Gson `JsonObject` / `JsonArray` with `search`, token-efficient Markdown with `md`, or raw search-engine HTML with `html`
- SDK methods for the [Image API](https://serpapi.com/image-api), [Location API](https://serpapi.com/locations-api), [Search Archive API](https://serpapi.com/search-archive-api), and [Account API](https://serpapi.com/account-api)

## Response Formats

Use `search` for structured results decoded into a `Gson JsonObject / JsonArray`:

```results = client.search(parameter);```

Use `md` for a token-efficient Markdown String optimized for LLMs and AI agents:

```markdown = client.markdown(parameter);```

Use `html` when you need the raw search-engine response:

```rawHtml = client.html(parameter);```

Learn more about [SerpApi Markdown output](https://serpapi.com/markdown-output).

> A couple of notes:
> - `client` is a `SerpApi` instance you create with your API key, e.g. `SerpApi client = new SerpApi(auth);` where `auth` is a `Map<String, String>` containing `"api_key"` (and typically `"engine"`).
> - `parameter` is a `Map<String, String>` with your search parameters (`q`, `location`, etc.).
> - `client.markdown(parameter)` is new as of `serpapi-java` **1.2.0** - make sure your `build.gradle` dependency is at least that version

## Requirements

This library uses [Gson](https://github.com/google/gson) for JSON and returns responses as Gson `JsonObject` / `JsonArray`.

**This repository** is built and tested with **JDK 21** and the **Gradle wrapper** (`./gradlew`, currently Gradle 8.5). Use the wrapper so you do not need a separate Gradle install.

**Consumers** of the JitPack artifact should run a JVM whose version is at least the **bytecode level** of the release you depend on (releases from this branch target **Java 21**).

## Configuration

Set defaults when creating a client, then override search parameters in individual calls:
```java
Map<String, String> auth = new HashMap<>();
auth.put("api_key", System.getenv("SERPAPI_KEY"));
auth.put("engine", "google");
auth.put("hl", "en");
auth.put("gl", "us");
SerpApi client = new SerpApi(auth);

Map<String, String> parameter = new HashMap<>();
parameter.put("q", "coffee");
parameter.put("gl", "gb");
parameter.put("async", "false");

try {
    JsonObject results = client.search(parameter);
} catch (SerpApiException e) {
    e.printStackTrace();
}
```

| Field     | Default | Description                                                                                       |
| :-------- | :-----: | :------------------------------------------------------------------------------------------------ |
| `api_key` |  None   | Your SerpApi API key. Use an environment variable rather than committing it to source control.    |
| `engine`  |  None   | The search engine used by default, such as google or google_maps.                                 |
| `async`   |  false  | Submits searches without waiting for them to complete. It can be set on the client or per search. |

Search-engine-specific parameters can also be supplied when creating the client or calling `search`. Parameters passed to search override client defaults.

### Search Asynchronous
Search API features non-blocking search using the option: `async=true`.

- Non-blocking - `async=true` - a single thread can submit many searches without waiting for each one to complete, then collects results later from the Search Archive API.
- Blocking - `async=false` - each search call blocks the calling thread until results are ready. To run searches concurrently, you'd need multiple threads (i.e. through an `ExecutorService`), each holding its own connection open for the duration of its search. This is more I/O-intensive, since concurrency requires as many held-open connections as concurrent searches.

Here is an example simulation of asynchronous searches using Java:
 
 ```java
String apiKey = System.getenv("SERPAPI_KEY");

Map<String, String> auth = new HashMap<>();
auth.put("engine", "google");
auth.put("api_key", apiKey);
auth.put("async", "true");
SerpApi client = new SerpApi(auth);

Queue<String> ids = new LinkedList<>();

for (String company : new String[]{"meta", "amazon", "apple", "netflix", "google"}) {
    Map<String, String> parameter = new HashMap<>();
    parameter.put("q", company);

    JsonObject result = client.search(parameter);
    ids.add(result.getAsJsonObject("search_metadata").get("id").getAsString());
}

System.out.println("waiting 10s for searches to complete...");
// for production use cases, continuous polling instead of waiting is necessary
Thread.sleep(10000);

while (!ids.isEmpty()) {
    JsonObject archived = client.searchArchive(ids.poll());
    String status = archived.getAsJsonObject("search_metadata").get("status").getAsString();
    String company = archived.getAsJsonObject("search_parameters").get("q").getAsString();
    System.out.println(company + ": " + status);
}
```

This code shows a simple solution to batch searches asynchronously into a [queue](https://en.wikipedia.org/wiki/Queue_(abstract_data_type)). 

Each search may take a few seconds to complete. By the time the first element pops out of the queue, the search results might already be available in the archive. 

If not, the `searchArchive` method blocks until the search results are available.

## Examples

Here are some examples for some of our most popular APIs. You can find the full list of supported engines and parameters in our [documentation](https://serpapi.com/search-engine-apis).

### Google Shopping

Scrape Google Shopping results with product names, prices, ratings, and merchant information.

```java
String apiKey = System.getenv("SERPAPI_KEY");
String engine = "google_shopping";

Map<String, String> auth = new HashMap<>();
auth.put("engine", engine);
auth.put("api_key", apiKey);
SerpApi client = new SerpApi(auth);

Map<String, String> parameter = new HashMap<>();
parameter.put("q", "Macbook M4");

try {
    JsonObject data = client.search(parameter);
    JsonArray results = data.getAsJsonArray("shopping_results");
    if (results != null && results.size() > 0) {
        JsonObject first = results.get(0).getAsJsonObject();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(first));
    }
} catch (SerpApiException e) {
        e.printStackTrace();
        System.exit(1);
}
```

Source code: [src/test/java/serpapi/example/GoogleShoppingTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/example/GoogleShoppingTest.java)

[See documentation](https://serpapi.com/google-shopping-api)

#### Google Shopping Light

A [light variant](https://serpapi.com/google-shopping-light-api) engine called `google_shopping_light` is also available for faster, lower-cost shopping searches.

### Google Images

Scrape Google Images search results, including image URLs, thumbnails, titles, and source pages.

```java
String apiKey = System.getenv("SERPAPI_KEY");
String engine = "google_images";

Map<String, String> auth = new HashMap<>();
auth.put("engine", engine);
auth.put("api_key", apiKey);
SerpApi client = new SerpApi(auth);

Map<String, String> parameter = new HashMap<>();
parameter.put("q", "coffee");

try {
    JsonObject data = client.search(parameter);
    JsonArray results = data.getAsJsonArray("images_results");
    if (results != null && results.size() > 0) {
        JsonObject first = results.get(0).getAsJsonObject();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(first));
    }   
} catch (SerpApiException e) {
    e.printStackTrace();
    System.exit(1);
}
```

Source code: [src/test/java/serpapi/example/GoogleImagesTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/example/GoogleImagesTest.java)

[See documentation](https://serpapi.com/images-results)

#### Google Images Light

A [light variant](https://serpapi.com/google-images-light-api) engine called `google_images_light` is also available for faster, lower-cost image searches.

### Google Lens 

Scrape results from the Google Lens page when performing an image search. The results related to the image could contain visual matches and other data.

```java
String apiKey = System.getenv("SERPAPI_KEY");
String engine = "google_lens";

Map<String, String> auth = new HashMap<>();
auth.put("engine", engine);
auth.put("api_key", apiKey);
SerpApi client = new SerpApi(auth);

Map<String, String> parameter = new HashMap<>();
parameter.put("url", "https://i.imgur.com/your_image.png");

try {
    JsonObject data = client.search(parameter);
    JsonArray visualMatches = data.getAsJsonArray("visual_matches");
    System.out.println(visualMatches);
} catch (SerpApiException e) {
    e.printStackTrace();
    System.exit(1);
}
```

Source code: [src/test/java/serpapi/example/GoogleLensTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/example/GoogleLensTest.java)

[See Image API documentation](https://serpapi.com/image-api) · [See Google Lens image upload documentation](https://serpapi.com/google-lens-upload-an-image)

### Google Trends

Track search interest over time and compare the popularity of search terms.

```java
String apiKey = System.getenv("SERPAPI_KEY");
String engine = "google_trends";

Map<String, String> auth = new HashMap<>();
auth.put("engine", engine);
auth.put("api_key", apiKey);
SerpApi client = new SerpApi(auth);

Map<String, String> parameter = new HashMap<>();
parameter.put("q", "coffee");
parameter.put("data_type", "TIMESERIES");

try {
    JsonObject data = client.search(parameter);
    JsonObject interestOverTime = data.getAsJsonObject("interest_over_time");

    if (interestOverTime != null) {
        JsonArray timelineData = interestOverTime.getAsJsonArray("timeline_data");

        if (timelineData != null && timelineData.size() > 0) {
            JsonObject first = timelineData.get(0).getAsJsonObject();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            System.out.println(gson.toJson(first));
        }
    }
} catch (SerpApiException e) {
    e.printStackTrace();
    System.exit(1);
}
```

Source code: [src/test/java/serpapi/example/GoogleTrendsTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/example/GoogleTrendsTest.java)

[See documentation](https://serpapi.com/google-trends-api)

### Google Flights
Search flight routes, schedules, prices, and booking options.

_Note: The `google_flights` engine does not use `q`. Specify route and date parameters such as `departure_id`, `arrival_id`, `outbound_date`, and `return_date`._

```java
String apiKey = System.getenv("SERPAPI_KEY");
String engine = "google_flights";

Map<String, String> auth = new HashMap<>();
auth.put("engine", engine);
auth.put("api_key", apiKey);
SerpApi client = new SerpApi(auth);

String outboundDate = LocalDate.now().plusDays(7).toString();
String returnDate = LocalDate.now().plusDays(14).toString();

Map<String, String> parameter = new HashMap<>();
parameter.put("departure_id", "LAX");
parameter.put("arrival_id", "AUS");
parameter.put("outbound_date", outboundDate);
parameter.put("return_date", returnDate);

try {
    JsonObject data = client.search(parameter);
    JsonArray bestFlights = data.getAsJsonArray("best_flights");
    //JsonArray otherFlights = data.getAsJsonArray("other_flights");
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    System.out.println(gson.toJson(bestFlights));
} catch (SerpApiException e) {
    e.printStackTrace();
    System.exit(1);
}
```
Source code: [src/test/java/serpapi/example/GoogleFlightsTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/example/GoogleFlightsTest.java)

[See documentation](https://serpapi.com/google-flights-api)

### Google AI Mode API
The Google AI Mode API returns AI-generated answers with structured text blocks, references, images, products, and more.

```java
String apiKey = System.getenv("SERPAPI_KEY");
String engine = "google_ai_mode";

Map<String, String> auth = new HashMap<>();
auth.put("engine", engine);
auth.put("api_key", apiKey);
SerpApi client = new SerpApi(auth);

Map<String, String> parameter = new HashMap<>();
parameter.put("q", "best coffee maker");

try {
    String markdownData = client.markdown(parameter);
    System.out.println(markdownData);
} catch (SerpApiException e) {
    e.printStackTrace();
    System.exit(1);
}
```

Source code: [src/test/java/serpapi/example/GoogleAIModeTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/example/GoogleAIModeTest.java)

[See documentation](https://serpapi.com/google-ai-mode-api)

### Bing Search

Scrape Bing web search results, including organic results, ads, related searches, and more.

```java
String apiKey = System.getenv("SERPAPI_KEY");
String engine = "bing";

Map<String, String> auth = new HashMap<>();
auth.put("api_key", apiKey);
auth.put("engine", engine);
SerpApi client = new SerpApi(auth);

Map<String, String> parameter = new HashMap<>();
parameter.put("q", "coffee");
try {
    JsonObject results = client.search(parameter);
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    System.out.println(gson.toJson(results));
} catch (SerpApiException e) {
    e.printStackTrace();
    System.exit(1);
}
```

Source code: [src/test/java/serpapi/example/BingTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/example/BingTest.java)

[See documentation](https://serpapi.com/bing-search-api)

### DuckDuckGo Search

Scrape DuckDuckGo search results, including organic results, ads, knowledge graphs, and related searches.

```java
String apiKey = System.getenv("SERPAPI_KEY");
String engine = "duckduckgo";

Map<String, String> auth = new HashMap<>();
auth.put("api_key", apiKey);
auth.put("engine", engine);
SerpApi client = new SerpApi(auth);

Map<String, String> parameter = new HashMap<>();
parameter.put("q", "coffee");
try {
    JsonObject results = client.search(parameter);
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    System.out.println(gson.toJson(results));
} catch (SerpApiException e) {
    e.printStackTrace();
    System.exit(1);
}
```

Source code: [src/test/java/serpapi/example/DuckduckgoTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/example/DuckduckgoTest.java)

[See documentation](https://serpapi.com/duckduckgo-search-api)

### Baidu Search

Scrape Baidu search results, including organic results, answer boxes, and related searches.

```java
String apiKey = System.getenv("SERPAPI_KEY");
String engine = "baidu";

Map<String, String> auth = new HashMap<>();
auth.put("api_key", apiKey);
auth.put("engine", engine);
SerpApi client = new SerpApi(auth);

Map<String, String> parameter = new HashMap<>();
parameter.put("q", "coffee");
try {
    JsonObject results = client.search(parameter);
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    System.out.println(gson.toJson(results));
} catch (SerpApiException e) {
    e.printStackTrace();
    System.exit(1);
}
```

Source code: [src/test/java/serpapi/example/BaiduTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/example/BaiduTest.java)

[See documentation](https://serpapi.com/baidu-search-api)

### Amazon Search

Scrape Amazon product search results, including product names, prices, ratings, reviews, and availability.

_Note: The `amazon` engine uses the `k` parameter for a keyword search, not `q`._

```java
String apiKey = System.getenv("SERPAPI_KEY");
String engine = "amazon";

Map<String, String> auth = new HashMap<>();
auth.put("api_key", apiKey);
auth.put("engine", engine);
SerpApi client = new SerpApi(auth);

Map<String, String> parameter = new HashMap<>();
parameter.put("k", "coffee");
parameter.put("amazon_domain", "amazon.com");

try {
    JsonObject data = client.search(parameter);
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    System.out.println(gson.toJson(data));
} catch (SerpApiException e) {
    e.printStackTrace();
    System.exit(1);
}
```
Source code: [src/test/java/serpapi/example/AmazonSearchTest.java](https://github.com/serpapi/serpapi-java/blob/master/src/test/java/serpapi/example/AmazonSearchTest.java)

[See documentation](https://serpapi.com/amazon-search-api)


## Migration from google-search-results-java

If you are upgrading from the legacy [`google-search-results-java`](https://github.com/serpapi/google-search-results-java) library, here is a summary of what changed.

### Dependency

```gradle
// before
implementation 'com.github.serpapi:google-search-results-java:2.0.0'

// after
implementation 'com.github.serpapi:serpapi-java:1.2.0'
```

### Class and method renames

| Old (`google-search-results-java`) | New (`serpapi-java`)                                                                 |
| ---------------------------------- | ------------------------------------------------------------------------------------ |
| `GoogleSearch`                     | `SerpApi`                                                                            |
| `SerpApiSearch`                    | `SerpApi`                                                                            |
| `client.getJson()`                 | `client.search(parameter)`                                                           |
| `client.getHtml()`                 | `client.html(parameter)`                                                             |
| —                                  | `client.markdown(parameter)` — new in 1.2.0                                          |
| `client.getSearchArchive(id)`      | `client.searchArchive(id)`                                                           |
| `client.getAccount()`              | `client.account()`                                                                   |
| `client.getLocation(parameter)`    | `client.location(parameter)`                                                         |
| `SerpApiSearchException`           | `SerpApiException`                                                                   |

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

## Documentation

SerpApi supports Google Search, Google Maps, Google Shopping, Baidu, Yandex, Yahoo, eBay, Apple App Store, and many other APIs. Browse the [SerpApi](https://serpapi.com/search-api) documentation to find supported APIs and parameters, or use the [Playground](https://serpapi.com/playground) to build a request and generate code.

Additional SDK resources:

- [Java SDK integration page](https://serpapi.com/integrations/java)
- [Java package](https://github.com/serpapi/serpapi-java)
- [SerpApi status](https://serpapi.com/status)

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

## Inspiration
 * https://www.baeldung.com/java-http-request
 * https://github.com/google/gson

## Contributing

Contributions are welcome. Make sure to read our [contributing guide](https://github.com/serpapi/serpapi-java/blob/master/CONTRIBUTING.md) 

© 2026 [SerpApi](https://serpapi.com/)