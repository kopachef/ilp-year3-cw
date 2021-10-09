package uk.ac.ed.inf;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

class UrlDownLoader {

  /**
   * This class provides access to a single http client and also provides the necessary functions to
   * download and read content from a given url.
   *
   * <p>Further utility functions are provided within the class to enable easier operations including url content
   * caching in cases where the same url is called multiple times.</>
   *
   */

  private static final Object lock = new Object();
  private static final Settings settings = new Settings();
  private static final HttpClient client = HttpClient.newHttpClient();
  private static final HashMap<String, String> jsonLookUpTable =
          new HashMap<>(settings.getDefaultUrlCacheSize());

  /**
   * Given a url, this method returns the content returned as output from making a http request with from this url.
   *
   * @param url url used to make http request.
   * @return    String output returned from calling given url.
   * @throws IOException throws exception if input stream is corrupted or error occurred while reading data.
   */
  public static String loadUrlContents(String url) {
    if (jsonLookUpTable.get(url) == null) {
      try {
        updateCache(url);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    String response = jsonLookUpTable.get(url) == null ? "" : jsonLookUpTable.get(url);
    if (!settings.isCacheUrlContent()) {
      jsonLookUpTable.clear();
    }
    return response;
  }

  /**
   * This method updates our <url:urlContent> hashmap by adding or updating an entry with
   * the key being the url and the value being the content returned from making an http request with the url
   *
   * @param url url to make http request with and use as key in our hashmap.
   */
  private static void updateCache(String url) {
    synchronized (lock) {
      HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
      try {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
          jsonLookUpTable.put(url, response.body());
        }
      } catch (IOException | InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
