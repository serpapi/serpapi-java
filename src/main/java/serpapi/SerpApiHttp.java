package serpapi;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * HTTPS client for Serp API
 */
public class SerpApiHttp {
  // http request configuration
  private int httpConnectionTimeout;
  private int httpReadTimeout;

  /**
   * current API version
   */
  public static String VERSION = "1.0.0";

  /**
   * backend service
   */
  public static String BACKEND = "https://serpapi.com";

  /**
   * initialize gson
   */
  private static Gson gson = new Gson();

  /**
   * current backend HTTP path
   */
  public String path;

  /***
   * Constructor
   * @param path HTTP url path
   */
  public SerpApiHttp(String path) {
    this.path = path;
  }

  /***
   * Connect socket connection
   *
   * @param path url end point
   * @param parameter client parameters like: { "q": "coffee", "location": "Austin, TX"}
   * @return HttpURLConnection connection object
   * @throws SerpApiException wraps error message
   */
  protected HttpURLConnection connect(String path, Map<String, String> parameter) throws SerpApiException {
    HttpURLConnection con;
    try {
      allowHTTPS();
      String query = ParameterStringBuilder.getParamsString(parameter);
      URL url = new URL(BACKEND + path + "?" + query);
      con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod("GET");
    } catch (IOException e) {
      throw new SerpApiException(e);
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      throw new SerpApiException(e);
    } catch (KeyManagementException e) {
      e.printStackTrace();
      throw new SerpApiException(e);
    }

    String outputFormat = parameter.get("output");
    if (outputFormat == null) {
      throw new SerpApiException("output format must be defined: " + path);
    } else if (outputFormat.startsWith("json")) {
      con.setRequestProperty("Content-Type", "application/json");
    }
    con.setConnectTimeout(getHttpConnectionTimeout());
    con.setReadTimeout(getHttpReadTimeout());

    con.setDoOutput(true);
    return con;
  }

  // Allow HTTPS support with legacy java version
  private void allowHTTPS() throws NoSuchAlgorithmException, KeyManagementException {
    TrustManager[] trustAllCerts;
    trustAllCerts = new TrustManager[] { new X509TrustManager() {
      public X509Certificate[] getAcceptedIssuers() {
        return null;
      }

      public void checkClientTrusted(X509Certificate[] certs, String authType) {
      }

      public void checkServerTrusted(X509Certificate[] certs, String authType) {
      }

    } };

    SSLContext sc = SSLContext.getInstance("SSL");
    sc.init(null, trustAllCerts, new java.security.SecureRandom());
    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

    // Create all-trusting host name verifier
    HostnameVerifier allHostsValid = new HostnameVerifier() {
      public boolean verify(String hostname, SSLSession session) {
        return true;
      }
    };
    // Install the all-trusting host verifier
    HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    /*
     * end of the fix
     */
  }

  /***
   * Returns HTTP response content as a raw String
   *
   * @param parameter user client parameters
   * @return http response body
   * @throws SerpApiException wraps error message
   */
  public String get(Map<String, String> parameter) throws SerpApiException {
    HttpURLConnection con = connect(this.path, parameter);

    // Get HTTP status
    int statusCode = -1;
    // Hold response stream
    InputStream is = null;
    // Read buffer
    BufferedReader in = null;
    try {
      statusCode = con.getResponseCode();

      if (statusCode == 200) {
        is = con.getInputStream();
      } else {
        is = con.getErrorStream();
      }

      Reader reader = new InputStreamReader(is);
      in = new BufferedReader(reader);
    } catch (IOException e) {
      throw new SerpApiException(e);
    }

    String inputLine;
    StringBuffer content = new StringBuffer();
    try {
      while ((inputLine = in.readLine()) != null) {
        content.append(inputLine);
      }
      in.close();
    } catch (IOException e) {
      throw new SerpApiException(e);
    }

    // Disconnect
    con.disconnect();

    if (statusCode != 200) {
      triggerSerpApiException(content.toString());
    }
    return content.toString();
  }

  /**
   * trigger a exception on error
   * @param content raw JSON response from serpapi.com
   * @throws SerpApiException wraps error message
   */
  protected void triggerSerpApiException(String content) throws SerpApiException {
    String errorMessage;
    try {
      JsonObject element = gson.fromJson(content, JsonObject.class);
      errorMessage = element.get("error").getAsString();
    } catch (Exception e) {
      throw new AssertionError("invalid response format: " + content);
    }
    throw new SerpApiException(errorMessage);
  }

  /**
    * @return current HTTP connection timeout
    */
  public int getHttpConnectionTimeout() {
    return httpConnectionTimeout;
  }

  /**
   * @param httpConnectionTimeout set HTTP connection timeout
   */
  public void setHttpConnectionTimeout(int httpConnectionTimeout) {
    this.httpConnectionTimeout = httpConnectionTimeout;
  }

  /**
   * @return current HTTP read timeout
   */
  public int getHttpReadTimeout() {
    return httpReadTimeout;
  }

  /**
   * @param httpReadTimeout set HTTP read timeout
   */
  public void setHttpReadTimeout(int httpReadTimeout) {
    this.httpReadTimeout = httpReadTimeout;
  }

}
