package uk.ac.ed.inf;

import com.mapbox.geojson.*;
import com.mapbox.turf.*;
import uk.ac.ed.inf.algorithm.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GeoJsonManager {

    private static FeatureCollection restrictedAreasFeatures =
            FeatureCollection.fromJson(UrlDownloadManager.loadUrlContents(
                    Settings.getDefaultServerUrlProtocol()
                            + Settings.getDefaultServerHost()
                            + ":"
                            + Settings.getDefaultServerPort()
                            + Settings.getDefaultRestrictedBuildingsFilename()));

    public static List<Feature> getRestrictedAreasFeatures() {
        return restrictedAreasFeatures.features();
    }

    public static List<Feature> getLandmarkFeatures() {
        return landmarkFeatures.features();
    }

    private static FeatureCollection landmarkFeatures =
            FeatureCollection.fromJson(UrlDownloadManager.loadUrlContents(
                    Settings.getDefaultServerUrlProtocol()
                            + Settings.getDefaultServerHost()
                            + ":"
                            + Settings.getDefaultServerPort()
                            + Settings.getDefaultRestrictedBuildingsFilename()));

    public List<Polygon> getRestrictedAreas() {
        return restrictedAreas;
    }
    public List<Geometry> getLandmarks() {
        return landmarks;
    }
    private static List<Polygon> restrictedAreas =
            restrictedAreasFeatures.features().stream().map(x -> (Polygon)x.geometry()).collect(Collectors.toList());
    private static List<Geometry> landmarks =
            landmarkFeatures.features().stream().map(x -> x.geometry()).collect(Collectors.toList());

    public static boolean isInRestrictedArea(LongLat longlat) {
        for(Polygon p: restrictedAreas) {
            if(TurfJoins.inside(createPointFromLongLat(longlat), p)) return true;
        } return false;
    }

    public static boolean isInPerimeterOfRestrictedArea(LongLat centre, double radius) {
        LongLat top = new LongLat(centre.longitude, centre.latitude + radius);
        LongLat bottom = new LongLat(centre.longitude, centre.latitude - radius);
        LongLat right = new LongLat(centre.longitude + radius, centre.latitude);
        LongLat left = new LongLat(centre.longitude - radius, centre.latitude);

        for(Polygon p: restrictedAreas) {
            if (Stream.of(centre,top,bottom,right,left)
                    .map(x -> (TurfJoins.inside(createPointFromLongLat(x), p)))
                    .anyMatch(Boolean.TRUE::equals)) {
                return true;
            }
        }
        return false;
    }
    public static Point createPointFromLongLat(LongLat longLat) {
        return Point.fromLngLat(longLat.longitude, longLat.latitude);
    }
    public static List<Point> generatePointsFromNodes(List<Node> nodes) {
        List<Point> result = new ArrayList<>();
        for(Node n : nodes) {
            result.add(createPointFromLongLat(n.getLongLat()));
        }
        return result;
    }
    public static FeatureCollection createFeatureCollection(List<Feature>...features) {
        List<Feature> result = new ArrayList<>();
        for(List<Feature> f : features) {
            result.addAll(f);
        }
        return FeatureCollection.fromFeatures(result);
    }

}