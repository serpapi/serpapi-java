package serpapi;

import com.google.gson.JsonArray;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import java.nio.file.Paths;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class LocationApiTest {

  // @Test
  // public void getLocation() throws Exception {
  //   // mock response if run on github
  //   SerpApi client = new SerpApi();
  //   if (SerpApi.api_key_default == null) {
  //     SerpApiHttp stub = mock(SerpApiHttp.class);
  //     when(stub.getResults(ArgumentMatchers.<String, String>anyMap()))
  //         .thenReturn(ReadJsonFile.readAsJson(Paths.get("src/test/java/serpapi/data/location.json")).toString());
  //     client.client = stub;
  //   }

  //   // client.client = search;
  //   JsonArray location = client.getLocation("Austin", 3);
  //   System.out.println(location.toString());
  //   assertEquals("Austin, TX", location.get(0).getAsJsonObject().get("name").getAsString());
  // }
}