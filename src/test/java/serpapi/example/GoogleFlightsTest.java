package serpapi.example;
import serpapi.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import org.junit.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Test Google Flights has best or other flights
 */
public class GoogleFlightsTest {

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
    DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
    String outboundDate = LocalDate.now().plusDays(30).format(fmt);
    String returnDate = LocalDate.now().plusDays(37).format(fmt);

    Map<String, String> parameter = new HashMap<>();
    parameter.put("engine", "google_flights");
    parameter.put("departure_id", "LAX");
    parameter.put("arrival_id", "AUS");
    parameter.put("outbound_date", outboundDate);
    parameter.put("return_date", returnDate);
    JsonObject results = client.search(parameter);

    JsonArray bestFlights = results.getAsJsonArray("best_flights");
    JsonArray otherFlights = results.getAsJsonArray("other_flights");
    JsonArray flights = (bestFlights != null && bestFlights.size() > 0) ? bestFlights : otherFlights;

    assertNotNull(flights);
    assertTrue(flights.size() > 0);
  }

}