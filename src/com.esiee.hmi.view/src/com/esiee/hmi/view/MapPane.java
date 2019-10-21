package com.esiee.hmi.view;

import com.esiee.hmi.model.DataManager;
import com.esiee.hmi.model.countries.Country;
import com.esiee.hmi.model.countries.GeoPoint;
import com.esiee.hmi.model.countries.Polygon;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Screen;

import java.util.HashMap;
import java.util.List;

public class MapPane extends Pane {
    double baseX ;
    double baseY ;
    Scale dynamicZoom;
    Translate dynamicPosition;
    private HashMap<javafx.scene.shape.Polygon,Country> polyMap;
    public MapPane() {
        polyMap = new HashMap<javafx.scene.shape.Polygon, Country>();

        // Transformations
        Translate centerTranslate = new Translate();
        centerTranslate.xProperty().bind(this.widthProperty().divide(2));
        centerTranslate.yProperty().bind(this.heightProperty().divide(2));
        dynamicZoom = new Scale(1,1);
        dynamicPosition = new Translate();
        Scale reverseY = new Scale(1, -1);

        Scale zoom = new Scale();
        // The point at coordinates (1; 1) is the top-right corner (width/2; height/2)
        // 1 * zoomX = width/2 => zoomX = width/2
        zoom.xProperty().bind(this.widthProperty().divide(360));
        // 1 * zoomY = height/2 => zoomY = height/2
        zoom.yProperty().bind(this.heightProperty().divide(180));
        this.getTransforms().addAll(centerTranslate, reverseY,dynamicPosition, zoom,dynamicZoom);

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
                polyMap.put(tamponPolygon,c);
                this.getChildren().add(tamponPolygon);
            }

        }


        this.getChildren().forEach(
                // change the lines stroke width depending on the zoom
                shape -> ((Shape) shape).strokeWidthProperty().bind(new SimpleDoubleProperty(1).divide(zoom.xProperty())));


        this.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent scrollEvent) {
                    //Scale dynamicZoom;
                    int signe = (int)Math.signum(scrollEvent.getDeltaY());
                    switch(signe){
                        case 1:
                            dynamicZoom.setX(1.2*dynamicZoom.getX());
                            dynamicZoom.setY(1.2*dynamicZoom.getY());
                            break;
                        case -1:
                            dynamicZoom.setX(dynamicZoom.getX()/1.2);
                            dynamicZoom.setY(dynamicZoom.getY()/1.2);
                            break;
                        default:
                            dynamicZoom.setX(1);
                            dynamicZoom.setY(1);
                    }

            }
        });


        this.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                baseX = mouseEvent.getSceneX();
                baseY = mouseEvent.getSceneY();

            }
        });

        this.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                double newX = (mouseEvent.getSceneX()-baseX);
                double newY = (baseY- mouseEvent.getSceneY());
                dynamicPosition.setX(newX+dynamicPosition.getX());

                dynamicPosition.setY(newY+dynamicPosition.getY());
                System.out.println(dynamicPosition.getX() + " "+dynamicPosition.getY());
                baseX = mouseEvent.getSceneX();
                baseY = mouseEvent.getSceneY();
            }
        });
    }



}
