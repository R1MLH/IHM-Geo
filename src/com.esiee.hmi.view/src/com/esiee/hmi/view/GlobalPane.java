package com.esiee.hmi.view;

import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;

public class GlobalPane extends SplitPane {

    private boolean isMenuClosed;
    private IndicatorPane menu;

    public GlobalPane(){
        this.isMenuClosed = false;
        this.menu = new IndicatorPane();
        AnchorPane anchorPane = new AnchorPane();
        MapPane map = new MapPane();

        Button menuButton = new Button("Menu");
        menuButton.setOnMouseClicked(event -> closeOrOpenMenu());

        AnchorPane.setLeftAnchor(menuButton, 20d);

        this.heightProperty().addListener((observable, oldValue, newValue) -> AnchorPane.setTopAnchor(menuButton, newValue.doubleValue()/2));

        AnchorPane.setLeftAnchor(map, 0d);
        AnchorPane.setRightAnchor(map, 0d);
        AnchorPane.setTopAnchor(map, 0d);
        AnchorPane.setBottomAnchor(map, 0d);

        anchorPane.getChildren().addAll(map, menuButton);

        this.getItems().addAll(this.menu, anchorPane);
    }

    private void closeOrOpenMenu(){
        if (this.isMenuClosed){
            this.setDividerPositions(this.menu.getMaxWidth());
            this.isMenuClosed = false;
        }else {
            this.setDividerPositions(this.menu.getMinWidth());
            this.isMenuClosed = true;
        }
    }

}
