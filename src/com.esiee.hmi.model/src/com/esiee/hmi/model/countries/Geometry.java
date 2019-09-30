package com.esiee.hmi.model.countries;

import java.util.ArrayList;
import java.util.List;

/**
 * Geometry class which is in fact a list of polygons.
 */
public class Geometry
{
    private final List<Polygon> polygons = new ArrayList<>();

    public void addPolygon(Polygon toAdd)
    {
        polygons.add(toAdd);
    }

    public List<Polygon> getPolygons()
    {
        return polygons;
    }
}
