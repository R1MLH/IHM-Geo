package com.esiee.hmi.view;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

public class MapPane extends Pane {

    public MapPane() {

        Line xAxis = new Line(0, 0, 180, 0);
        xAxis.setStyle("-fx-stroke: RED");
        Line yAxis = new Line(0, 0, 0, 90);
        yAxis.setStyle("-fx-stroke: GREEN");

        this.getChildren().addAll(xAxis,yAxis);
    }

}
