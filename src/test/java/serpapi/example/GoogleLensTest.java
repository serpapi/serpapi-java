package serpapi.example;
import serpapi.*;

import com.google.gson.JsonObject;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Test Google Lens has visual matches
 */
public class GoogleLensTest {

  @Test
  public void search() throws SerpApiException {
    // skip test if no api_key provided
    if(System.getenv("SERPAPI_KEY") == null)
      return;

    // setup serpapi client
    Map<String, String> auth = new HashMap<>();
    auth.put("api_key", System.getenv("API_KEY"));
    SerpApi client = new SerpApi(auth);

    // run search
    Map<String, String> parameter = new HashMap<>();
    parameter.put("engine", "google_lens");
    parameter.put("url", "https://i.imgur.com/HBrB8p0.png");
    JsonObject results = client.search(parameter);
    assertTrue(results.getAsJsonArray("visual_matches").size() > 1);
  }

}