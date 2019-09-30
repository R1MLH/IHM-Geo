module com.esiee.hmi.dataaccess
{
    exports com.esiee.hmi.dataaccess;
    exports com.esiee.hmi.dataaccess.ne to com.fasterxml.jackson.databind;
    exports com.esiee.hmi.dataaccess.wdi to com.fasterxml.jackson.databind;

    requires com.esiee.hmi.model;

    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.csv;
    requires com.fasterxml.jackson.annotation;
    requires slf4j.api;
}
