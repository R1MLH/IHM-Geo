package com.esiee.hmi.dataaccess;

import com.esiee.hmi.dataaccess.ne.RawCountry;
import com.esiee.hmi.model.countries.Country;
import com.esiee.hmi.model.countries.Geometry;
import com.esiee.hmi.model.countries.Polygon;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class dedicated to parse GeoJSON data. Data from Natural Earth : http://www.naturalearthdata.com Conversion to JSON
 * format done online with : http://newconverter.mygeodata.eu
 */
public final class NEGeoJsonDecoder
{
    // --------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------

    /**
     * Utility class empty private constructor.
     */
    private NEGeoJsonDecoder()
    {
        // Empty on purpose: empty private constructor for utility class
    }

    // --------------------------------------------
    // METHODS
    // --------------------------------------------

    /**
     * Parse a Natural Earth GeoJSON file create a list of {@link Country} objects.
     *
     * @param file the GeoJSON file to parse
     * @return the list of parsed data
     * @throws IOException if the given file cannot be parsed
     */
    public static List<Country> decode(File file) throws IOException
    {
        return NEGeoJsonDecoder.convert(NEGeoJsonDecoder.parseFile(file));
    }

    /**
     * Parse a GeoJSON file with Jackson and create a list of {@link RawCountry} objects.
     *
     * @param file the JSON file to parse
     * @return the list of parsed data
     * @throws IOException if the given file cannot be parsed
     */
    private static List<RawCountry> parseFile(File file) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

        return mapper.readValue(file, new TypeReference<List<RawCountry>>()
        {
        });
    }

    /**
     * Convert Jackson library decoded data ({@link RawCountry}) into the application data model ({@link Country}).
     *
     * @param in the list of {@link RawCountry} decoded instances
     * @return a list of converted {@link Country} instances
     */
    private static List<Country> convert(Collection<RawCountry> in)
    {
        return in.stream()
            // .filter(c -> c.properties.scalerank < 10) Not very useful considering data
            .map(c ->
                 {
                     Geometry geometry = new Geometry();
                     if ("Polygon".equals(c.geometry.type))
                     {
                         List<Object> jsonPoly =
                             c.geometry.coordinates.get(0); // 0 is the base polygon; others are holes

                         Polygon poly = new Polygon(jsonPoly);
                         geometry.addPolygon(poly);
                     }
                     else // Multi polygons
                     {
                         int count = c.geometry.coordinates.size();

                         for (int i = 0; i < count; i++)
                         {
                             List<Object> jsonPoly = (List<Object>)
                                 c.geometry.coordinates.get(i).get(0); // 0 is the base polygon; others are holes

                             Polygon poly = new Polygon(jsonPoly);
                             geometry.addPolygon(poly);
                         }
                     }

                     return new Country.Builder()
                         .withName(c.properties.admin)
                         .withIsoCode(c.properties.iso_a3)
                         .withRegion(c.properties.region_wb)
                         .withSubRegion(c.properties.subregion)
                         .withGeometry(geometry)
                         .build();
                 }).
                collect(Collectors.toList());
    }
}
