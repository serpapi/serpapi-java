package serpapi;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Test that the client timeout is applied to reading as well as connecting.
 *
 * Offline: only client configuration is inspected, so no network call and no
 * SERPAPI_KEY are needed.
 */
public class TimeoutTest {

  @Test
  public void appliesTimeoutToBothConnectAndRead() {
    SerpApi serpapi = new SerpApi(new HashMap<>());

    assertEquals(serpapi.timeout.intValue(), serpapi.getClient().getHttpConnectionTimeout());
    assertEquals(serpapi.timeout.intValue(), serpapi.getClient().getHttpReadTimeout());
  }

  @Test
  public void defaultTimeoutSurvivesTheSlowestEngines() {
    // home_depot has repeatedly taken longer than a minute to respond.
    assertTrue(new SerpApi().timeout > 60000);
  }
}
