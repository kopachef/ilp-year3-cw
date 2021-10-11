package uk.ac.ed.inf;

public final class Settings {

  private Settings() {
    //Prevent instantiation
    throw new UnsupportedOperationException();
  }
  /**
   * Bounding box coordinates
   *
   * (NORTHWEST_BOUND)
   *          +-----------------+
   *          |        N        |
   *          |                 |
   *          | W             E |
   *          |                 |
   *          |        S        |
   *          +-----------------+
   *                       (SOUTHEAST_BOUND)
   *
   *  Below are the value denoting the coordinates of bounds shown in the diagram.
   */

  public static double DEFAULT_NORTHWEST_BOUND_LONGITUDE = -3.192473;
  public static double DEFAULT_NORTHWEST_BOUND_LATITUDE = 55.946233;
  public static double DEFAULT_SOUTHEAST_BOUND_LONGITUDE = -3.184319;
  public static double DEFAULT_SOUTHEAST_BOUND_LATITUDE = 55.942617;

  /**
   * Set methods for bounding box parameters.
   */
  public static void setDefaultNorthwestBoundLongitude(double longitude) {
    DEFAULT_NORTHWEST_BOUND_LONGITUDE = longitude;
  }
  public static void setDefaultNorthwestBoundLatitude(double latitude) {
    DEFAULT_NORTHWEST_BOUND_LATITUDE = latitude;
  }
  public static void setDefaultSoutheastBoundLongitude(double longitude) {
    DEFAULT_SOUTHEAST_BOUND_LONGITUDE = longitude;
  }
  public static void setDefaultSoutheastBoundLatitude(double latitude) {
    DEFAULT_SOUTHEAST_BOUND_LATITUDE = latitude;
  }

  /**
   * Get methods for bounding box parameters
   */

  public static double getDefaultNorthwestBoundLongitude() {
    return DEFAULT_NORTHWEST_BOUND_LONGITUDE;
  }
  public static double getDefaultNorthwestBoundLatitude() {
    return DEFAULT_NORTHWEST_BOUND_LATITUDE;
  }
  public static double getDefaultSoutheastBoundLongitude() {
    return DEFAULT_SOUTHEAST_BOUND_LONGITUDE;
  }
  public static double getDefaultSoutheastBoundLatitude() {
    return DEFAULT_SOUTHEAST_BOUND_LATITUDE;
  }

  /***************************************************************
   *
   * DRONE MOVEMENT CONSTANTS
   */
  public static double DEFAULT_CLOSENESS_TOLERANCE = 0.00015;
  public static double DEFAULT_MOVEMENT_STEP_DISTANCE = 0.00015;

  /**
   * Set methods for the drone movement parameters.
   */

  public static void setDefaultClosenessTolerance(double closenessTolerance) {
    DEFAULT_CLOSENESS_TOLERANCE = closenessTolerance;
  }
  public static void setDefaultMovementStepDistance(double movementStepDistance) {
    DEFAULT_MOVEMENT_STEP_DISTANCE = movementStepDistance;
  }

  /**
   * Get methods for the drone movement parameters.
   */
  public static double getDefaultClosenessTolerance() {
    return DEFAULT_CLOSENESS_TOLERANCE;
  }

  public static double getDefaultMovementStepDistance() {
    return DEFAULT_MOVEMENT_STEP_DISTANCE;
  }

  /*****************************************************************
   *
   * FOOD ORDER AND DELIVERY CONSTANTS
   */
  public static int DEFAULT_STANDARD_CHARGE = 50;

  /**
   * Set methods for our food order and delivery constants.
   */
  public static void setDefaultStandardCharge(int defaultStandardCharge) {
    DEFAULT_STANDARD_CHARGE = defaultStandardCharge;
  }

  /**
   * Get methods for our food order and delivery constants.
   */
  public static int getDefaultStandardCharge() {
    return DEFAULT_STANDARD_CHARGE;
  }

  /*****************************************************************
   *
   * SERVER FILE DIRECTORY CONSTANTS
   */
  public static String DEFAULT_MENUS_ADDRESS_URL = "/menus/menus.json";
  public static String DEFAULT_W3W_CONTENT_FILENAME = "details.json";
  public static String DEFAULT_W3W_CONTENT_ROOT_DIRECTORY = "/words/";

  /**
   * Set methods for the server file directory constants.
   */
  public static void setDefaultMenusAddressUrl(String menusAddressUrl){
    DEFAULT_MENUS_ADDRESS_URL = menusAddressUrl;
  }
  public static void setDefaultW3wContentFilename(String w3wContentFilename){
    DEFAULT_W3W_CONTENT_FILENAME = w3wContentFilename;
  }
  public static void setDefaultW3wContentRootDirectory(String w3wContentRootDirectory){
    DEFAULT_W3W_CONTENT_ROOT_DIRECTORY = w3wContentRootDirectory;
  }

  /**
   * Get methods for the server file directory constants.
   */
  public static String getDefaultMenusAddressUrl() {
    return DEFAULT_MENUS_ADDRESS_URL;
  }
  public static String getDefaultW3wContentFilename() {
    return DEFAULT_W3W_CONTENT_FILENAME;
  }
  public static String getDefaultW3wContentRootDirectory() {
    return DEFAULT_W3W_CONTENT_ROOT_DIRECTORY;
  }

  /*****************************************************************
   *
   * URL HTTP REQUEST CONFIGURATIONS
   */
  public static String DEFAULT_HOST = "localhost";
  public static String DEFAULT_PORT = "9898";
  public static String DEFAULT_URL_PREFIX = "http://";
  public static boolean CACHE_URL_CONTENT = true;
  public static int DEFAULT_URL_CACHE_SIZE = 5;

  /**
   * Set methods for the url http request configurations.
   */
  public static void setDefaultHost(String defaultHost) {
    DEFAULT_HOST = defaultHost;
  }
  public static void setDefaultPort(String defaultPort) {
    DEFAULT_PORT = defaultPort;
  }
  public static void setDefaultUrlPrefix(String urlPrefix) {
    DEFAULT_URL_PREFIX = urlPrefix;
  }
  public static void setCacheUrlContent(boolean cacheUrlContent) {
    CACHE_URL_CONTENT = cacheUrlContent;
  }
  public static void setDefaultUrlCacheSize(int urlCacheSize) {
    DEFAULT_URL_CACHE_SIZE = urlCacheSize;
  }

  /**
   * Get methods for the url http request configurations.
   */
  public static String getDefaultHost() {
    return DEFAULT_HOST;
  }
  public static String getDefaultPort() {
    return DEFAULT_PORT;
  }
  public static String getDefaultUrlPrefix() {
    return DEFAULT_URL_PREFIX;
  }
  public static boolean isCacheUrlContentEnabled() {
    return CACHE_URL_CONTENT;
  }
  public static int getDefaultUrlCacheSize() {
    return DEFAULT_URL_CACHE_SIZE;
  }

  /**
   * Dump methods to print current state and parameters of our configurations.
   */
  public static void dump() {
    System.out.println(
                    "NORTHWEST_BOUND_LONGITUDE: " + getDefaultNorthwestBoundLongitude()
                    + "\nNORTHWEST_BOUND_LATITUDE: " + getDefaultNorthwestBoundLatitude()
                    + "\nSOUTHEAST_BOUND_LONGITUDE: " + getDefaultSoutheastBoundLongitude()
                    + "\nSOUTHEAST_BOUND_LATITUDE: " + getDefaultSoutheastBoundLatitude()
                    + "\nCLOSENESS_TOLERANCE: " + getDefaultClosenessTolerance()
                    + "\nMOVEMENT_STEP_DISTANCE: " + getDefaultMovementStepDistance()
                    + "\nSTANDARD_CHARGE: " + getDefaultStandardCharge()
                    + "\nMENUS_ADDRESS_URL: " + getDefaultMenusAddressUrl()
                    + "\nW3W_CONTENT_FILENAME: " + getDefaultW3wContentFilename()
                    + "\nW3W_CONTENT_ROOT_DIRECTORY: " + getDefaultW3wContentRootDirectory()
                    + "\nURL_PREFIX: " + getDefaultUrlPrefix()
                    + "\nCACHE_URL_CONTENT: " + isCacheUrlContentEnabled()
                    + "\nURL_CACHE_SIZE: " + getDefaultUrlCacheSize()
    );
  }

}
