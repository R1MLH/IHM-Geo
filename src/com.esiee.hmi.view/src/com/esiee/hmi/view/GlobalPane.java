package com.esiee.hmi.view;

import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;

public class GlobalPane extends SplitPane {

    public GlobalPane(){
        StackPane stackPane = new StackPane();
        Button menuButton = new Button("Menu");
        menuButton.setTranslateX(20);
        stackPane.getChildren().addAll(new MapPane(), menuButton);

        this.getItems().addAll(new IndicatorPane(), stackPane);
//        this.setDividerPositions(0f);
    }

}
