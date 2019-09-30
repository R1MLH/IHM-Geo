package com.esiee.hmi.model.countries;

/**
 * Simple lon/lat point model.
 */
public class GeoPoint
{
    public double lon;

    public double lat;

    /**
     * Create a new GeoPoint instance.
     *
     * @param lon longitude in decimal degrees
     * @param lat latitude in decimal degrees
     */
    public GeoPoint(double lon, double lat)
    {
        this.lon = lon;
        this.lat = lat;
    }
}
