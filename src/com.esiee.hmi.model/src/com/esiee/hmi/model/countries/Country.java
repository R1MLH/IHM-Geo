package com.esiee.hmi.model.countries;

/**
 * Model class for countries.
 */
public class Country
{
    // --------------------------------------------
    // ATTRIBUTES
    // --------------------------------------------
    private String name;

    private String region;

    private String subRegion;

    private String isoCode;

    private Geometry geometry;

    // --------------------------------------------
    // GETTERS AND SETTERS
    // --------------------------------------------
    public Geometry getGeometry()
    {
        return geometry;
    }

    public String getName()
    {
        return name;
    }

    public String getRegion()
    {
        return region;
    }

    public String getIsoCode()
    {
        return isoCode;
    }

    public String getSubRegion()
    {
        return subRegion;
    }

    // --------------------------------------------
    // METHODS
    // --------------------------------------------

    @Override
    public String toString()
    {
        return "Country{"
               + "name='" + name + '\''
               + ", region='" + region + '\''
               + ", subRegion='" + subRegion + '\''
               + ", isoCode='" + isoCode + '\''
               + ", polygons='" + geometry.getPolygons().size() + '\''
               + '}';
    }

    // --------------------------------------------
    // INNER CLASSES
    // --------------------------------------------
    public static final class Builder
    {
        private String name;

        private String region;

        private String subRegion;

        private String isoCode;

        private Geometry geometry;

        public Builder()
        {
        }

        public Builder withName(final String name)
        {
            this.name = name;
            return this;
        }

        public Builder withRegion(final String region)
        {
            this.region = region;
            return this;
        }

        public Builder withSubRegion(final String subRegion)
        {
            this.subRegion = subRegion;
            return this;
        }

        public Builder withIsoCode(final String isoCode)
        {
            this.isoCode = isoCode;
            return this;
        }

        public Builder withGeometry(final Geometry geometry)
        {
            this.geometry = geometry;
            return this;
        }

        public Country build()
        {
            final Country result = new Country();
            result.name = name;
            result.region = region;
            result.subRegion = subRegion;
            result.isoCode = isoCode;
            result.geometry = geometry;
            return result;
        }
    }
}
