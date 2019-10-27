package com.esiee.hmi.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;
import java.util.LinkedHashMap;

class LegendPane extends Pane {

    private GlobalPane parent;

    LegendPane(GlobalPane parent) {
        this.parent = parent;
    }

    void refreshLegend(double max, double min, double sum, double numberOfsamples, double mean){
        StackPane stackPane = new StackPane();
        Rectangle rectangle = new Rectangle(400f, 200f, Color.LIGHTGRAY);
        GridPane gridPane = new GridPane();

        LinkedHashMap<Double, Color> catLimit = this.parent.getMenu().getCatLimit();
        int i = 0;
        for(Double key : catLimit.keySet()){
            gridPane.add(new Rectangle(20,10, catLimit.get(key)), 1, i + 1);
            gridPane.add(new Label("Value <= " + key), 2, i + 1);
            i++;
        }

        gridPane.add(new Label("Max : " + max), 4, 1);
        gridPane.add(new Label("Min : " + min), 4, 3);
        gridPane.add(new Label("Mean : " + mean), 4, 5);

        ColumnConstraints column = new ColumnConstraints(5);
        gridPane.getColumnConstraints().addAll(column);

        RowConstraints row = new RowConstraints(2.5);
        gridPane.getRowConstraints().addAll(row);

        gridPane.setVgap(10);
        gridPane.setHgap(10);

        stackPane.getChildren().addAll(rectangle, gridPane);
        this.getChildren().addAll(stackPane);
    }
}
