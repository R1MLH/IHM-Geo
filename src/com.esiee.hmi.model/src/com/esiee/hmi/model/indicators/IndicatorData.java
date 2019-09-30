package com.esiee.hmi.model.indicators;

/**
 * Data model class representing <b>data values</b> for one World Data Index indicator and one country.
 */
public class IndicatorData
{
    // --------------------------------------------
    // CONSTANTS
    // --------------------------------------------

    /**
     * Year of the first value of the values array.
     */
    public static final int FIRST_YEAR = 1960;

    /**
     * Year of the last value of the values array.
     */
    public static final int LAST_YEAR = 2018;

    // --------------------------------------------
    // ATTRIBUTES
    // --------------------------------------------
    private final String indicatorCode;

    private final String countryCode;

    private final double[] values;

    // --------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------

    /**
     * Creates a new instance of {@link IndicatorData} with the given data.
     *
     * @param indicatorCode The indicator code
     * @param countryCode   The country to which these data apply
     * @param values        The indicator data
     */
    public IndicatorData(String indicatorCode, String countryCode, double[] values)
    {
        this.indicatorCode = indicatorCode;
        this.countryCode = countryCode;
        this.values = values;
    }

    // --------------------------------------------
    // GETTERS AND SETTERS
    // --------------------------------------------

    public String getIndicatorCode()
    {
        return indicatorCode;
    }

    public String getCountryCode()
    {
        return countryCode;
    }

    public double[] getValues()
    {
        return values;
    }

    // --------------------------------------------
    // METHODS
    // --------------------------------------------

    /**
     * Retrieves the value for the given year.
     *
     * @param year the year of the value to retrieve
     * @return the value for the given year
     */
    public double getValueForYear(int year)
    {
        if (year < FIRST_YEAR || year > LAST_YEAR)
        {
            throw new IllegalArgumentException(
                "Year not in valid bounds [" + FIRST_YEAR + "; " + LAST_YEAR + "]: " + year);
        }

        return values[year - FIRST_YEAR];
    }
}
