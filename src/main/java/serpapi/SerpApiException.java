package serpapi;

/**
 * SerpApi Client Exception wraps any exception related to the connection with SerpApi.com
 */
public class SerpApiException extends Exception {
  /**
   * Constructor
   */
  public SerpApiException() {
    super();
  }

  /**
   * Constructor
   * @param exception original exception
   */
  public SerpApiException(Exception exception) {
    super(exception);
  }

  /**
   * Constructor
   * @param message short description of the root cause 
   */
  public SerpApiException(String message) {
    super(message);
  }
}
