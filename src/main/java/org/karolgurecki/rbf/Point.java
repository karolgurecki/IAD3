package org.karolgurecki.rbf;

import java.util.Arrays;

public class Point {
private double[] coordinates;
    public Point(double... coordinates) {
        this.coordinates = coordinates.clone();
    }

    /**
     * Zwraca rozmiar punktu (ile ma wymiarow)
     *
     * @return rozmiar punktu
     */
    public int getSize() {
        return coordinates.length;
    }

    /**
     * Zwraca wspolrzedna punktu o numerze index
     *
     * @param index nr wspolrzednej
     * @return wspolrzedna punktu
     */
    public double getCoordinate(int index) {
        if (coordinates.length <= index)
            throw new IllegalArgumentException("Point doesn't contain " + index
                    + " coordinate.");
        if (index < 0)
            throw new IllegalArgumentException("Index cannot be negative.");
        return coordinates[index];
    }

    /**
     * Zwraca tablice zawierajaca wspolrzedne punktu
     *
     * @return
     */
    public double[] getCoordinates() {
        return coordinates.clone();
    }

    /**
     * Ustawia wspolrzedna punktu index (o ile punkt ja posaida) na wartosc
     * value
     *
     * @param index numer wspolrzendje punktu
     * @param value wartosc
     */
    public void setCoordinate(int index, double value) {
        if (coordinates.length <= index)
            throw new IllegalArgumentException("Point doesn't contain " + index
                    + " coordinate.");
        if (index < 0)
            throw new IllegalArgumentException("Index cannot be negative.");
        coordinates[index] = value;
    }

    /**
     * Ustawia nowe wspolrzedne punktu. Nie da sie w ten sposob zmienic wymiaru
     * punktu. Jesli tablica z nowymi wspolrzednymi jest dluzsza od wymniaru
     * punktu to obcina nadmiarowe wartosci, jesli jest krotsza to pozostale
     * wspolrzedne wypelnia zerami.
     *
     * @param coordinates
     */
    public void setCoordinates(double[] coordinates) {
        this.coordinates = Arrays.copyOf(coordinates, this.coordinates.length);
    }

    /**
     * Oblicza euklidesowska odleglosc miedzy dowam punktami
     *
     * @param point
     * @return euklidesowska odleglosc
     */
    public double distance(Point point) {
        int length = Math.min(point.coordinates.length, coordinates.length);
        double ret = .0;
        for (int i = 0; i < length; i++)
            ret += Math.pow(point.getCoordinate(i) - coordinates[i],2);
        return Math.sqrt(ret);
    }

    public String toString() {
        StringBuilder build = new StringBuilder();
        for (int i = 0; i < coordinates.length; i++) {
            build.append(coordinates[i]);
            build.append(" ");
        }
        return build.toString();
    }
}
