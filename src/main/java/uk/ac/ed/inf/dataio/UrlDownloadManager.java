package uk.ac.ed.inf.dataio;

import uk.ac.ed.inf.deliveryutils.Settings;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

public class UrlDownloadManager {
  /**
   * This class provides access to a single http client and also provides the necessary functions to
   * download and read content from a given url.
   *
   * <p>Further utility functions are provided within the class to enable easier operations
   * including url content caching in cases where the same url is called multiple times.</>
   */
  private static final Object lock = new Object();

  private static final HttpClient client = HttpClient.newHttpClient();
  private static final HashMap<String, String> jsonLookUpTable =
      new HashMap<>(Settings.getDefaultUrlCacheSize());

  /**
   * Given a url, this method returns the content returned as output from making a http request with
   * from this url.
   *
   * @param url url used to make http request.
   * @return String output returned from calling given url.
   */
  public static String loadUrlContents(String url) {
    if (jsonLookUpTable.get(url) == null) {
      updateCache(url);
    }
    // if cache is disabled we completely remove the cached value.
    return Settings.isCacheUrlContentEnabled()
        ? jsonLookUpTable.get(url)
        : jsonLookUpTable.remove(url);
  }

  /**
   * This method updates our <url:urlContent> hashmap by adding or updating an entry with the key
   * being the url and the value being the content returned from making an http request with the url
   *
   * @param url url to make http request with and use as key in our hashmap.
   */
  private static void updateCache(String url) {
    synchronized (lock) {
      try {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
          jsonLookUpTable.put(url, response.body());
        } else {
          System.err.println("\nNothing found(Error 404)! at:\n\t " + url);
          System.exit(1);
        }
      } catch (IOException | InterruptedException e) {
        System.err.println(
            "\nFatal error: Unable to connect to: '"
                + Settings.getDefaultServerHost()
                + "' at port: '"
                + Settings.getDefaultServerPort()
                + "'.");
        System.exit(1);
      } catch (IllegalArgumentException e) {
        System.err.println("\nWrong URL format found in: " + url);
        System.exit(1);
      }
    }
  }
}
