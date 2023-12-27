package serpapi;

import static org.junit.Assert.*;

import java.net.HttpURLConnection;
import java.nio.file.Paths;
import java.util.HashMap;
import org.junit.Before;
import org.junit.Test;

/**
 * Test HTTP search
 */
public class GoogleSearchClientTest {

  SerpApiHttp search;
  private HashMap<String, String> parameter;
  String api_key;

  @Before
  public void setUp() throws Exception {
    client = new SerpApiHttp("/search");

    parameter = new HashMap<>();
    parameter.put("q", "Coffee");
    parameter.put("location", "Austin, Texas");
    parameter.put("output", "json");

    api_key = System.getenv("API_KEY");
  }

  @Test
  public void buildConnection() throws SerpApiException {
    HttpURLConnection connection = client.buildConnection("/search", parameter);
    assertEquals(
      "https://serpapi.com/search?output=json&q=Coffee&api_key=" +
      api_key +
      "&location=Austin%2C+Texas",
      connection.getURL().toString()
    );
  }

  @Test
  public void triggerSerpApiException() throws Exception {
    try {
      String content = ReadJsonFile
        .readAsJson(Paths.get("src/test/java/serpapi/data/error_sample.json"))
        .toString();
      client.triggerSerpApiException(content);
    } catch (Exception ex) {
      assertEquals(SerpApiException.class, ex.getClass());
    }
  }
}
