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
public class SerpApi extends Exception {

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

  /* default HTTP client timeout */
  public Integer timeout = 60000;

  /***
   * Constructor
   *
   * @param parameter default search parameter should include {"api_key": "secret_api_key", "engine": "google" }
   */
  public SerpApi(Map<String, String> parameter) {
    this.parameter = parameter;
  }

  /***
   * Constructor
   * 
   * @param parameter default search parameter should include {"api_key": "secret_api_key", "engine": "google" }
   */
  public SerpApi() {
    this.parameter = new HashMap();
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
   * Returns location 
   * 
   * @param parameter must include {q: "city", limit: 3}
   * @return JsonObject location using Location API
   * @throws SerpApiException wraps backend error message
   */
  public JsonObject location(Map<String, String> parameter) throws SerpApiException {
    return json("/locations.json", parameter);
  }

  /***
   * Get client result from the Client Archive API
   * 
   * @param clientID archived client result = client_metadata.id
   * @return JsonObject client result
   * @throws SerpApiException wraps backend error message
   */
  public JsonObject searchArchive(String clientID) throws SerpApiException {
    if(!this.parameter.containsKey("api_key")) {
      throw new SerpApiException("api_key must be set in the default parameter passed to the constructor!");
    }
    return json("/searches/" + clientID + ".json", null);
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
   * @param parameter Map including the api_key if not set in the default client parameter
   * @return JsonObject account information
   * @throws SerpApiException wraps backend error message
   */
  public JsonObject account() throws SerpApiException {
    return json("/account.json", this.parameter);
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
   * @return format parameter hash map
   * @throws SerpApiException wraps backend error message
   */
  public String get(String path, String output, Map<String, String> parameter) throws SerpApiException {
    // Initialize client if not done
    if (this.client == null) {
      this.client = new SerpApiHttp(path);
      this.client.setHttpConnectionTimeout(this.timeout);
    } else {
      this.client.path = path;
    }

    Map<String, String> query = new HashMap();
    // Merge default parameter
    query.putAll(this.parameter);

    // Merge query parameter
    query.putAll(parameter);

    // Set current programming language
    query.put("source", "java");

    // Set output format
    query.put("output", output);

    return this.client.get(query);
  }

}
