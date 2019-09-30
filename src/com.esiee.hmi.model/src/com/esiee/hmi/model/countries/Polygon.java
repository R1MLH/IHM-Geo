package com.esiee.hmi.model.countries;

import java.util.List;

/**
 * Simple polygon model.
 */
public class Polygon
{
    public GeoPoint[] points;

    /**
     * Create a new polygon from jackson parsed json content.
     *
     * @param pointsCoordinatesList A list of point coordinates. The coordinates (Object) are actually a list of two
     *                              doubles (List&lt;Double&gt;): a latitude and a longitude.
     */
    public Polygon(List<Object> pointsCoordinatesList)
    {
        points = new GeoPoint[pointsCoordinatesList.size()];
        int count = 0;

        for (Object pointCoordinates : pointsCoordinatesList)
        {
            List<Double> coordinates = (List<Double>) pointCoordinates;
            points[count] = new GeoPoint(coordinates.get(0), coordinates.get(1));
            count++;
        }
    }
}
