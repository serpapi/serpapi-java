package serpapi.example;
import serpapi.*;

import com.google.gson.JsonObject;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Test Google Shopping has shopping results
 */
public class GoogleShoppingTest {

  @Test
  public void search() throws SerpApiException {
    // skip test if no api_key provided
    if(System.getenv("API_KEY") == null)
      return;

    // setup serpapi client
    Map<String, String> auth = new HashMap<>();
    auth.put("api_key", System.getenv("SERPAPI_KEY"));
    SerpApi client = new SerpApi(auth);

    // run search
    Map<String, String> parameter = new HashMap<>();
    parameter.put("engine", "google_shopping");
    parameter.put("q", "Macbook M4");
    JsonObject results = client.search(parameter);
    assertTrue(results.getAsJsonArray("shopping_results").size() > 1);
  }

}