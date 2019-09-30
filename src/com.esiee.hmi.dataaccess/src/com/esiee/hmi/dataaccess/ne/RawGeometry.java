package com.esiee.hmi.dataaccess.ne;

import java.util.List;

/**
 * Sample model for Jackson library parsing, inner level for countries geometry.
 */
public class RawGeometry
{
    public String type;

    /**
     * If the geometry is multiple (MultiPolygon), Object is a List of Double. Otherwise, Object is Double. <br>
     * The last list is a list of 2 Double (longitude, latitude). Then it is a list of points. Then it is a list of one
     * base polygon with optional holes. Then if the geometry is multiple, it is a list of sub-geometries.
     */
    public List<List<Object>> coordinates;
}
