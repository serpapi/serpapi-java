package serpapi;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;
import java.util.HashMap;

/**
 * SerpApi wraps HTTP interaction with the service serpapi.com
 */
public class SerpApi {

 /** 
  * client parameters
  */
  public Map<String, String> parameter;

  // initialize gson
  private static Gson gson = new Gson();

  /**
  * https client implementation for Java 7+
  */
  public SerpApiHttp client;

  /** 
   * default HTTP client timeout 
   */
  public Integer timeout = 60000;

  /***
   * Constructor
   *
   * @param parameter default search parameter should include {"api_key": "secret_api_key", "engine": "google" }
   */
  public SerpApi(Map<String, String> parameter) {
    this.parameter = parameter;
    this.client = new SerpApiHttp("/search");
    this.client.setHttpConnectionTimeout(this.timeout);
  }

  /***
   * Constructor without arguments
   */
  public SerpApi() {
    this.parameter = new HashMap();
    this.client = new SerpApiHttp("/search");
    this.client.setHttpConnectionTimeout(this.timeout);
  }

  /***
   * Returns HTML search results as a raw HTML String
   * 
   * @param parameter html search parameter
   * @return raw HTML response from the client engine for custom parsing
   * @throws SerpApiException wraps backend error message
   */
  public String html(Map<String, String> parameter) throws SerpApiException {
    return get("/client", "html", parameter);
  }

  /***
   * Returns search results as JSON object
   * 
   * @param parameter custom search parameter which override the default parameter provided in the constructor
   * @return JsonObject search results parent node
   * @throws SerpApiException wraps backend error message
   */
  public JsonObject search(Map<String, String> parameter) throws SerpApiException {
    return json("/search", parameter);
  }

  /***
   * Return location using Location API
   * 
   * @param parameter must include {q: "city", limit: 3}
   * @return JsonObject location using Location API
   * @throws SerpApiException wraps backend error message
   */
  public JsonArray location(Map<String, String> parameter) throws SerpApiException {
    String content = get("/locations.json", "json", parameter);    
    JsonElement element = gson.fromJson(content, JsonElement.class);
    return element.getAsJsonArray();
  }

  /***
   * Retrieve search result from the Search Archive API
   * 
   * @param id search unique identifier
   * @return JsonObject client result
   * @throws SerpApiException wraps backend error message
   */
  public JsonObject searchArchive(String id) throws SerpApiException {
    return json("/searches/" + id + ".json", null);
  }

  /***
   * Get account information using Account API
   * 
   * @param parameter Map including the api_key if not set in the default client parameter
   * @return JsonObject account information
   * @throws SerpApiException wraps backend error message
   */
  public JsonObject account(Map<String, String> parameter) throws SerpApiException {
    return json("/account.json", parameter);
  }

  /***
   * Get account information using Account API
   * 
   * @return JsonObject account information
   * @throws SerpApiException wraps backend error message
   */
  public JsonObject account() throws SerpApiException {
    return json("/account.json", null);
  }  

    /***
   * Convert HTTP content to JsonValue
   * 
   * @param content raw JSON HTTP response
   * @return JsonObject created by gson parser
   */
  private JsonObject json(String endpoint, Map<String, String> parameter) throws SerpApiException {
    String content = get(endpoint, "json", parameter);    
    JsonElement element = gson.fromJson(content, JsonElement.class);
    return element.getAsJsonObject();
  }

  /***
   * Get the HTTP client used for requests
   * 
   * @return http client
   */
  public SerpApiHttp getClient() {
    return this.client;
  }

  /***
   * Build a serp API query by expanding existing parameters
   *
   * @param path backend HTTP path
   * @param output type of output format (json, html, json_with_images)
   * @param parameter custom search parameter which override the default parameter provided in the constructor
   * @return format parameter hash map
   * @throws SerpApiException wraps backend error message
   */
  public String get(String path, String output, Map<String, String> parameter) throws SerpApiException {
    // Update path for the client
    this.client.path = path;

    // create HTTP query
    Map<String, String> query = new HashMap();

    if (path.startsWith("/searches")) {
      // Only preserve API_KEY
      query.put("api_key", this.parameter.get("api_key"));
    } else {
      // Merge default parameter
      query.putAll(this.parameter);
    }

    // override default parameter with custom parameter
    if (parameter != null) {
      query.putAll(parameter);
    }

    // Set current programming language
    query.put("source", "java");

    // Set output format
    query.put("output", output);

    return this.client.get(query);
  }

}
