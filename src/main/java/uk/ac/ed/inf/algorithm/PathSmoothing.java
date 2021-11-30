package uk.ac.ed.inf.algorithm;

import uk.ac.ed.inf.dataio.GeoJsonManager;
import uk.ac.ed.inf.LongLat;
import uk.ac.ed.inf.utils.Settings;

import com.google.common.annotations.VisibleForTesting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * The PathSmoothing class contains a number of helper functions used to smooth out a given path. Initially,
 * our shortest path will be a list of nodes which a restricted to a shorter list of distances and angles between them.
 * The angle constraints are discussed in the report. Path smoothing allows us to ahve more directed and smoother paths
 * between nodes in based on their usages(Node Usages).
 *
 */
public class PathSmoothing {

  private static final Object lock = new Object();
  private final int smoothCycles;
  private final HashMap<LongLat, LongLat> nodeToTargteMapping;

  public PathSmoothing(HashMap<LongLat, LongLat> nodeToLongLatMapping, int smoothCycles) {
    this.nodeToTargteMapping = nodeToLongLatMapping;
    this.smoothCycles = smoothCycles;
  }

  /**
   * The reassignTrueNodes() method iterates through a path list, checking each node's usage. If the node is not of
   * the ORDINARY usage, the current node is saved temporarily, the oldLocation is retrieved from the node, and the
   * trueLocation is retrieved from the nodeToTargetMapping. The current node is then set to the trueLocation and the
   * path list is updated. The method then returns the updated path list.
   *
   * This moves away from considering the closest node as the target location to instead using the actual target
   * location(e.g a pick or drop off location) as the destination.
   *
   * @param path List of nodes to filtered
   * @return updated node list
   */
  @VisibleForTesting
  private List<Node> reassignTrueNodes(List<Node> path) {
    for (int i = 0; i < path.size(); i++) {
      if (path.get(i).getUsage() != Node.NodeUsage.ORDINARY) {
        Node current = path.get(i);
        LongLat oldLocation = current.getLongLat();
        LongLat trueLocation = nodeToTargteMapping.get(oldLocation);
        current.setLongLat(trueLocation);
        path.set(i, current);
      }
    }
    return path;
  }

  /**
   * The midNodeRemovalCycles() method determines the number of cycles in a path and removes the middle node
   * of each cycle. A cycle in this case is defined as three contiguous nodes such that the first and last node are
   * within the same line of sight which in turn makes are second node redundant as we can simply connect the
   * first and last node. e.g. if a -> b -> c and c and accessible from a then this is the equivalence of writing
   * a -> c. Additionally, we have a smoothCycles variable which determines how many times we perform this operation.
   * A more complicated example is shown below.
   *
   * a -> b -> c -> d -> e
   *
   * if a is in LOS(line of sight) of e then:
   *
   * cycle 1:
   *      a -> b -> c -> e
   * cycle 2:
   *      a -> b -> e
   *
   * Thus we don't completely eliminate such cycles but we minimise how many times we do this to make it easier to
   * correct for angles and distances between nodes in further operations.
   *
   * @param path
   * @return
   */
  @VisibleForTesting
  private List<Node> midNodeRemovalCycles(List<Node> path) {
    int cycleCount = smoothCycles;
    while (cycleCount > 0) {
      for (int j = 0; j < path.size() - 2; j++) {
        if (!GeoJsonManager.crossesRestricedArea(
                path.get(j).getLongLat(), path.get(j + 2).getLongLat())
            && (path.get(j + 1).getUsage() == Node.NodeUsage.ORDINARY)) {
          path.remove(j + 1);
        }
      }
      cycleCount -= 1;
    }
    return path;
  }

  /**
   * This method calculates the distance and angle between two nodes in a path, and then adjusts the path accordingly
   * to meet the distance and angle constraints.
   * The path is adjusted by moving the nodes closest to the calculated angle, and by only moving nodes that are not
   * already in a restricted area or confinement zone. If a generated nodes falls within a restricted area, we keep on
   * regenerating nodes that meet our constraints until a valid nodes is found.
   *
   * @param path
   * @return angle and distance adjusted path
   */
  @VisibleForTesting
  private List<Node> pathDistanceAngleReadjustment(List<Node> path) {
    for (int i = 0; i < path.size() - 1; i++) {
      Node curr = path.get(i);
      Node next = path.get(i + 1);

      double bearing = curr.getLongLat().calculateBearing(next.getLongLat());
      double distance = curr.getLongLat().distanceTo(next.getLongLat());

      int roundedAngle = GraphUtils.roundOffToNearest10th(bearing);
      double roundedDistance =
          GraphUtils.roundOffToNearestMultiple(distance, Settings.getDefaultMovementStepDistance());
      int stepCount = (int) (roundedDistance / Settings.getDefaultMovementStepDistance());

      LongLat pos1 = curr.getLongLat();
      LongLat pos2 = curr.getLongLat();
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

      while (stepCount > 0) {
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
        stepCount -= 1;
      }
      List<LongLat> positions =
          Arrays.asList(pos1, pos2, pos3, pos4, pos5, pos6, pos7, pos8, pos9, pos10, pos11);
      for (LongLat l : positions) {
        if (!GeoJsonManager.crossesRestricedArea(curr.getLongLat(), l)
            && !GeoJsonManager.isInRestrictedArea(l)
            && GeoJsonManager.isInConfinementZone(l)) {
          next.setLongLat(l);
          break;
        }
      }
      path.set(i + 1, next);
    }
    return path;
  }

  /**
   * This method calculates a path between two nodes, taking into account the movement step distance and bearing. if
   * the number of nodes that can be fit between two nodes is greater than one, we populate this space with
   * additional nodes to ensure each node is always one-step distance away from the next. This is done because the drone
   * can only take one step at a time and we have already ensured that the distance between ndoes a multiple of a single
   * step. We just need to make sure that each step taken moves us from one node to the next.
   *
   * @param path
   * @return populated path list
   */
  @VisibleForTesting
  private List<Node> pathReExpansion(List<Node> path) {
    Node lastNode = path.get(path.size() - 1);
    List<Node> resultantPath = new ArrayList<>();
    for (int i = 1; i < path.size(); i++) {
      resultantPath.add(path.get(i - 1));
      int distanceToStepRatio =
          (int)
              Math.round(
                  path.get(i - 1).getLongLat().distanceTo(path.get(i).getLongLat())
                      / Settings.getDefaultMovementStepDistance());
      int angle =
          (int) Math.round(path.get(i - 1).getLongLat().calculateBearing(path.get(i).getLongLat()));

      while (distanceToStepRatio > 1) {
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

  /**
   * This method calculates a corrected path by walking the path and correcting any differences in usage or
   * longitude/latitude between nodes. So far, all nodes are treated the same i.e. as transit nodes. We do not
   * want this behaviour on special nodes(Node which are not ordinary) for example rather than changing the position
   * of the drone at a PICKUP Node, we just want to perform a Hover. To achieve this, we can look for these special
   * nodes and create a copy of them with the copy being assigned Ordinary Usage. This movement won't have to be
   * performed on a special node but instead we will use the ordinary node to get to the special node and use the
   * special node to perform the Hover action. Overall we are splitting the responsibility of a single node it
   * two steps.
   *
   *
   * @param path
   * @return node action corrected path
   */
  @VisibleForTesting
  private List<Node> nodeActionCorrection(List<Node> path) {
    for (int i = 1; i < path.size(); i++) {
      if ((path.get(i).getUsage() != Node.NodeUsage.ORDINARY)
          && (path.get(i - 1).getLongLat() != path.get(i).getLongLat())) {
        Node current = null;
        try {
          current = path.get(i).clone();
        } catch (CloneNotSupportedException e) {
          e.printStackTrace();
        }
        current.setNodeUsage(Node.NodeUsage.ORDINARY);
        path.add(i, current);
      }
      if (path.get(i).getUsage() == path.get(i - 1).getUsage()
          && (path.get(i).getUsage() == Node.NodeUsage.ORDINARY)
          && (path.get(i).getLongLat().distanceTo(path.get(i - 1).getLongLat()) == 0.0)) {
        path.remove(i);
      }
    }
    return path;
  }

  /**
   * The removeStationaryOrdinaryMoves() method removes all the nodes in the path that have an ordinary usage and
   * are located at the same longitude and latitude. These are considered redundant as no action is performed at them
   * and they do not contribute to the path.
   *
   * @param path
   * @return path without redundant ordinary nodes
   */
  @VisibleForTesting
  private List<Node> removeStationaryOrdinaryMoves(List<Node> path) {
    for (int i = 1; i < path.size(); i++) {
      if (path.get(i).getUsage() == Node.NodeUsage.ORDINARY
          && (path.get(i - 1).getLongLat().distanceTo(path.get(i).getLongLat())) == 0) {
        path.remove(i);
      }
    }
    return path;
  }

  /**
   * The smoothenPath() method cleans up a given path, removing unnecessary nodes and cycles, and adjusting the
   * distances and angles between nodes.
   *
   * @param path
   * @return smoothed out path
   */
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
