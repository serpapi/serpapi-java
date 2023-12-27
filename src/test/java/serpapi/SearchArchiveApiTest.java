package serpapi;

import com.google.gson.JsonObject;
import org.junit.Test;

import java.util.Map;
import java.util.HashMap;
import static org.junit.Assert.*;

public class SearchArchiveApiTest {

  @Test
  public void getSearchArchive() throws SerpApiException {
    // skip test if no api_key provided
    if (System.getenv("API_KEY") == null)
      return;

    GoogleSearch.api_key_default = System.getenv("API_KEY");

    Map<String, String> parameter = new HashMap<>();
    parameter.put("q", "Coffee");
    parameter.put("location", "Austin,Texas");

    GoogleSearch client = new GoogleSearch(parameter);
    JsonObject result = client.getJson();

    String searchID = result.get("search_metadata").getAsJsonObject().get("id").getAsString();
    JsonObject archived_result = client.getSearchArchive(searchID);
    System.out.println(archived_result.toString());

    assertEquals(searchID, archived_result.get("search_metadata").getAsJsonObject().get("id").getAsString());
  }
}