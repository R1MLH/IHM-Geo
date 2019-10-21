package com.esiee.hmi.view;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class LegendPane extends GridPane {

    public LegendPane() {
        Label label = new Label("Test");
        GridPane.setRowIndex(label, 1);
        GridPane.setColumnIndex(label, 2);

        this.getChildren().addAll(label);
    }
}
