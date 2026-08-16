package serpapi;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Test SerpApi.markdown() method.
 *
 * Offline: the HTTP client is stubbed, so no network call and no SERPAPI_KEY
 * are needed. What matters here is the request this client builds, which can
 * be asserted without a live backend.
 */
public class MarkdownApiTest {

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
      return "## Search results\n\n1. [Coffee](https://example.com)\n";
    }
  }

  @Test
  public void markdownRequestsMdOutput() throws SerpApiException {
    SerpApi serpapi = new SerpApi(new HashMap<>());
    RecordingHttp http = new RecordingHttp();
    serpapi.client = http;

    Map<String, String> parameter = new HashMap<>();
    parameter.put("q", "coffee");
    String content = serpapi.markdown(parameter);

    assertEquals("md", http.recorded.get("output"));
    assertEquals("coffee", http.recorded.get("q"));
    assertEquals("/search", http.path);
    assertTrue(content.startsWith("## Search results"));
  }

  @Test
  public void markdownMergesDefaultParameter() throws SerpApiException {
    Map<String, String> auth = new HashMap<>();
    auth.put("api_key", "secret");
    auth.put("engine", "google");
    SerpApi serpapi = new SerpApi(auth);
    RecordingHttp http = new RecordingHttp();
    serpapi.client = http;

    serpapi.markdown(new HashMap<>());

    assertEquals("secret", http.recorded.get("api_key"));
    assertEquals("google", http.recorded.get("engine"));
    assertEquals("md", http.recorded.get("output"));
  }
}
