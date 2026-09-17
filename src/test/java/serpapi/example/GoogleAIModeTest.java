package serpapi.example;
import serpapi.*;

import com.google.gson.JsonObject;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Test Google AI Mode has a text_blocks response
 */
public class GoogleAIModeTest {

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
    parameter.put("engine", "google_ai_mode");
    parameter.put("q", "best coffee maker");
    JsonObject results = client.search(parameter);
    assertTrue(results.getAsJsonArray("text_blocks").size() > 0);
  }

}