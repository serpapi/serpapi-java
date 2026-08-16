package serpapi.example;
import serpapi.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Test main class
 */
public class GoogleEventsTest {

  // Quarantined: the google_events engine returns no results at all. The API
  // responds HTTP 200 with search_metadata.status "Success", but the body
  // carries:
  //
  //   "search_information": { "events_results_state": "Fully empty" },
  //   "error": "Google hasn't returned any results for this query."
  //
  // Reproduced across two different queries ("coffee", "Events in Austin, TX")
  // two days apart, so this is the engine, not the query or this client.
  // Re-enable once the engine returns events again.
  @Ignore("google_events engine returns 'Fully empty' for all queries; not a client bug")
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