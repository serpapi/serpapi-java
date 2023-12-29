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
public class GoogleMapsTest {

  @Test
  public void search() throws SerpApiException {
    // skip test if no api_key provided
    if(System.getenv("API_KEY") == null)
      return;

    // set search context
    Map<String, String> parameter = new HashMap<>();
    parameter.put("engine", "google_maps");
    parameter.put("api_key", System.getenv("API_KEY"));

    SerpApi serpapi = new SerpApi(parameter);

    // set search parameter
parameter.put("q", "pizza");
parameter.put("ll", "@40.7455096,-74.0083012,15.1z");
parameter.put("type", "search");

    JsonObject results = serpapi.search(parameter);
    assertTrue(results.getAsJsonArray("local_results").size() > 5);
  }

}