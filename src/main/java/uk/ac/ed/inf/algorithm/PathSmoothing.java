package uk.ac.ed.inf.algorithm;

import org.apache.commons.math3.util.Precision;
import uk.ac.ed.inf.GeoJsonManager;
import uk.ac.ed.inf.LongLat;
import uk.ac.ed.inf.Settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PathSmoothing {

    private int smoothCycles;
    private HashMap<LongLat, LongLat> nodeToTargteMapping;
    private static final Object lock = new Object();

    public PathSmoothing(HashMap<LongLat, LongLat> nodeToLongLatMapping, int smoothCycles) {
        this.nodeToTargteMapping = nodeToLongLatMapping;
        this.smoothCycles = smoothCycles;
    }



    private List<Node> reassignTrueNodes(List<Node> path) {
        for(int i = 0; i < path.size(); i++)  {
            if(path.get(i).getUsage() != Node.NodeUsage.ORDINARY) {
                Node current = path.get(i);
                LongLat oldLocation = current.getLongLat();
                LongLat trueLocation = nodeToTargteMapping.get(oldLocation);
                current.setLongLat(trueLocation);
                path.set(i, current);
            }
        }
        return path;
    }

    private List<Node> midNodeRemovalCycles(List<Node> path) {
        int cycleCount = smoothCycles;
        while (cycleCount > 0) {
            for (int j = 0; j < path.size() - 2; j++) {
                if (!GeoJsonManager.crossesRestricedArea(path.get(j).getLongLat(), path.get(j + 2).getLongLat())
                        && (path.get(j + 1).getUsage() == Node.NodeUsage.ORDINARY)) {
                    path.remove(j + 1);
                }
            }
            cycleCount -= 1;
        }
        return path;
    }

    private List<Node> pathDistanceAngleReadjustment(List<Node> path) {
        for (int i = 0; i < path.size() - 1; i++) {
            Node curr = path.get(i);
            Node next = path.get(i + 1);

            double bearing = curr.getLongLat().calculateBearing(next.getLongLat());
            double distance = curr.getLongLat().distanceTo(next.getLongLat());

            int roundedAngle = Utils.roundOffToNearest10th(bearing);
            double roundedDistance =
                    Utils.roundOffToNearestMultiple(distance, Settings.getDefaultMovementStepDistance());
            int stepCount = (int) (roundedDistance / Settings.getDefaultMovementStepDistance());

            LongLat pos1 = curr.getLongLat();
            LongLat pos2= curr.getLongLat();
            LongLat pos3 = curr.getLongLat();
            LongLat pos4 = curr.getLongLat();
            LongLat pos5 = curr.getLongLat();
            LongLat pos6 = curr.getLongLat();
            LongLat pos7 = curr.getLongLat();
            LongLat pos8 = curr.getLongLat();
            LongLat pos9 = curr.getLongLat();
            LongLat pos10 = curr.getLongLat();
            LongLat pos11 = curr.getLongLat();
            LongLat pos12 = curr.getLongLat();
            LongLat pos13 = curr.getLongLat();

            while(stepCount > 0){
                pos1 = pos1.nextPosition(roundedAngle);
                pos2 = pos2.nextPosition(roundedAngle + 10);
                pos3 = pos3.nextPosition(roundedAngle - 10);
                pos4 = pos4.nextPosition(roundedAngle + 20);
                pos5 = pos5.nextPosition(roundedAngle - 20);
                pos6 = pos6.nextPosition(roundedAngle + 30);
                pos7 = pos7.nextPosition(roundedAngle - 30);
                pos8 = pos8.nextPosition(roundedAngle + 40);
                pos9 = pos9.nextPosition(roundedAngle - 40);
                pos10 = pos10.nextPosition(roundedAngle + 50);
                pos11 = pos11.nextPosition(roundedAngle - 50);
                pos12 = pos12.nextPosition(roundedAngle + 60);
                pos13 = pos13.nextPosition(roundedAngle - 60);
                stepCount-=1;
            }
            List<LongLat> positions = Arrays.asList(pos1, pos2, pos3, pos4, pos5, pos6, pos7, pos8, pos9, pos10, pos11);
            for(LongLat l : positions) {
                if(!GeoJsonManager.crossesRestricedArea(curr.getLongLat(), l) && !GeoJsonManager.isInRestrictedArea(l)
                && GeoJsonManager.isInConfinementZone(l)) {
                    next.setLongLat(l);
                    break;
                }
            }
            path.set(i + 1, next);
        }
        return path;
    }

    private List<Node> pathReExpansion(List<Node> path) {
        Node lastNode = path.get(path.size() - 1);
        List<Node> resultantPath = new ArrayList<>();
        for(int i = 1; i < path.size(); i++) {
            resultantPath.add(path.get(i-1));
            int distanceToStepRatio = (int) Math.round(path.get(i-1).getLongLat().distanceTo(path.get(i).getLongLat())/Settings.getDefaultMovementStepDistance());
            int angle = (int) Math.round(path.get(i-1).getLongLat().calculateBearing(path.get(i).getLongLat()));

            while(distanceToStepRatio > 1) {
                Node current = null;
                try {
                    current = resultantPath.get(resultantPath.size() - 1).clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
                LongLat currentLongLat = current.getLongLat();
                currentLongLat = currentLongLat.nextPosition(angle);
                current.setLongLat(currentLongLat);
                current.setNodeUsage(Node.NodeUsage.ORDINARY);
                resultantPath.add(current);
                distanceToStepRatio -= 1;
            }
        }
        resultantPath.add(lastNode);
        return resultantPath;
    }

    private List<Node> nodeActionCorrection(List<Node> path) {
        for(int i = 1; i < path.size(); i++) {
            if((path.get(i).getUsage() != Node.NodeUsage.ORDINARY)
                && (path.get(i-1).getLongLat() != path.get(i).getLongLat())) {
                Node current = null;
                try {
                    current = path.get(i).clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
                current.setNodeUsage(Node.NodeUsage.ORDINARY);
                path.add(i, current);
            }
            if(path.get(i).getUsage() == path.get(i-1).getUsage() &&
                    (path.get(i).getUsage() == Node.NodeUsage.ORDINARY) &&
                    (path.get(i).getLongLat().distanceTo(path.get(i-1).getLongLat()) == 0.0)) {
                path.remove(i);
            }
        }
        return path;
    }

    private List<Node> removeStationaryOrdinaryMoves(List<Node> path) {
        for(int i = 1; i < path.size(); i++) {
            if(path.get(i).getUsage() == Node.NodeUsage.ORDINARY
                    && (path.get(i - 1).getLongLat().distanceTo(path.get(i).getLongLat())) == 0) {
                System.out.println("removed a node: " + path.get(i) + "usage: " + path.get(i).getUsage());
                path.remove(i);

            }
        };
        return path;
    }

    public List<Node> smoothenPath(List<Node> path) {
        synchronized (lock) {
            path = reassignTrueNodes(path);
            path = midNodeRemovalCycles(path);
            path = pathDistanceAngleReadjustment(path);
            path = nodeActionCorrection(path);
            path = pathReExpansion(path);
            path = removeStationaryOrdinaryMoves(path);
        }
        return path;
    }
}
