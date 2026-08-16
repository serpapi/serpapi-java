package serpapi;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * HTTPS client for Serp API
 */
public class SerpApiHttp {
  // http request configuration
  private int httpConnectionTimeout = 60000;
  private int httpReadTimeout = 60000;

  /**
   * current API version
   */
  public static String VERSION = "1.2.0";

  /**
   * backend service
   */
  public static String BACKEND = "https://serpapi.com";

  /**
   * initialize gson
   */
  private static Gson gson = new Gson();

  /**
   * current backend HTTP path
   */
  public String path;

  /**
   * HTTP client
   */
  private HttpClient httpClient;

  /***
   * Constructor
   * @param path HTTP url path
   */
  public SerpApiHttp(String path) {
    this.path = path;
    this.httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofMillis(httpConnectionTimeout))
        .build();
  }

  /***
   * Returns HTTP response content as a raw String
   *
   * @param parameter user client parameters
   * @return http response body
   * @throws SerpApiException wraps error message
   */
  public String get(Map<String, String> parameter) throws SerpApiException {
    String query;
    try {
      query = ParameterStringBuilder.getParamsString(parameter);
    } catch (UnsupportedEncodingException e) {
      throw new SerpApiException(e);
    }

    URI uri = URI.create(BACKEND + path + "?" + query);

    HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
        .uri(uri)
        .timeout(Duration.ofMillis(httpReadTimeout))
        .GET();

    String outputFormat = parameter.get("output");
    if (outputFormat != null && outputFormat.startsWith("json")) {
      requestBuilder.header("Content-Type", "application/json");
    }

    try {
      HttpResponse<String> response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
      
      if (response.statusCode() != 200) {
        triggerSerpApiException(response.body());
      }
      return response.body();
    } catch (IOException | InterruptedException e) {
      throw new SerpApiException(e);
    }
  }

  /**
   * trigger a exception on error
   * @param content raw JSON response from serpapi.com
   * @throws SerpApiException wraps error message
   */
  protected void triggerSerpApiException(String content) throws SerpApiException {
    String errorMessage;
    try {
      JsonObject element = gson.fromJson(content, JsonObject.class);
      errorMessage = element.get("error").getAsString();
    } catch (Exception e) {
      throw new SerpApiException("invalid response format: " + content);
    }
    throw new SerpApiException(errorMessage);
  }

  /**
    * Get the HTTP connection timeout
    * 
    * @return current HTTP connection timeout
    */
  public int getHttpConnectionTimeout() {
    return httpConnectionTimeout;
  }

  /**
   * Set the HTTP connection timeout
   * 
   * @param httpConnectionTimeout set HTTP connection timeout
   */
  public void setHttpConnectionTimeout(int httpConnectionTimeout) {
    this.httpConnectionTimeout = httpConnectionTimeout;
    // Recreate client with new timeout
    this.httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofMillis(httpConnectionTimeout))
        .build();
  }

  /**
   * Get the HTTP read timeout
   * 
   * @return current HTTP read timeout
   */
  public int getHttpReadTimeout() {
    return httpReadTimeout;
  }

  /**
   * Set the HTTP read timeout
   * 
   * @param httpReadTimeout set HTTP read timeout
   */
  public void setHttpReadTimeout(int httpReadTimeout) {
    this.httpReadTimeout = httpReadTimeout;
  }

}
