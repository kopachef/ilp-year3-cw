package uk.ac.ed.inf;

public final class Settings {

  private Settings() {
    //Prevent instantiation
    throw new UnsupportedOperationException();
  }
  /**
   * Bounding box coordinates
   *
   * (NORTHWEST_BOUND)            (NORTHEAST_BOUND)
   *          +-----------------+
   *          |        N        |
   *          |                 |
   *          | W             E |
   *          |                 |
   *          |        S        |
   *          +-----------------+
   * (SOUTHWEST_BOUND)           (SOUTHEAST_BOUND)
   *
   *  Below are the value denoting the coordinates of bounds shown in the diagram.
   */

  //TODO seriously remove this location it is only for tests.
  public static final LongLat businessSchool = new LongLat(-3.1873,55.9430);

  public static int GRID_GRANULARITY = 54;
  /**
   ****************************************************************
   */

  public static LongLat DEFAULT_NORTHWEST_BOUND = new LongLat(-3.192473, 55.946233);
  public static LongLat DEFAULT_NORTHEAST_BOUND = new LongLat(-3.184319, 55.946233);
  public static LongLat DEFAULT_SOUTHWEST_BOUND = new LongLat(-3.192473, 55.942617);
  public static LongLat DEFAULT_SOUTHEAST_BOUND = new LongLat(-3.184319, 55.942617);

  /**
   * Set methods for bounding box parameters.
   */
  public static void setDefaultNorthwestBound(LongLat northwestBound) {
    DEFAULT_NORTHWEST_BOUND = northwestBound;
  }
  public static void setDefaultNorthEastBound(LongLat northEastBound) {
    DEFAULT_NORTHEAST_BOUND = northEastBound;
  }
  public static void setDefaultSouthEastBound(LongLat southEastBound) {
    DEFAULT_SOUTHEAST_BOUND = southEastBound;
  }
  public static void setDefaultSouthWastBound(LongLat southWestBound) { DEFAULT_SOUTHWEST_BOUND = southWestBound; }

  /**
   * Get methods for bounding box parameters
   */

  public static LongLat getDefaultNorthWestBound() {
    return DEFAULT_NORTHWEST_BOUND;
  }
  public static LongLat getDefaultNorthEastBound() {
    return DEFAULT_NORTHEAST_BOUND;
  }
  public static LongLat getDefaultSouthEastBound() {
    return DEFAULT_SOUTHEAST_BOUND;
  }
  public static LongLat getDefaultSouthWestBound() {
    return DEFAULT_SOUTHWEST_BOUND;
  }

  /***************************************************************
   *
   * DRONE MOVEMENT ATTRIBUTES AND ASSOCIATED CONSTANTS
   */
  public static LongLat DEFAULT_HOME_LOCATION = new LongLat(-3.186874, 55.944494);
  public static double DEFAULT_CLOSENESS_TOLERANCE = 0.00015;
  public static double DEFAULT_MOVEMENT_STEP_DISTANCE = 0.00015;

  /**
   * Set methods for the drone movement parameters.
   */

  public static void setHomeLocation(LongLat homeLocation) {
    DEFAULT_HOME_LOCATION = homeLocation;
  }
  public static void setDefaultClosenessTolerance(double closenessTolerance) {
    DEFAULT_CLOSENESS_TOLERANCE = closenessTolerance;
  }
  public static void setDefaultMovementStepDistance(double movementStepDistance) {
    DEFAULT_MOVEMENT_STEP_DISTANCE = movementStepDistance;
  }

  /**
   * Get methods for the drone movement parameters.
   */
  public static LongLat getDefaultHomeLocation() { return DEFAULT_HOME_LOCATION; }
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
  public static String DEFAULT_MENUS_ADDRESS_RESOURCE = "/menus/menus.json";
  public static String DEFAULT_W3W_CONTENT_FILENAME = "details.json";
  public static String DEFAULT_W3W_CONTENT_ROOT_DIRECTORY = "/words/";
  public static String DEFAULT_RESTRICTED_BUILDINGS_FILENAME = "/buildings/no-fly-zones.geojson";
  public static String DEFAULT_LANDMARKS_FILENAME = "/buildings/landmarks.geojsono";

  /**
   * Set methods for the server file directory constants.
   */
  public static void setDefaultMenusAddressResource(String menusAddressUrl){
    DEFAULT_MENUS_ADDRESS_RESOURCE = menusAddressUrl;
  }
  public static void setDefaultW3wContentFilename(String w3wContentFilename){
    DEFAULT_W3W_CONTENT_FILENAME = w3wContentFilename;
  }
  public static void setDefaultW3wContentRootDirectory(String w3wContentRootDirectory){
    DEFAULT_W3W_CONTENT_ROOT_DIRECTORY = w3wContentRootDirectory;
  }
  public static void setDefaultRestrictedBuildingsFilename(String defaultRestrictedBuildingsFilename) {
    DEFAULT_RESTRICTED_BUILDINGS_FILENAME = defaultRestrictedBuildingsFilename;
  }
  public static void setDefaultLandmarksFilename(String defaultLandmarksFilename) {
    DEFAULT_LANDMARKS_FILENAME = defaultLandmarksFilename;
  }

  /**
   * Get methods for the server file directory constants.
   */
  public static String getDefaultMenusAddressResource() {
    return DEFAULT_MENUS_ADDRESS_RESOURCE;
  }
  public static String getDefaultW3wContentFilename() {
    return DEFAULT_W3W_CONTENT_FILENAME;
  }
  public static String getDefaultW3wContentRootDirectory() { return DEFAULT_W3W_CONTENT_ROOT_DIRECTORY;}
  public static String getDefaultRestrictedBuildingsFilename() { return DEFAULT_RESTRICTED_BUILDINGS_FILENAME; }
  public static String getDefaultLandmarksFilename() { return DEFAULT_LANDMARKS_FILENAME; }

  /*****************************************************************
   *
   * URL HTTP REQUEST CONFIGURATIONS
   */
  public static String DEFAULT_SERVER_HOST = "localhost";
  public static String DEFAULT_SERVER_PORT = "9898";
  public static String DEFAULT_SERVER_URL_PROTOCOL = "http://";
  public static boolean CACHE_URL_CONTENT = true;
  public static int DEFAULT_URL_CACHE_SIZE = 5;

  /**
   * Set methods for the url http request configurations.
   */
  public static void setDefaultServerHost(String defaultServerHost) {
    DEFAULT_SERVER_HOST = defaultServerHost;
  }
  public static void setDefaultServerPort(String defaultServerPort) {
    DEFAULT_SERVER_PORT = defaultServerPort;
  }
  public static void setDefaultServerUrlProtocol(String urlPrefix) {
    DEFAULT_SERVER_URL_PROTOCOL = urlPrefix;
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
  public static String getDefaultServerHost() {
    return DEFAULT_SERVER_HOST;
  }
  public static String getDefaultServerPort() {
    return DEFAULT_SERVER_PORT;
  }
  public static String getDefaultServerUrlProtocol() {
    return DEFAULT_SERVER_URL_PROTOCOL;
  }
  public static boolean isCacheUrlContentEnabled() {
    return CACHE_URL_CONTENT;
  }
  public static int getDefaultUrlCacheSize() {
    return DEFAULT_URL_CACHE_SIZE;
  }

  /*****************************************************************
   *
   * DATABASE ACCESS CONFIGURATIONS
   */

  public static String DEFAULT_DATABASE_PROTOCOL = "jdbc:derby://";
  public static String DEFAULT_DATABASE_HOST = "localhost";
  public static String DEFAULT_DATABASE_PORT = "1527";
  public static String DEFAULT_DATABASE_ADDRESS = "derbyDB";


  /**
   * Set methods for the database access confgurations
   */

  public static void setDefaultDatabaseProtocol(String defaultDatabaseProtocol) {
    DEFAULT_DATABASE_PROTOCOL = defaultDatabaseProtocol;
  }
  public static void setDefaultDatabaseHost(String defaultDatabaseHost) {
    DEFAULT_DATABASE_HOST = defaultDatabaseHost;
  }
  public static void setDefaultDatabasePort(String defaultDatabasePort) {
    DEFAULT_DATABASE_PORT = defaultDatabasePort;
  }
  public static void setDefaultDatabaseAddress(String defaultDatabaseAddress) {
    DEFAULT_DATABASE_ADDRESS = defaultDatabaseAddress;
  }

  /**
   * Get methods for the database access configurations
   */

  public static String getDefaultDatabaseProtocol() {return DEFAULT_DATABASE_PROTOCOL;}
  public static String getDefaultDatabaseHost() {return DEFAULT_DATABASE_HOST;}
  public static String getDefaultDatabasePort() {return DEFAULT_DATABASE_PORT;}
  public static String getDefaultDatabaseAddress() {return DEFAULT_DATABASE_ADDRESS;}


  /**
   * Dump methods to print current state and constants of our configuration.
   */
  public static void dump() {
    System.out.println(
                    "NORTHEAST_BOUND: " + getDefaultNorthEastBound()
                    + "\nNORTHWEST_BOUND: " + getDefaultNorthWestBound()
                    + "\nSOUTHEAST_BOUND: " + getDefaultSouthEastBound()
                    + "\nSOUTHWEST_BOUND: " + getDefaultSouthWestBound()
                    + "\nCLOSENESS_TOLERANCE: " + getDefaultClosenessTolerance()
                    + "\nMOVEMENT_STEP_DISTANCE: " + getDefaultMovementStepDistance()
                    + "\nSTANDARD_CHARGE: " + getDefaultStandardCharge()
                    + "\nMENUS_ADDRESS_URL: " + getDefaultMenusAddressResource()
                    + "\nW3W_CONTENT_FILENAME: " + getDefaultW3wContentFilename()
                    + "\nW3W_CONTENT_ROOT_DIRECTORY: " + getDefaultW3wContentRootDirectory()
                    + "\nURL_PREFIX: " + getDefaultServerUrlProtocol()
                    + "\nCACHE_URL_CONTENT: " + isCacheUrlContentEnabled()
                    + "\nURL_CACHE_SIZE: " + getDefaultUrlCacheSize()
    );
  }
}
