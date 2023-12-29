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
public class SerpApiTest {

  @Test
  public void search() throws SerpApiException, InterruptedException {
    // skip test if no api_key provided
    if(System.getenv("API_KEY") == null)
      return;

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
    assertTrue(results.getAsJsonArray("organic_results").size() > 5);
  }

  @Test
  public void searchArchive() throws SerpApiException, InterruptedException {
    // skip test if no api_key provided
    if(System.getenv("API_KEY") == null)
      return;

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
    assertTrue(results.getAsJsonArray("organic_results").size() > 5);

    // now search in the archive
    String id = results.getAsJsonObject("search_metadata").getAsJsonPrimitive("id").getAsString();
    assertNotNull(id);
    System.out.println("id: " + id);
    // wait a bit for cache propagation
    Thread.sleep(100);
    // retrieve search from the archive for free
    JsonObject archive = serpapi.searchArchive(id);
    // compare id and the content should be the same
    assertEquals(id, archive.getAsJsonObject("search_metadata").getAsJsonPrimitive("id").getAsString());
  }

}
