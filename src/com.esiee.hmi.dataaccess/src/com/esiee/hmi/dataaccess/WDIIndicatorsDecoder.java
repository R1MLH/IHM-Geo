package com.esiee.hmi.dataaccess;

import com.esiee.hmi.dataaccess.wdi.RawIndicator;
import com.esiee.hmi.model.indicators.EIndicatorType;
import com.esiee.hmi.model.indicators.Indicator;
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

/**
 * Class decoding data contained in the WDI_Series CSV file: data are information about each indicator.
 *
 * <b>File content must be UTF-8 encoded for Jackson MappingIterator.</b><br>
 */
public final class WDIIndicatorsDecoder
{
    private static final Logger LOGGER = LoggerFactory.getLogger(WDIIndicatorsDecoder.class);

    // --------------------------------------------
    // CONSTANTS
    // --------------------------------------------
    private static final String NO_SUB_TOPIC = "General";

    private static final String EMPTY_TOPIC = "General";
    // --------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------

    /**
     * Utility class empty private constructor.
     */
    private WDIIndicatorsDecoder()
    {
        // Empty on purpose: empty private constructor for utility class
    }

    // --------------------------------------------
    // METHODS
    // --------------------------------------------

    /**
     * Parse a WDI Series CSV file to find all indicators it contains.
     *
     * @param file the file to parse
     * @return a list of all indicators
     * @throws IOException if the given file cannot be parsed
     */
    public static List<Indicator> decode(File file) throws IOException
    {
        long in = System.nanoTime();

        List<Indicator> result = convert(parseFile(file));

        long out = System.nanoTime();

        LOGGER.info("Indicators list decoding time = {} ms.", (out - in) / (1000 * 1000));

        return result;
    }

    /**
     * Parse a CSV file with Jackson and create a list of {@link RawIndicator} objects.
     *
     * @param file the CSV file to parse
     * @return the list of parsed data
     * @throws IOException if the given file cannot be parsed
     */
    private static MappingIterator<RawIndicator> parseFile(File file) throws IOException
    {
        CsvSchema headerSchema = CsvSchema.emptySchema().withHeader();
        CsvMapper mapper = new CsvMapper();

        return mapper.readerFor(RawIndicator.class).
            with(headerSchema).
            without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).
            readValues(file);
    }

    /**
     * Convert Jackson library decoded data ({@link RawIndicator}) into the application data model ({@link Indicator}).
     *
     * @param in the list of {@link RawIndicator} decoded instances
     * @return a list of converted {@link Indicator} instances
     * @throws IOException if the iterator cannot be closed
     */
    private static List<Indicator> convert(MappingIterator<RawIndicator> in) throws IOException
    {
        List<Indicator> result = new ArrayList<>();

        try (in)
        {
            while (in != null && in.hasNext())
            {
                RawIndicator indic = in.next();

                // Topic and subtopic
                String[] split = indic.topic.split(":");

                String topic = split[0];
                if (topic != null && topic.isEmpty())
                {
                    topic = EMPTY_TOPIC;
                }

                String subTopic = split.length == 2 ? split[1] : NO_SUB_TOPIC;
                subTopic = subTopic.trim();

                // Type
                EIndicatorType type;
                if (indic.name.contains("%"))
                {
                    type = EIndicatorType.PERCENTAGE;
                }
                else
                {
                    type = EIndicatorType.VALUES;
                }

                // Indicator creation
                Indicator indicator = new Indicator.Builder()
                    .withTopic(topic)
                    .withSubTopic(subTopic)
                    .withName(indic.name)
                    .withCode(indic.code)
                    .withType(type)
                    .build();

                result.add(indicator);
            }
        }

        return result;
    }
}
