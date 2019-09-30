package com.esiee.hmi.model;

import com.esiee.hmi.model.countries.Country;
import com.esiee.hmi.model.countries.WorldRegions;
import com.esiee.hmi.model.indicators.Indicator;
import com.esiee.hmi.model.indicators.IndicatorData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Singleton, following enum pattern, used to easily store and retrieve data.
 */
public enum DataManager
{
    /**
     * Unique instance.
     */
    INSTANCE;

    // --------------------------------------------
    // STATIC ATTRIBUTES
    // --------------------------------------------
    private static final Logger LOGGER = LoggerFactory.getLogger(DataManager.class);

    // --------------------------------------------
    // ATTRIBUTES
    // --------------------------------------------
    private List<Country> countries;

    private Map<String, Country> countriesMap = Collections.emptyMap();

    private WorldRegions regions;

    // Topic, subtopic and indicators.
    private final Map<String, Map<String, List<Indicator>>> indicatorsMap = new TreeMap<>();

    private List<Indicator> indicators;

    private Map<String, IndicatorData> indicatorDataMap = Collections.emptyMap();

    // --------------------------------------------
    // METHODS
    // --------------------------------------------

    /**
     * Retrieve the list of countries.
     *
     * @return a list of all countries
     */
    public List<Country> getCountries()
    {
        return countries;
    }

    /**
     * Store the list of countries in the DataManager instance. The mapping between valid country code and Country is
     * also created.
     *
     * @param countries the countries to store
     */
    public void setCountries(List<Country> countries)
    {
        this.countries = countries;

        countriesMap = countries.stream().
            filter(c -> !"-99".equals(c.getIsoCode())).
            collect(Collectors.toMap(Country::getIsoCode, Function.identity()));

        LOGGER.info("Stored {} countries with valid code ( != -99) ", countriesMap.size());

        regions = new WorldRegions(countries);
    }

    public Optional<Country> getCountryByCode(String code)
    {
        return Optional.ofNullable(countriesMap.get(code));
    }

    /**
     * Retrieve the regions of the world.
     *
     * @return the regions of the world
     */
    public WorldRegions getWorldRegions()
    {
        return regions;
    }

    /**
     * Retrieve the mapping of indicators split in topics and subtopics. First key is the topic, it maps to a map with
     * subtopic as a key and a list of indicators for this topic & subtopic
     *
     * @return the indicators mapping
     */
    public Map<String, Map<String, List<Indicator>>> getIndicatorsMap()
    {
        return indicatorsMap;
    }

    public Stream<Indicator> getIndicators()
    {
        return indicators.stream();
    }

    public void setIndicators(List<Indicator> indicators)
    {
        this.indicators = indicators;

        indicatorsMap.clear();
        for (Indicator ind : indicators)
        {
            // Retrieve the map for the topic
            Map<String, List<Indicator>> topic = indicatorsMap.computeIfAbsent(ind.getTopic(), key -> new TreeMap<>());

            // Next retrieve the sub topic list
            List<Indicator> subtopic = topic.computeIfAbsent(ind.getSubTopic(), key -> new ArrayList<>());
            subtopic.add(ind);
        }
    }

    /**
     * Retrieves the current indicator data for all countries.
     *
     * @return a list of indicator data for all countries
     */
    public Stream<IndicatorData> getCurrentIndicatorData()
    {
        return indicatorDataMap.values().stream();
    }

    /**
     * Retrieves the current indicator data for the given country.
     *
     * @param countryCode The code of the country to retrieve data
     * @return the current indicator data for the given country
     */
    public Optional<IndicatorData> getCurrentIndicatorData(String countryCode)
    {
        return Optional.ofNullable(indicatorDataMap.get(countryCode));
    }

    /**
     * Stores the given indicator data.
     *
     * @param data the indicator data to store
     */
    public void setCurrentIndicatorData(Collection<IndicatorData> data)
    {
        indicatorDataMap = data.stream().collect(Collectors.toMap(IndicatorData::getCountryCode, Function.identity()));
    }
}
