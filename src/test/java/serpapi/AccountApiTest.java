package serpapi;

import com.google.gson.JsonObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AccountApiTest {

  @Test
  public void account() throws Exception {
    if (System.getenv("API_KEY") == null) {
     return;
    }

    Map<String, String> parameter = new HashMap<String, String>();
    parameter.put("api_key", System.getenv("API_KEY"));
    SerpApi client = new SerpApi(parameter);
    JsonObject info = client.account();
    System.out.println(info.toString());
    assertEquals("Active", info.getAsJsonObject().get("account_status").getAsString());
  }
}