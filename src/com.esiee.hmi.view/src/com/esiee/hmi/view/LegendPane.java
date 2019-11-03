package com.esiee.hmi.view;

import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.LinkedHashMap;

class LegendPane extends Pane {

    private GlobalPane parent;

    LegendPane(GlobalPane parent) {
        this.parent = parent;
        this.setPrefWidth(425f);
        this.setPrefHeight(225f);
    }

    void refreshLegend(double max, double min, double sum, double numberOfsamples, double mean){
        StackPane stackPane = new StackPane();
        Rectangle rectangle = new Rectangle(this.getPrefWidth(), this.getPrefHeight(), Color.LIGHTGRAY);
        GridPane gridPane = new GridPane();

        LinkedHashMap<Double, Color> catLimit = this.parent.getMenu().getCatLimit();
        int i = 0;
        for(Double key : catLimit.keySet()){
            gridPane.add(new Rectangle(20,10, catLimit.get(key)), 1, i + 1);
            gridPane.add(new Label("Value <= " + key), 2, i + 1);
            i++;
        }
        gridPane.add(new Rectangle(20, 10, Color.GRAY), 1, i+1);
        gridPane.add(new Label("No Value"), 2, i+1);

        gridPane.add(new Label("Max : " + max), 4, 1);
        gridPane.add(new Label("Min : " + min), 4, 3);
        gridPane.add(new Label("Mean : " + mean), 4, 5);
        gridPane.add(new Label("Sum : " + sum), 4, 7);
//        gridPane.add(new Label("Mean : " + mean), 4, 5);

        ColumnConstraints column = new ColumnConstraints(5);
        gridPane.getColumnConstraints().addAll(column);

        RowConstraints row = new RowConstraints(2.5);
        gridPane.getRowConstraints().addAll(row);

        gridPane.setVgap(10);
        gridPane.setHgap(10);

        stackPane.getChildren().addAll(rectangle, gridPane);
        this.getChildren().addAll(stackPane);
    }

    void printMessage(String text){
        StackPane stackPane = new StackPane();
        Rectangle rectangle = new Rectangle(400f, 225f, Color.LIGHTGRAY);
        BorderPane borderPane = new BorderPane();

        borderPane.setCenter(new Label(text));

        stackPane.getChildren().addAll(rectangle, borderPane);
        this.getChildren().addAll(stackPane);
    }
}
