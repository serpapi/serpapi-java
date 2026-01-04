package serpapi;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Test main class
 */
public class SearchArchiveApiTest {


  @Test
  public void searchArchive() throws SerpApiException, InterruptedException {
    // skip test if no api_key provided
    if(System.getenv("SERPAPI_KEY") == null)
      return;

    Map<String, String> parameter = new HashMap<>();
    parameter.put("api_key", System.getenv("SERPAPI_KEY"));
    parameter.put("q", "Coffee");
    parameter.put("location", "Austin, Texas, United States");
    parameter.put("hl", "en");
    parameter.put("gl", "us");
    parameter.put("google_domain", "google.com");
    parameter.put("safe", "active");
    parameter.put("start", "10");
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
