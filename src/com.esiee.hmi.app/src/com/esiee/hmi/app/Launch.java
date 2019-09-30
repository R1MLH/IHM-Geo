package com.esiee.hmi.app;

import com.esiee.hmi.dataaccess.NEGeoJsonDecoder;
import com.esiee.hmi.dataaccess.WDIIndicatorsDecoder;
import com.esiee.hmi.model.DataManager;
import com.esiee.hmi.model.countries.Country;
import com.esiee.hmi.model.indicators.Indicator;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

/**
 * Application entry point.
 */
public final class Launch extends Application
{
    private static final Logger LOGGER;

    static
    {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");

        LOGGER = LoggerFactory.getLogger(Launch.class);
    }

    // --------------------------------------------
    // CONSTANTS
    // --------------------------------------------
    private static final String COUNTRIES_FILE = "./data/ne_50m_admin_0_countries.json";

    private static final String INDICATORS_FILE = "./data/WDI/WDISeries.csv";

    // --------------------------------------------
    // METHODS
    // --------------------------------------------
    private static void populateCountries() throws IOException
    {
        // Parse the countries data file
        List<Country> countries = NEGeoJsonDecoder.decode(new File(COUNTRIES_FILE));
        LOGGER.info("Read {} countries.", countries.size());

        // Store in the DataManager
        DataManager.INSTANCE.setCountries(countries);
    }

    private static void populatesIndicators() throws IOException
    {
        // Decode the indicators files
        List<Indicator> indicators = WDIIndicatorsDecoder.decode(new File(INDICATORS_FILE));
        LOGGER.info("Read {} indicators.", indicators.size());

        // Store in the DataManager
        DataManager.INSTANCE.setIndicators(indicators);
    }

    /**
     * Decode and store countries and indicators in the {@link DataManager}.
     */
    public static void initData()
    {
        LOGGER.info("Started application, working dir: {} ", Paths.get("").toAbsolutePath());

        // Countries
        try
        {
            populateCountries();
        }
        catch (IOException e)
        {
            if (LOGGER.isErrorEnabled())
            {
                LOGGER.error("Error during Natural Earth countries parsing.", e);
            }
        }

        // Indicators
        try
        {
            populatesIndicators();
        }
        catch (IOException e)
        {
            if (LOGGER.isErrorEnabled())
            {
                LOGGER.error("Error during WDI indicators parsing.", e);
            }
        }
    }

    // --------------------------------------------
    // MAIN METHOD
    // --------------------------------------------

    @Override
    public void start(Stage primaryStage)
    {
        Scene scene = new Scene(new Pane());

        primaryStage.setTitle("World Development Indicator Viewer");
        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Application entry point.
     *
     * @param args no parameter used
     */
    public static void main(String[] args)
    {
        Launch.initData();

        launch(args);
    }
}
