package serpapi;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpServer;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/** Offline tests for Image API multipart upload support. */
public class ImageApiTest {

  private static class RecordingHttp extends SerpApiHttp {
    Map<String, String> recorded;
    byte[] recordedImage;
    String response = "{\"message\":\"Image uploaded successfully.\",\"image_id\":\"image-123\"}";

    RecordingHttp() {
      super("/search");
    }

    @Override
    public String postMultipart(Map<String, String> parameter, byte[] image) {
      recorded = parameter;
      recordedImage = image;
      return response;
    }
  }

  private static SerpApi client(RecordingHttp http) {
    Map<String, String> defaults = new HashMap<>();
    defaults.put("api_key", "client-key");
    defaults.put("engine", "google_lens");
    SerpApi client = new SerpApi(defaults);
    client.client = http;
    return client;
  }

  @Test
  public void uploadsPathAndReturnsImageId() throws Exception {
    Path image = Files.createTempFile("serpapi-image-", ".png");
    byte[] imageData = new byte[] {1, 2, 3};
    Files.write(image, imageData);
    try {
      RecordingHttp http = new RecordingHttp();
      JsonObject result = client(http).uploadImage(image);

      assertEquals("image-123", result.get("image_id").getAsString());
      assertArrayEquals(imageData, http.recordedImage);
      assertEquals("/image", http.path);
      assertEquals("client-key", http.recorded.get("api_key"));
      assertFalse(http.recorded.containsKey("engine"));
    } finally {
      Files.deleteIfExists(image);
    }
  }

  @Test
  public void acceptsRawBytesAndCustomFormFields() throws Exception {
    byte[] image = new byte[] {1, 2, 3};
    Map<String, String> fields = new HashMap<>();
    fields.put("api_key", "request-key");
    fields.put("zero_trace", "true");
    RecordingHttp http = new RecordingHttp();

    JsonObject result = client(http).uploadImage(image, fields);

    assertEquals("image-123", result.get("image_id").getAsString());
    assertSame(image, http.recordedImage);
    assertEquals("request-key", http.recorded.get("api_key"));
    assertEquals("true", http.recorded.get("zero_trace"));
  }

  @Test
  public void httpClientSendsMultipartBody() throws Exception {
    AtomicReference<String> contentType = new AtomicReference<>();
    AtomicReference<String> requestBody = new AtomicReference<>();
    HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
    server.createContext("/image", exchange -> {
      contentType.set(exchange.getRequestHeaders().getFirst("Content-Type"));
      requestBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
      byte[] response = "{\"image_id\":\"local-test\"}".getBytes(StandardCharsets.UTF_8);
      exchange.sendResponseHeaders(200, response.length);
      exchange.getResponseBody().write(response);
      exchange.close();
    });
    server.start();

    String originalBackend = SerpApiHttp.BACKEND;
    byte[] image = "fake-png-data".getBytes(StandardCharsets.UTF_8);
    try {
      SerpApiHttp.BACKEND = "http://localhost:" + server.getAddress().getPort();
      SerpApiHttp http = new SerpApiHttp("/image");
      Map<String, String> fields = new HashMap<>();
      fields.put("api_key", "test-key");

      assertTrue(http.postMultipart(fields, image).contains("local-test"));
      assertTrue(contentType.get().startsWith("multipart/form-data; boundary="));
      assertTrue(requestBody.get().contains("name=\"api_key\"\r\n\r\ntest-key"));
      assertTrue(requestBody.get().contains("name=\"image\"; filename=\"image\""));
      assertTrue(requestBody.get().contains("Content-Type: application/octet-stream"));
      assertTrue(requestBody.get().contains("fake-png-data"));
    } finally {
      SerpApiHttp.BACKEND = originalBackend;
      server.stop(0);
    }
  }

  @Test
  public void raisesErrorReturnedByImageApi() {
    RecordingHttp http = new RecordingHttp();
    http.response = "{\"error\":\"Unsupported image format.\"}";

    try {
      client(http).uploadImage(new byte[0]);
      fail("expected SerpApiException");
    } catch (SerpApiException e) {
      assertEquals("Unsupported image format.", e.getMessage());
    }
  }
}
