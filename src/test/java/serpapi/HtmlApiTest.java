package serpapi;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Test SerpApi.html() method.
 *
 * Offline: the HTTP client is stubbed, so no network call and no SERPAPI_KEY
 * are needed. What matters here is the request this client builds, which can
 * be asserted without a live backend.
 */
public class HtmlApiTest {

  /**
   * Stubbed HTTP client that records the query it was asked to send.
   */
  private static class RecordingHttp extends SerpApiHttp {
    Map<String, String> recorded;

    RecordingHttp() {
      super("/search");
    }

    @Override
    public String get(Map<String, String> parameter) {
      this.recorded = parameter;
      return "<html><body>coffee</body></html>";
    }
  }

  @Test
  public void htmlRequestsHtmlOutputFromSearchEndpoint() throws SerpApiException {
    SerpApi serpapi = new SerpApi(new HashMap<>());
    RecordingHttp http = new RecordingHttp();
    serpapi.client = http;

    Map<String, String> parameter = new HashMap<>();
    parameter.put("q", "coffee");
    String content = serpapi.html(parameter);

    assertEquals("html", http.recorded.get("output"));
    assertEquals("coffee", http.recorded.get("q"));
    assertEquals("/search", http.path);
    assertTrue(content.startsWith("<html>"));
  }
}
