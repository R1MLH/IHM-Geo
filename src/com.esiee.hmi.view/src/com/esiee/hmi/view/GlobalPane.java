package com.esiee.hmi.view;

import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class GlobalPane extends SplitPane {

    public GlobalPane(){
        AnchorPane anchorPane = new AnchorPane();
        Button menuButton = new Button("Menu");
        MapPane map = new MapPane();

        AnchorPane.setLeftAnchor(menuButton, 20d);

        this.heightProperty().addListener((observable, oldValue, newValue) -> AnchorPane.setTopAnchor(menuButton, newValue.doubleValue()/2));

        AnchorPane.setLeftAnchor(map, 0d);
        AnchorPane.setRightAnchor(map, 0d);
        AnchorPane.setTopAnchor(map, 0d);
        AnchorPane.setBottomAnchor(map, 0d);

        anchorPane.getChildren().addAll(map, menuButton);

        this.getItems().addAll(new IndicatorPane(), anchorPane);
    }

}
