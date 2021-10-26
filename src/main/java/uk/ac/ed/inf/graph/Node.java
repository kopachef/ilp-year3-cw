package uk.ac.ed.inf.graph;

import uk.ac.ed.inf.LongLat;

import java.util.ArrayList;

public class Node {
    public double stepCost = 5;
    public int  row, col;
    double g, h, f;
    private LongLat longLat;
    private boolean isRestricted;
    private Node parent;

    public Node(int row, int col) {
        super();
        this.col = col;
        this.row = row;
    }
    public LongLat getLongLat() {
        return longLat;
    }
    public void setLongLat(LongLat longLat) {
        this.longLat = longLat;
    }
    public void calculateHeuristic(Node destination) {
        this.h = this.longLat.distanceTo(destination.getLongLat());
    }
    public void setNodeData(Node currentNode, double cost) {
        double gTotal = currentNode.getG() + cost;
        setParent(currentNode);
        setG(gTotal);
        calculateTotalCost();
    }
    public void calculateTotalCost() {
        double totalCost = getG() + getH();
        setF(totalCost);
    }

    public boolean lookUpBetterPath(Node currentNode, double cost) {
        double gTotal = currentNode.getG() + cost;
        if(gTotal < getG()) {
            setNodeData(currentNode, cost);
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object node) {
        Node inputNode = (Node) node;
        return this.getRow() == inputNode.getRow() &&
                this.getCol() == inputNode.getCol() &&
                inputNode.getLongLat().equals(this.getLongLat());
    }

    @Override
    public String toString() {
        return "Node [row = " + row + ", col = " + col + "]\n" + longLat.toString()+"\n";
    }

    public int getRow() {
        return this.row;
    }
    public int getCol() {
        return this.col;
    }

    public double getG() {
        return this.g;
    }
    public double getH() {
        return this.h;
    }
    public void setG(double g) {
        this.g = g;
    }
    public void setH(double h) {
        this.h = h;
    }
    public Node getParent() {
        return parent;
    }
    public void setParent(Node parent) {
        this.parent = parent;
    }
    public void setF(double f) {
        this.f = f;
    }
    public double getF() {
        return this.f;
    }

    public boolean isRestricted() {
        return isRestricted;
    }
    public void setRestricted(boolean restricted) {
        isRestricted = restricted;
    }
}
