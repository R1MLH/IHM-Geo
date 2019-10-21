package com.esiee.hmi.view;

import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.util.Map;

public class GlobalPane extends SplitPane {

    private boolean isMenuClosed;
    private IndicatorPane menu;
    private MapPane mapPane;

    public GlobalPane(){
        this.isMenuClosed = false;
        this.menu = new IndicatorPane(this);
        this.mapPane = new MapPane();
        AnchorPane anchorPane = new AnchorPane();
        LegendPane legendPane = new LegendPane();

        Button menuButton = new Button("Menu");
        menuButton.setOnMouseClicked(event -> closeOrOpenMenu());
        AnchorPane.setLeftAnchor(menuButton, 20d);
        this.heightProperty().addListener((observable, oldValue, newValue) -> AnchorPane.setTopAnchor(menuButton, newValue.doubleValue()/2));

        AnchorPane.setLeftAnchor(this.mapPane, 0d);
        AnchorPane.setRightAnchor(this.mapPane, 0d);
        AnchorPane.setTopAnchor(this.mapPane, 0d);
        AnchorPane.setBottomAnchor(this.mapPane, 0d);

        AnchorPane.setBottomAnchor(legendPane, 10d);
        this.mapPane.widthProperty().addListener((observable, oldValue, newValue) -> AnchorPane.setRightAnchor(legendPane, newValue.doubleValue()/2));

        anchorPane.getChildren().addAll(this.mapPane, menuButton, legendPane);
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

    public MapPane getMapPane(){
        return this.mapPane;
    }

}
