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
public class GoogleReverseImageTest {

  @Test
  public void search() throws SerpApiException {
    // skip test if no api_key provided
    if(System.getenv("API_KEY") == null)
      return;

    // set search context
    Map<String, String> parameter = new HashMap<>();
    parameter.put("engine", "google_reverse_image");
    parameter.put("api_key", System.getenv("API_KEY"));

    SerpApi serpapi = new SerpApi(parameter);

    // set search parameter
parameter.put("image_url", "https://i.imgur.com/5bGzZi7.jpg");
parameter.put("max_results", "1");

    JsonObject results = serpapi.search(parameter);
    assertTrue(results.getAsJsonArray("image_sizes").size() > 1);
  }

}