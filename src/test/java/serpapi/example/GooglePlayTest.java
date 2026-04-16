package serpapi.example;
import serpapi.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Test main class
 */
public class GooglePlayTest {

  @Test
  public void search() throws SerpApiException {
    // skip test if no api_key provided
    if(System.getenv("SERPAPI_KEY") == null)
      return;

    // setup serpapi client
    Map<String, String> auth = new HashMap<>();
    auth.put("api_key", System.getenv("SERPAPI_KEY"));
    SerpApi client = new SerpApi(auth);

    // run search
    Map<String, String> parameter = new HashMap<>();
    parameter.put("engine", "google_play");
    parameter.put("q", "kite");
    parameter.put("store", "apps");
    JsonObject results = client.search(parameter);
    JsonArray sections = results.getAsJsonArray("organic_results");
    int appCount = 0;
    for (JsonElement section : sections) {
      JsonObject sectionObj = section.getAsJsonObject();
      if (sectionObj.has("items") && sectionObj.get("items").isJsonArray()) {
        appCount += sectionObj.getAsJsonArray("items").size();
      }
    }
    assertTrue(appCount > 1);
  }

}