package uk.ac.ed.inf;

import com.mapbox.geojson.*;
import com.mapbox.turf.TurfJoins;
import org.apache.commons.math3.util.Precision;
import uk.ac.ed.inf.graph.Graph;
import uk.ac.ed.inf.graph.Node;

import javax.sound.sampled.Line;
import java.util.ArrayList;
import java.util.List;
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
    int granularity = 6;

    LongLat end = new LongLat(-3.1898600, 55.942931);
    LongLat start = new LongLat(-3.1861510, 55.944197);
    LongLat start2 = new LongLat(-3.1896190,55.944817);
    LongLat dest2 = new LongLat(-3.1911610, 55.945572);


    Graph g = new Graph(Settings.getDefaultNorthwestBoundLongitude(),
     Settings.getDefaultNorthwestBoundLatitude(), Settings.getDefaultSoutheastBoundLongitude(),
     Settings.getDefaultSoutheastBoundLatitude(), granularity);

  }

}