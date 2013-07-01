package org.karolgurecki.rbf;

public class Point {
private double[] coordinates;
    public Point(double... coordinates) {
        this.coordinates = coordinates.clone();
    }

    public double getCoordinate(int index) {
        if (coordinates.length <= index)
            throw new IllegalArgumentException("Point doesn't contain " + index
                    + " coordinate.");
        if (index < 0)
            throw new IllegalArgumentException("Index cannot be negative.");
        return coordinates[index];
    }

    public double distance(Point point) {
        int length = Math.min(point.coordinates.length, coordinates.length);
        double ret = .0;
        for (int i = 0; i < length; i++)
            ret += Math.pow(point.getCoordinate(i) - coordinates[i],2);
        return Math.sqrt(ret);
    }

    public String toString() {
        StringBuilder build = new StringBuilder();
        for (double coordinate : coordinates) {
            build.append(coordinate);
            build.append(" ");
        }
        return build.toString();
    }
}
