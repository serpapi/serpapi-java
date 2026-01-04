package serpapi;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Test SerpApi.search() method.
 */
public class SearchApiTest {

  @Test
  public void search() throws SerpApiException, InterruptedException {
    // skip test if no api_key provided
    if(System.getenv("SERPAPI_KEY") == null) {
      fail("SERPAPI_KEY is not set");
    }

    Map<String, String> auth = new HashMap<>();
    auth.put("api_key", System.getenv("SERPAPI_KEY"));
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
    assertTrue(results.getAsJsonArray("organic_results").size() > 5);
  }

}
