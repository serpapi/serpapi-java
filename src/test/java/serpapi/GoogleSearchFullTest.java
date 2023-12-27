package serpapi;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test main class
 */
public class GoogleSearchFullTest {

  GoogleSearch search;

  @Before
  public void setUp() throws Exception {
    if (System.getenv("API_KEY") != null) {
      GoogleSearch.api_key_default = System.getenv("API_KEY");
    }
  }

  @Test
  public void buildParameter() throws SerpApiException {
    Map<String, String> parameter = new HashMap<>();
    parameter.put("q", "Coffee");
    parameter.put("location", "Austin, Texas");
    client = new GoogleSearch(parameter);
    client.buildQuery("/search", "html");
    assertEquals(client.parameter.get("source"), "java");
    assertEquals(client.parameter.get(GoogleSearch.API_KEY_NAME), GoogleSearch.api_key_default);
  }

  @Test
  public void builParameterForInstance() throws SerpApiException {
    GoogleSearch client = new GoogleSearch();
    client.buildQuery("/search", "json");
    assertEquals(client.parameter.get("source"), "java");
    assertEquals(client.parameter.get("output"), "json");
    assertEquals(client.parameter.get(GoogleSearch.API_KEY_NAME), GoogleSearch.api_key_default);
  }

  @Test
  public void getApiKey() throws Exception {
    GoogleSearch.api_key_default = "abc";
    assertEquals("abc", GoogleSearch.getApiKey());
  }

  @Test
  public void asJson() throws Exception {
    GoogleSearch client = new GoogleSearch();
    JsonObject expectation = new JsonObject();
    expectation.add("status", new JsonPrimitive("ok"));
    assertEquals(expectation, client.asJson("{\"status\": \"ok\"}"));
  }

  @Test
  public void getHtml() throws Exception {
    SerpApiHttp client = mock(SerpApiHttp.class);

    String htmlContent = ReadJsonFile.readAsString(Paths.get("src/test/java/serpapi/data/search_coffee_sample.html"));
    when(client.getResults(ArgumentMatchers.<String, String>anyMap())).thenReturn(htmlContent);

    Map<String, String> parameter = new HashMap<>();
    parameter.put("q", "Coffee");
    parameter.put("location", "Austin, Texas");
    GoogleSearch result = new GoogleSearch(parameter);
    result.client = search;

    assertEquals(htmlContent, result.getHtml());
  }

  @Test
  public void getJson() throws Exception {
    Map<String, String> parameter = new HashMap<>();
    parameter.put("q", "Coffee");
    parameter.put("location", "Austin, Texas");

    SerpApiHttp stub = mock(SerpApiHttp.class);
    when(stub.getResults(ArgumentMatchers.<String, String>anyMap())).thenReturn(
        ReadJsonFile.readAsJson(Paths.get("src/test/java/serpapi/data/search_coffee_sample.json")).toString());

    GoogleSearch client = new GoogleSearch(parameter);
    client.client = stub;

    assertEquals(3, client.getJson().getAsJsonArray("local_results").size());
  }

  @Test
  public void searchCoffee() throws SerpApiException {
    // skip test if no api_key provided
    if (System.getenv("API_KEY") == null)
      return;

    Map<String, String> parameter = new HashMap<>();
    parameter.put("q", "Coffee");
    parameter.put("location", "Austin, Texas, United States");
    parameter.put("hl", "en");
    parameter.put("gl", "us");
    parameter.put("google_domain", "google.com");
    // parameter.put("api_key", "demo");
    parameter.put("safe", "active");
    parameter.put("start", "10");
    parameter.put("num", "10");
    parameter.put("device", "desktop");

    GoogleSearch result = new GoogleSearch(parameter);
    JsonObject results = result.getJson();
    assertTrue(results.getAsJsonArray("organic_results").size() > 5);
  }

}
