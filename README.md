
# SerpApi Java Library

[![serpapi-java](https://github.com/serpapi/serpapi-java/actions/workflows/gradle.yml/badge.svg)](https://github.com/serpapi/serpapi-java/actions/workflows/gradle.yml)
[![JitPack](https://jitpack.io/v/serpapi/serpapi-java.svg)](https://jitpack.io/#serpapi/serpapi-java)

Integrate search data into your AI workflow, RAG, fine-tuning, or Java application using this official [SerpApi Java SDK](https://serpapi.com/integrations/java).

[SerpApi](https://serpapi.com/) supports Google, Google Maps, Google Shopping, Baidu, Yandex, Yahoo, eBay, App Stores, and many more.

## Installation 

Using Maven / Gradle.

Edit your `build.gradle` file:
```gradle
repositories {
    maven { url "https://jitpack.io" }
}

dependencies {
    implementation 'com.github.serpapi:serpapi-java:1.2.0'
}
```

To list all available versions:
https://jitpack.io/api/builds/com.github.serpapi/serpapi-java

or you can download the jar file from https://github.com/serpapi/serpapi-java/releases

Note: JitPack builds Maven artifacts from GitHub releases and tags.

## Quickstart

[Create a SerpApi account](https://serpapi.com/dashboard) to get your API key, then store it in an environment variable:

```bash export SERPAPI_KEY="your_api_key"```

To try the library quickly, use the demo project:
```bash
git clone https://github.com/serpapi/serpapi-java.git
cd serpapi-java/demo
make all SERPAPI_KEY='<your private key>'
```
Use quotes if your key contains shell-special characters. You need a SerpApi account to obtain a key: https://serpapi.com/dashboard

`demo/src/main/java/demo/App.java`:
```java
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

## Features
TODO some links are dead find new equivalents

- [Asynchronous searches]() for submitting non-blocking jobs and retrieving completed results from the Search Archive API.
- [Persistent connections and connection pooling]() for reusing HTTP connections across searches.
- Search results as Gson JsonObject / JsonArray  responses with `search`, token-efficient Markdown with `md`, or raw search-engine HTML with `html`.
- SDK methods for the [Image API](https://serpapi.com/image-api), [Location API](https://serpapi.com/locations-api), [Search Archive API](https://serpapi.com/search-archive-api), and [Account API](https://serpapi.com/account-api).
- Configurable HTTP timeouts and symbolized or string JSON keys.

## Response Formats

Use `search` for structured results decoded into a  `Gson JsonObject / JsonArray`:

```java results = todo add ```

Use `md` for a token-efficient Markdown String optimized for LLMs and AI agents:

```java markdown = todo add ```

Use `html` when you need the raw search-engine response:

```java raw_html = todo add```

Archived results are also available as Markdown with `client.search_archive(search_id, :md)`.

Learn more about [SerpApi Markdown output](https://serpapi.com/markdown-output).


## Requirements

TODO check if still applicable 

This library uses [Gson](https://github.com/google/gson) for JSON and returns responses as Gson `JsonObject` / `JsonArray`.

**This repository** is built and tested with **JDK 21** and the **Gradle wrapper** (`./gradlew`, currently Gradle 8.5). Use the wrapper so you do not need a separate Gradle install.

**Consumers** of the JitPack artifact should run a JVM whose version is at least the **bytecode level** of the release you depend on (releases from this branch target **Java 21**).

## Configuration

Set defaults when creating a client, then override search parameters in individual calls:
```java 
client = SerpApi::Client.new(
  api_key: ENV.fetch("SERPAPI_KEY"),
  engine: "google",
  hl: "en",
  gl: "us",
  persistent: true,
  timeout: 120
)

results = client.search(
  q: "coffee",
  gl: "gb",
  async: false,
  symbolize_names: true
)
```
TODO find out if this is supposed to be a table?
Optional Default Description 
`api_key` None Your SerpApi API key. Use an environment variable rather than committing it to source control. 

`engine` None The search engine used by default, such as google or google_maps. persistent true Reuses the HTTP connection between requests.


 Call `client.close` when finished. 
 `timeout `120 Timeout in seconds for non-persistent HTTP requests. 


 `async false` Submits searches without waiting for them to complete. It can be set on the client or per search. 


 `symbolize_names` true Returns JSON object keys as symbols. Pass false to a search to receive string keys.


Search-engine-specific parameters can also be supplied when creating the client or calling `search`. Parameters passed to search override client defaults.

### Search Asynchronous
Search API features non-blocking search using the option: `async=true`.

- Non-blocking - async=true - a single parent process can handle unlimited concurrent searches.
- Blocking - async=false - many processes must be forked and synchronized to handle concurrent searches. This strategy is I/O usage because each client would hold a network connection.

Search API enables `async` search.

- Non-blocking (`async=true`) : the development is more complex, but this allows handling many simultaneous connections.
- Blocking (`async=false`) : it is easy to write the code but more compute-intensive when the parent process needs to hold many connections.
Here is an example of asynchronous searches using Java
 TODO ADD 

source code: todo find in java repo

This code shows a simple solution to batch searches asynchronously into a [queue](https://en.wikipedia.org/wiki/Queue_(abstract_data_type)). Each search may take up to few seconds to complete. By the time the first element pops out of the queue, the search results might already be available in the archive. If not, the `search_archive` method blocks until the search results are available.

## Examples

Here are some examples for some of our most popular APIs. You can find the full list of supported engines and parameters in our [documentation](https://serpapi.com/search-engine-apis).

### Google Shopping

Scrape Google Shopping results with product names, prices, ratings, and merchant information.

TODO add code

[See Documentation]()

#### Google Shopping Light

TODO

[See Documentation]()

### Google Images

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

[See Documentation](https://serpapi.com/images-results)

#### Google Images Light

[See Documentation]()

### Google Lens with File Upload

[See Documentation]()

### Google Trends

[See Documentation]()

### Google Flights

[See Documentation]()

### Google AI Mode API

[See Documentation]()

### Bing Search

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

[See Documentation]()

### DuckDuckGo Search

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

[See Documentation]()

### Baidu Search

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

[See Documentation]()

### Amazon Search

[See Documentation]()


## Documentation

SerpApi supports Google Search, Google Maps, Google Shopping, Baidu, Yandex, Yahoo, eBay, Apple App Store, and many other APIs. Browse the [SerpApi](https://serpapi.com/search-api) documentation to find supported APIs and parameters, or use the [Playground](https://serpapi.com/playground) to build a request and generate code.

Additional SDK resources:

- [Java SDK integration page]()
- [Java SDK API reference]()
- [Java package]()
- [SerpApi status]()

## Performance

todo fill

### Key Takeaways
todo

### Contributing

Contributions are welcome. Make sure to read our [contributing guide](). 

© 2026 [SerpApi](https://serpapi.com/)