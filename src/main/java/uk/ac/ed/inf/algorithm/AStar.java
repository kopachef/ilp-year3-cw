package uk.ac.ed.inf.algorithm;

import java.util.*;

public class AStar {
    private Node[][] searchArea;;
    private PriorityQueue<Node> openList;
    private Set<Node> closedSet;
    private Node initialNode;
    private Node finalNode;

    public AStar(Node[][] grid, Node initialNode, Node finalNode) {
        setInitialNode(initialNode);
        setFinalNode(finalNode);
        this.searchArea = grid;
        this.openList = new PriorityQueue<Node>(new Comparator<Node>() {
            @Override
            public int compare(Node node0, Node node1) {
                return Double.compare(node0.getF(), node1.getF());
            }
        });
        setNodes();
        this.closedSet = new HashSet<>();
    }
    private void setNodes() {
        for (int i = 0; i < searchArea.length; i++) {
            for (int j = 0; j < searchArea[0].length; j++) {
                searchArea[i][j].calculateHeuristic(getFinalNode());
            }
        }
    }
    public void setRestrictedAreas(Node[] restrictedNodes) {
        for (int i = 0; i < restrictedNodes.length; i++) {
            int row = restrictedNodes[i].getRow();
            int col = restrictedNodes[i].getCol();
            setRestricted(row, col);
        }
    }
    public List<Node> findPath() {
        openList.add(initialNode);
        while (!isEmpty(openList)) {
            Node currentNode = openList.poll();
            closedSet.add(currentNode);
            if (isFinalNode(currentNode)) {
                return getPath(currentNode);
            } else {
                addAdjacentNodes(currentNode);
            }
        }
        return new ArrayList<Node>();
    }
    private List<Node> getPath(Node currentNode) {
        List<Node> path = new ArrayList<Node>();
        path.add(currentNode);
        Node parent;
        while ((parent = currentNode.getParent()) != null && !path.contains(parent)) {
            path.add(0, parent);
            currentNode = parent;

        }
        return path;
    }
    private void addAdjacentNodes(Node currentNode) {
        addAdjacentUpperRow(currentNode);
        addAdjacentMiddleRow(currentNode);
        addAdjacentLowerRow(currentNode);
    }
    private void addAdjacentLowerRow(Node currentNode) {
        int row = currentNode.getRow();
        int col = currentNode.getCol();
        int lowerRow = row + 1;
        if (lowerRow < getSearchArea().length) {
            if (col - 1 >= 0) {
                double cost = getSearchArea()[lowerRow][col-1].stepCost;
                if(row % 2 == 0) {
                    checkNode(currentNode, col - 1, lowerRow, cost); // Comment this line if diagonal movements are not allowed
                }
            }
            if (col + 1 < getSearchArea()[0].length) {
                double cost = getSearchArea()[lowerRow][col+1].stepCost;
                if(row % 2 == 1) {
                    checkNode(currentNode, col + 1, lowerRow, cost); // Comment this line if diagonal movements are not allowed
                }
            }
            double cost = getSearchArea()[lowerRow][col].stepCost;
            checkNode(currentNode, col, lowerRow, cost);
        }
    }
    private void addAdjacentMiddleRow(Node currentNode) {
        int row = currentNode.getRow();
        int col = currentNode.getCol();
        int middleRow = row;
        if (col - 1 >= 0) {
            double cost = getSearchArea()[middleRow][col-1].stepCost;
            checkNode(currentNode, col - 1, middleRow, cost);
        }
        if (col + 1 < getSearchArea()[0].length) {
            double cost = getSearchArea()[middleRow][col+1].stepCost;
            checkNode(currentNode, col + 1, middleRow, cost);
        }
    }

    private void addAdjacentUpperRow(Node currentNode) {
        int row = currentNode.getRow();
        int col = currentNode.getCol();
        int upperRow = row - 1;
        if (upperRow >= 0) {
            if (col - 1 >= 0) {
                double cost = getSearchArea()[upperRow][col-1].stepCost;
                if(row % 2 == 0) {
                    checkNode(currentNode, col - 1, upperRow, cost); // Comment this if diagonal movements are not allowed
                }
            }
            if (col + 1 < getSearchArea()[0].length) {
                double cost = getSearchArea()[upperRow][col+1].stepCost;
                if(row % 2 == 1) {
                    checkNode(currentNode, col + 1, upperRow, cost); // Comment this if diagonal movements are not allowed
                }
            }

            double cost = getSearchArea()[upperRow][col].stepCost;
            checkNode(currentNode, col, upperRow, cost);
        }
    }

    /* --- add a check here to make the formed line segment not intersect a  restricetd area ----n*/
    private void checkNode(Node currentNode, int col, int row, double cost) {
        Node adjacentNode = getSearchArea()[row][col];
        if (!adjacentNode.isRestricted() && !getClosedSet().contains(adjacentNode)) {
            if (!getOpenList().contains(adjacentNode)) {
                adjacentNode.setNodeData(currentNode, cost);
                getOpenList().add(adjacentNode);
            } else {
                boolean changed = adjacentNode.lookUpBetterPath(currentNode, cost);
                if (changed) {
                    // Remove and Add the changed node, so that the PriorityQueue can sort again its
                    // contents with the modified "finalCost" value of the modified node
                    getOpenList().remove(adjacentNode);
                    getOpenList().add(adjacentNode);
                }
            }
        }
    }

    private boolean isFinalNode(Node currentNode) {
        return currentNode.equals(finalNode);
    }

    private boolean isEmpty(PriorityQueue<Node> openList) {
        return openList.size() == 0;
    }

    private void setRestricted(int row, int col) {
        this.searchArea[row][col].setRestricted(true);
    }

    public Node getInitialNode() {
        return initialNode;
    }

    public void setInitialNode(Node initialNode) {
        this.initialNode = initialNode;
    }

    public Node getFinalNode() {
        return finalNode;
    }

    public void setFinalNode(Node finalNode) {
        this.finalNode = finalNode;
    }

    public Node[][] getSearchArea() {
        return searchArea;
    }

    public void setSearchArea(Node[][] searchArea) {
        this.searchArea = searchArea;
    }

    public PriorityQueue<Node> getOpenList() {
        return openList;
    }

    public void setOpenList(PriorityQueue<Node> openList) {
        this.openList = openList;
    }

    public Set<Node> getClosedSet() {
        return closedSet;
    }

    public void setClosedSet(Set<Node> closedSet) {
        this.closedSet = closedSet;
    }
}
