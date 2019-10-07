package com.esiee.hmi.view;

import com.esiee.hmi.model.DataManager;
import com.esiee.hmi.model.countries.Country;
import com.esiee.hmi.model.countries.GeoPoint;
import com.esiee.hmi.model.countries.Polygon;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Screen;

import java.util.List;

public class MapPane extends Pane {

    public MapPane() {

        // Transformations
        Translate centerTranslate = new Translate();
        centerTranslate.xProperty().bind(this.widthProperty().divide(2));
        centerTranslate.yProperty().bind(this.heightProperty().divide(2));

        Scale reverseY = new Scale(1, -1);

        Scale zoom = new Scale();
        // The point at coordinates (1; 1) is the top-right corner (width/2; height/2)
        // 1 * zoomX = width/2 => zoomX = width/2
        zoom.xProperty().bind(this.widthProperty().divide(360));
        // 1 * zoomY = height/2 => zoomY = height/2
        zoom.yProperty().bind(this.heightProperty().divide(180));
        this.getTransforms().addAll(centerTranslate, reverseY, zoom);

        List<Country> countries = DataManager.INSTANCE.getCountries();
        for (Country c: countries
             ) {
            List<Polygon> poly = c.getGeometry().getPolygons();
            for (Polygon polygon: poly
                 ) {
                javafx.scene.shape.Polygon tamponPolygon = new javafx.scene.shape.Polygon();
                for (GeoPoint geos: polygon.points
                     ) {
                    tamponPolygon.getPoints().addAll(geos.lon,geos.lat);

                }
                tamponPolygon.setFill(Color.GREEN);
                this.getChildren().add(tamponPolygon);
            }

        }


        this.getChildren().forEach(
                // change the lines stroke width depending on the zoom
                shape -> ((Shape) shape).strokeWidthProperty().bind(new SimpleDoubleProperty(1).divide(zoom.xProperty())));

       
    }



}
