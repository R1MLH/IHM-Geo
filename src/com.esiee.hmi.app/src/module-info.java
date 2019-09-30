module com.esiee.hmi.app
{
    exports com.esiee.hmi.app;

    requires com.esiee.hmi.dataaccess;
    requires com.esiee.hmi.model;
    requires com.esiee.hmi.view;

    requires javafx.graphics;
    requires slf4j.api;
}
