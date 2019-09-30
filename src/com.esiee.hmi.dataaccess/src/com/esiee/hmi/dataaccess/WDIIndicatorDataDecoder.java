package com.esiee.hmi.dataaccess;

import com.esiee.hmi.dataaccess.wdi.RawIndicatorData;
import com.esiee.hmi.model.indicators.IndicatorData;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Jackson based decoder class used to parse data for a specific indicator.<br>
 * * <b>CAUTION: Data file path is hard coded to: ./data/WDI/WDI_Data-utf8.csv</b>
 */
public final class WDIIndicatorDataDecoder
{
    private static final Logger LOGGER = LoggerFactory.getLogger(WDIIndicatorDataDecoder.class);

    // --------------------------------------------
    // CONSTANTS
    // --------------------------------------------

    private static final File INDICATORS_DATA_FILE = new File("./data/WDI/WDIData.csv");

    // --------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------

    /**
     * Utility class empty private constructor.
     */
    private WDIIndicatorDataDecoder()
    {
        // Empty on purpose: empty private constructor for utility class
    }

    // --------------------------------------------
    // METHODS
    // --------------------------------------------

    /**
     * Parse the WDI Data CSV file to retrieve data for the given indicator code: for each country, an
     * {@link IndicatorData} containing the indicator data for all years is retrieved.
     *
     * @param indicatorCode the indicator code to parse, e.g. AG.AGR.TRAC.NO
     * @return a list of {@link IndicatorData} instances for each country
     * @throws IOException if the given file cannot be parsed
     */
    public static List<IndicatorData> decode(String indicatorCode) throws IOException
    {
        LOGGER.info("Start decoding: {}", indicatorCode);

        long in = System.nanoTime();

        List<IndicatorData> result = convert(parseFile(INDICATORS_DATA_FILE), indicatorCode);

        long out = System.nanoTime();

        LOGGER.info("Data decoding time = {} ms.", (out - in) / (1000 * 1000));

        return result;
    }

    /**
     * Parse a CSV file with Jackson and create a list of {@link RawIndicatorData} objects.
     *
     * @param file the CSV file to parse
     * @return the list of parsed data
     * @throws IOException if the given file cannot be parsed
     */
    private static MappingIterator<RawIndicatorData> parseFile(File file) throws IOException
    {
        CsvSchema headerSchema = CsvSchema.emptySchema().withHeader();
        CsvMapper mapper = new CsvMapper();

        return mapper.readerFor(RawIndicatorData.class).
            with(headerSchema).
            without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).
            readValues(file);
    }

    /**
     * Convert Jackson library decoded data ({@link RawIndicatorData}) into the application data model ({@link
     * IndicatorData}).
     *
     * @param in            the list of {@link RawIndicatorData} decoded instances
     * @param indicatorCode the code of the indicator to get data
     * @return a list of converted {@link IndicatorData} instances
     * @throws IOException if the iterator cannot be closed
     */
    private static List<IndicatorData> convert(MappingIterator<RawIndicatorData> in, String indicatorCode)
        throws IOException
    {
        List<IndicatorData> result = new ArrayList<>();

        try (in)
        {

            while (in != null && in.hasNext())
            {
                RawIndicatorData data = in.next();

                if (data.indicatorCode.equals(indicatorCode))
                {
                    result.add(new IndicatorData(indicatorCode, data.countryCode,
                                                 WDIIndicatorDataDecoder.buildValuesArray(data)));
                }

            }
        }

        return result;
    }

    private static double[] buildValuesArray(RawIndicatorData data)
    {
        return Stream.of(
            data.value1960, data.value1961, data.value1962, data.value1963, data.value1964,
            data.value1965, data.value1966, data.value1967, data.value1968, data.value1969,

            data.value1970, data.value1971, data.value1972, data.value1973, data.value1974,
            data.value1975, data.value1976, data.value1977, data.value1978, data.value1979,

            data.value1980, data.value1981, data.value1982, data.value1983, data.value1984,
            data.value1985, data.value1986, data.value1987, data.value1988, data.value1989,

            data.value1990, data.value1991, data.value1992, data.value1993, data.value1994,
            data.value1995, data.value1996, data.value1997, data.value1998, data.value1999,

            data.value2000, data.value2001, data.value2002, data.value2003, data.value2004,
            data.value2005, data.value2006, data.value2007, data.value2008, data.value2009,

            data.value2010, data.value2011, data.value2012, data.value2013, data.value2014,
            data.value2015, data.value2016, data.value2017, data.value2018).
            mapToDouble(WDIIndicatorDataDecoder::valueStringToDouble).
            toArray();
    }

    private static double valueStringToDouble(String valueString)
    {
        if (valueString == null || valueString.isEmpty())
        {
            return Double.NaN;
        }
        else
        {
            return Double.parseDouble(valueString);
        }
    }
}
