package serpapi;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Test main class
 */
public class GoogleProductTest {

  // TODO wait on https://github.com/serpapi/SerpApi/issues/4432

  // @Test
  // public void search() throws SerpApiException {
  //   // skip test if no api_key provided
  //   if(System.getenv("API_KEY") == null)
  //     return;

  //   // setup serpapi client
  //   Map<String, String> auth = new HashMap<>();
  //   auth.put("api_key", System.getenv("API_KEY"));
  //   SerpApi client = new SerpApi(auth);

  //   // run search
  //   Map<String, String> parameter = new HashMap<>();
  //   parameter.put("engine", "google_product");
  //   parameter.put("q", "coffee");
  //   parameter.put("product_id", "4887235756540435899");
  //   JsonObject results = client.search(parameter);
  //   assertTrue(results.getAsJsonArray("product_results").size() > 5);
  // }

}