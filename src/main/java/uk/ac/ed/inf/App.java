package uk.ac.ed.inf;

import com.mapbox.geojson.*;
import com.mapbox.turf.TurfJoins;
import com.mapbox.turf.TurfMeasurement;
import com.mapbox.turf.TurfTransformation;
import org.apache.commons.math3.util.Precision;
import uk.ac.ed.inf.graph.Graph;
import uk.ac.ed.inf.graph.Node;

import javax.sound.sampled.Line;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.DoubleStream;

import static java.util.stream.Collectors.toList;

/** Hello world! */
public class App {




    public static List<Double> generateDoubleSequence(double start, double end, double step, double offset) {
        return DoubleStream.iterate(start+offset, d -> d <= end-offset, d -> d + step)
                .boxed()
                .map(x -> Precision.round(x, 6))
                .collect(toList());
    }

  public static void main(String[] args) {
    int granularity = 20;

    LongLat end = new LongLat(-3.1898600, 55.942931);
    LongLat start = new LongLat(-3.1861510, 55.944197);
    LongLat start2 = new LongLat(-3.1896190, 55.944817);
    LongLat dest2 = new LongLat(-3.1911610, 55.945572);

    Graph g =
        new Graph(
            Settings.getDefaultNorthwestBoundLongitude(),
            Settings.getDefaultNorthwestBoundLatitude(),
            Settings.getDefaultSoutheastBoundLongitude(),
            Settings.getDefaultSoutheastBoundLatitude(),
            granularity);

    List<Feature> path =
        GeoJsonManager.generatePointsFromNodes(g.getRestrictedNodes()).stream()
            .map(x -> Feature.fromGeometry((Geometry) x))
            .collect(toList());

    List<Feature> feats = GeoJsonManager.getRestrictedAreasFeatures();
    feats.addAll(path);

    LineString ls = LineString.fromLngLats(GeoJsonManager.generatePointsFromNodes(g.getShortestPath(start, dest2)));

    feats.add(Feature.fromGeometry((Geometry) ls));
    feats.add(Feature.fromGeometry((Geometry) TurfTransformation.circle(GeoJsonManager.createPointFromLongLat(dest2), 0.00015)));
    //System.out.println(GeoJsonManager.createFeatureCollection(feats).toJson());
    //System.out.println(g.distanceBetweenNodes(g.getShortestPath(start, dest2)));
   //System.out.println(TurfMeasurement.length(ls, "metres"));

    //Polygon pp = TurfTransformation.circle(GeoJsonManager.createPointFromLongLat(dest2), 0.00015);
    //Feature f = Feature.fromGeometry(pp);
    //System.out.println(pp.toJson());

    DatabaseIO dbo = new DatabaseIO(Settings.getDefaultDatabaseHost(), Settings.getDefaultDatabasePort());
    System.out.println(dbo.queryOrders("", null,"","")
    );
    }
}