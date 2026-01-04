package serpapi;

import com.google.gson.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import java.nio.file.Paths;

import java.util.Map;
import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test SerpApi.location() method.
 */
public class LocationApiTest {

  @Test
  public void location() throws Exception {
    if(System.getenv("SERPAPI_KEY") == null) {
      fail("SERPAPI_KEY is not set");
    }

    SerpApi serpapi = new SerpApi();

    Map<String, String> parameter = new HashMap<String, String>();
    parameter.put("q", "Austin, TX");
    parameter.put("limit", "3");
    JsonArray location = serpapi.location(parameter);
    assertEquals("Austin, TX", location.get(0).getAsJsonObject().get("name").getAsString());
  }
}