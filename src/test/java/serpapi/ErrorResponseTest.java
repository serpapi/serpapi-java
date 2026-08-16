package serpapi;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Test that an error reported in the body of an HTTP 200 response is raised as
 * a SerpApiException rather than handed back to the caller as a result.
 */
public class ErrorResponseTest {

  /**
   * Stubbed HTTP client returning a canned body, so these tests never reach the network.
   */
  private static class StubHttp extends SerpApiHttp {
    private final String body;

    StubHttp(String body) {
      super("/search");
      this.body = body;
    }

    @Override
    public String get(Map<String, String> parameter) {
      return body;
    }
  }

  // Recorded from the google_events engine: HTTP 200, status "Success", no results.
  private static final String EMPTY_EVENTS = "{"
      + "\"search_metadata\":{\"status\":\"Success\"},"
      + "\"search_information\":{\"events_results_state\":\"Fully empty\"},"
      + "\"error\":\"Google hasn't returned any results for this query.\"}";

  private static final String ORGANIC_RESULTS =
      "{\"search_metadata\":{\"status\":\"Success\"},\"organic_results\":[{\"position\":1}]}";

  private static SerpApi clientReturning(String body) {
    SerpApi serpapi = new SerpApi(new HashMap<>());
    serpapi.client = new StubHttp(body);
    return serpapi;
  }

  @Test
  public void searchRaisesOnErrorInBody() {
    try {
      clientReturning(EMPTY_EVENTS).search(new HashMap<>());
      fail("expected SerpApiException for a 200 response carrying an error field");
    } catch (SerpApiException e) {
      assertEquals("Google hasn't returned any results for this query.", e.getMessage());
    }
  }

  @Test
  public void searchReturnsResultsWhenBodyHasNoError() throws SerpApiException {
    JsonObject results = clientReturning(ORGANIC_RESULTS).search(new HashMap<>());
    assertEquals(1, results.getAsJsonArray("organic_results").size());
  }

  @Test
  public void accountRaisesOnErrorInBody() {
    try {
      clientReturning("{\"error\":\"Invalid API key.\"}").account();
      fail("expected SerpApiException for a 200 response carrying an error field");
    } catch (SerpApiException e) {
      assertEquals("Invalid API key.", e.getMessage());
    }
  }

  @Test
  public void locationRaisesOnErrorInBody() {
    try {
      clientReturning("{\"error\":\"Invalid API key.\"}").location(new HashMap<>());
      fail("expected SerpApiException instead of a cast failure on the error object");
    } catch (SerpApiException e) {
      assertEquals("Invalid API key.", e.getMessage());
    }
  }

  @Test
  public void locationReturnsArrayWhenBodyHasNoError() throws SerpApiException {
    JsonArray locations = clientReturning("[{\"id\":\"austin\"}]").location(new HashMap<>());
    assertEquals(1, locations.size());
  }
}
