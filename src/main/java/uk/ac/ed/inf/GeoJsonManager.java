package uk.ac.ed.inf;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import com.mapbox.turf.TurfJoins;
import uk.ac.ed.inf.algorithm.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GeoJsonManager {

  /** Feature Collection of all the restricted areas. */
  private static FeatureCollection restrictedAreasFeatures =
      FeatureCollection.fromJson(
          UrlDownloadManager.loadUrlContents(
              Settings.getDefaultServerUrlProtocol()
                  + Settings.getDefaultServerHost()
                  + ":"
                  + Settings.getDefaultServerPort()
                  + Settings.getDefaultRestrictedBuildingsFilename()));

  /** Feature Collection of all the landmark features. */
  private static FeatureCollection landmarkFeatures =
      FeatureCollection.fromJson(
          UrlDownloadManager.loadUrlContents(
              Settings.getDefaultServerUrlProtocol()
                  + Settings.getDefaultServerHost()
                  + ":"
                  + Settings.getDefaultServerPort()
                  + Settings.getDefaultRestrictedBuildingsFilename()));

  /**
   * Function to check if a point in within the perimeter of the restricted area. Returns True if point is in
   * within range(subject to the radius) amd false otherwise.
   *
   * @param centre centre representing the point for which we are testing for.
   * @param radius radius denoting how far away from this point that we have to extrapolate and check if
   *               a restricted area is in range.
   * @return True if in range and False otherwise.
   */
  public static boolean isInPerimeterOfRestrictedArea(LongLat centre, double radius) {
    LongLat top = new LongLat(centre.longitude, centre.latitude + radius);
    LongLat bottom = new LongLat(centre.longitude, centre.latitude - radius);
    LongLat right = new LongLat(centre.longitude + radius, centre.latitude);
    LongLat left = new LongLat(centre.longitude - radius, centre.latitude);

    for (Polygon p : restrictedAreas) {
      if (Stream.of(centre, top, bottom, right, left)
              .map(x -> (TurfJoins.inside(createPointFromLongLat(x), p)))
              .anyMatch(Boolean.TRUE::equals)) {
        return true;
      }
    }
    return false;
  }

  /** List of Polygons representing restricted areas. */
  private static List<Polygon> restrictedAreas =
      restrictedAreasFeatures.features().stream()
          .map(x -> (Polygon) x.geometry())
          .collect(Collectors.toList());

  /**
   * Returns a list of Feature objects representing the restricted areas.
   * @return Restricted areas as a List of Features.
   */
  public static List<Feature> getRestrictedAreasFeatures() {
    return restrictedAreasFeatures.features();
  }

  /**
   * Alternative to the 'isInPerimeterOfRestrictedArea' function that just checks whether a point is in the
   * restricted area. This function does not provide any fine-grained control of checking whether a point
   * is right next to a restricted area. While this may seem like a duplicate of the afore mentioned function,
   * it is 4 times faster and has specific use cases where we don't care about radius.
   *
   * @param longlat Longlat object to checked
   * @return True if is in restricted area, False otherwise.
   */
  public static boolean isInRestrictedArea(LongLat longlat) {
    for (Polygon p : restrictedAreas) {
      if (TurfJoins.inside(createPointFromLongLat(longlat), p)) return true;
    }
    return false;
  }

  /**
   * Creates a Point object from a given LongLat object.
   *
   * @param longLat LongLat object to be used.
   * @return Point object with same longitude and latitude values.
   */
  public static Point createPointFromLongLat(LongLat longLat) {
    return Point.fromLngLat(longLat.longitude, longLat.latitude);
  }

  /**
   * Generates a List of Points from a given List of Nodes.
   *
   * @param nodes List of Nodes and input.
   * @return List of Points objects with equivalent longitude and latitude parameters.
   */
  public static List<Point> generatePointsFromNodes(List<Node> nodes) {
    List<Point> result = new ArrayList<>();
    for (Node n : nodes) {
      result.add(createPointFromLongLat(n.getLongLat()));
    }
    return result;
  }

  /**
   * Creates a Feature Collection from a given lost of features.
   *
   * @param features List of features from which to build the Feature Collection.
   * @return FeatureCollection object
   */
  public static FeatureCollection createFeatureCollection(List<Feature>... features) {
    List<Feature> result = new ArrayList<>();
    for (List<Feature> f : features) {
      result.addAll(f);
    }
    return FeatureCollection.fromFeatures(result);
  }
}
