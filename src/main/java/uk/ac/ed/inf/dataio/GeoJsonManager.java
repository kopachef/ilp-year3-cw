package uk.ac.ed.inf.dataio;

import com.mapbox.geojson.*;
import com.mapbox.turf.TurfJoins;
import uk.ac.ed.inf.LongLat;
import uk.ac.ed.inf.utils.Settings;
import uk.ac.ed.inf.algorithm.Node;

import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GeoJsonManager {

  /** Feature Collection of all the restricted areas. */
  private static final FeatureCollection restrictedAreasFeatures =
      FeatureCollection.fromJson(
          UrlDownloadManager.loadUrlContents(
              Settings.getDefaultServerUrlProtocol()
                  + Settings.getDefaultServerHost()
                  + ":"
                  + Settings.getDefaultServerPort()
                  + Settings.getDefaultRestrictedBuildingsFilename()));

  /** List of Polygons representing restricted areas. */
  private static final List<Polygon> restrictedAreas =
      Objects.requireNonNull(restrictedAreasFeatures.features()).stream()
          .map(x -> (Polygon) x.geometry())
          .collect(Collectors.toList());

  /**
   * Function to check if a point in within the perimeter of the restricted area. Returns True if
   * point is in within range(subject to the radius) amd false otherwise.
   *
   * @param centre centre representing the point for which we are testing for.
   * @param radius radius denoting how far away from this point that we have to extrapolate and
   *     check if a restricted area is in range.
   * @return True if in range and False otherwise.
   */
  public static boolean isInPerimeterOfRestrictedArea(LongLat centre, double radius) {
    LongLat top = new LongLat(centre.longitude, centre.latitude + radius);
    LongLat bottom = new LongLat(centre.longitude, centre.latitude - radius);
    LongLat right = new LongLat(centre.longitude + radius, centre.latitude);
    LongLat left = new LongLat(centre.longitude - radius, centre.latitude);

    for (Polygon p : restrictedAreas) {
      if (Stream.of(centre, top, bottom, right, left)
          .anyMatch(x -> (TurfJoins.inside(createPointFromLongLat(x), p)))) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns a list of Feature objects representing the restricted areas.
   *
   * @return Restricted areas as a List of Features.
   */
  public static List<Feature> getRestrictedAreasFeatures() {
    return restrictedAreasFeatures.features();
  }

  /**
   * Alternative to the 'isInPerimeterOfRestrictedArea' function that just checks whether a point is
   * in the restricted area. This function does not provide any fine-grained control of checking
   * whether a point is right next to a restricted area. While this may seem like a duplicate of the
   * afore mentioned function, it is 4 times faster and has specific use cases where we don't care
   * about radius.
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
   * Creates an Area from a Polygon.
   *
   * @param polygon the polygon
   * @return the Area created from the polygon
   */
  public static Area createAreaFromPolygon(Polygon polygon) {
    Path2D polyA = new Path2D.Double();
    boolean isFirst = true;
    for (Point p : polygon.outer().coordinates()) {
      if (isFirst) {
        polyA.moveTo(p.longitude(), p.latitude());
        isFirst = false;
      } else {
        polyA.lineTo(p.longitude(), p.latitude());
      }
    }
    polyA.closePath();
    return new Area(polyA);
  }

  /**
   * Checks if a Polygon intersects a restricted area.
   *
   * @param inputPolygon  the input Polygon.
   * @return true if the input Polygon intersects a restricted area, false otherwise.
   */
  public static boolean intersectsRestrictedArea(Polygon inputPolygon) {
    Area polygonA = createAreaFromPolygon(inputPolygon);
    for (Polygon p : restrictedAreas) {
      Area polygonB = createAreaFromPolygon(p);
      polygonB.intersect(polygonA);
      if (!polygonB.isEmpty()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Tests if a given point is inside the confinement zone.
   *
   * @param longLat the longitude and latitude of the point to test.
   * @return True if in confinement zone, false otherwise.
   */
  public static boolean isInConfinementZone(LongLat longLat) {
    List<Point> corners =
        Stream.of(
                Settings.getDefaultNorthWestBound(),
                Settings.getDefaultNorthEastBound(),
                Settings.getDefaultSouthEastBound(),
                Settings.getDefaultSouthWestBound(),
                Settings.getDefaultNorthWestBound())
            .map(GeoJsonManager::createPointFromLongLat)
            .collect(Collectors.toList());

    LineString boundingLineString = LineString.fromLngLats(corners);
    Polygon boundingBox = Polygon.fromOuterInner(boundingLineString);

    return TurfJoins.inside(createPointFromLongLat(longLat), boundingBox);
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
   * Checks if two nodes are visible from each other.
   *
   * @param a the first node
   * @param b the second node
   * @param c the node to check visibility from
   * @return true if the nodes a and c are directly visible from each other.
   * */

  public static boolean lineOfSight(Node a, Node b, Node c) {
    List<Point> pts = GeoJsonManager.generatePointsFromNodes(Arrays.asList(a, b, c, a));
    LineString line = LineString.fromLngLats(pts);
    return intersectsRestrictedArea(Polygon.fromOuterInner(line));
  }

  /**
   * Checks if a line segment connecting two points crosses a restricted area.

   * @param start the starting LongLat
   * @param end the ending LongLat
   * @return true if line segment crosses restricted area, false otherwise
   *
   * */
  public static boolean crossesRestrictedArea(LongLat start, LongLat end) {
    double offset = 0.05 * Settings.getDefaultMovementStepDistance();
    LongLat mid = new LongLat(start.longitude + offset, start.latitude + offset);
    List<LongLat> coords = Arrays.asList(start, mid, end, start);
    List<Point> pts =
        coords.stream()
            .map(GeoJsonManager::createPointFromLongLat)
            .collect(Collectors.toList());
    LineString line = LineString.fromLngLats(pts);
    return !intersectsRestrictedArea(Polygon.fromOuterInner(line));
  }

  /**
   * Creates a Feature Collection from a given lost of features.
   *
   * @param features List of features from which to build the Feature Collection.
   * @return FeatureCollection object
   */
  @SafeVarargs
  public static FeatureCollection createFeatureCollection(List<Feature>... features) {
    List<Feature> result = new ArrayList<>();
    for (List<Feature> f : features) {
      result.addAll(f);
    }
    return FeatureCollection.fromFeatures(result);
  }
}
