package com.esiee.hmi.model.countries;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Model class used to store regions and sub regions of the world as defined in Natural Earth Json file.
 */
public class WorldRegions
{
    // --------------------------------------------
    // ATTRIBUTES
    // --------------------------------------------
    private final Set<String> regionValues;

    private final Map<String, List<String>> subRegionsMap;

    // --------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------
    /**
     * Create a WorldRegions instance from the parsed countries.
     *
     * @param parsedCountries the list of countries parsed from Natural Earth JSON file
     */
    public WorldRegions(List<Country> parsedCountries)
    {
        regionValues = parsedCountries.stream()
                .map(Country::getRegion)
                .sorted()
                .collect(Collectors.toCollection(TreeSet<String>::new));

        subRegionsMap = regionValues.stream()
                .collect(
                        Collectors.toMap(
                                // Key Mapper : region
                                Function.identity(),
                                // Value Mapper : list of sub regions for region
                                r -> parsedCountries.stream().filter(c -> c.getRegion().equals(r))
                                        .map(Country::getSubRegion)
                                        .distinct()
                                        .sorted()
                                        .collect(Collectors.toList()),
                                // Merge function : No duplicate so no merge...
                                (t, u) -> t,
                                // Supplier
                                TreeMap::new));
    }

    // --------------------------------------------
    // METHODS
    // --------------------------------------------
    /**
     * Retrieve the different region names.
     *
     * @return The regions of the world
     */
    public Set<String> getRegionValues()
    {
        return regionValues;
    }

    /**
     * Retrieve a map with region names as keys and list of "sub regions names as values".
     *
     * @return a map with region names as keys and list of "sub regions names as values"
     */
    public Map<String, List<String>> getSubRegionsMap()
    {
        return subRegionsMap;
    }
}
