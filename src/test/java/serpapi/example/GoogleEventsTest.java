package serpapi.example;
import serpapi.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Test main class
 */
public class GoogleEventsTest {

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
    parameter.put("engine", "google_events");
    parameter.put("q", "Events in Austin, TX");
    JsonObject results = client.search(parameter);
    JsonArray events = results.getAsJsonArray("events_results");
    assertNotNull("no events_results in response: " + results, events);
    assertTrue(events.size() > 0);
  }

}