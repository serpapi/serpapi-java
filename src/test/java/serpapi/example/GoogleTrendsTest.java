package serpapi.example;
import serpapi.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Test Google Trends has timeline data
 */
public class GoogleTrendsTest {

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
    parameter.put("engine", "google_trends");
    parameter.put("q", "coffee");
    parameter.put("data_type", "TIMESERIES");
    JsonObject results = client.search(parameter);

    JsonObject interestOverTime = results.getAsJsonObject("interest_over_time");
    assertNotNull(interestOverTime);

    JsonArray timelineData = interestOverTime.getAsJsonArray("timeline_data");
    assertTrue(timelineData.size() > 1);
  }

}