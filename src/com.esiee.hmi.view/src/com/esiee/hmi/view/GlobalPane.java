package com.esiee.hmi.view;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;

public class GlobalPane extends SplitPane {

    private boolean isMenuClosed;
    private IndicatorPane menu;
    private MapPane mapPane;
    private LegendPane legendPane;

    public GlobalPane(){
        this.isMenuClosed = false;

        Slider slider = new Slider();
        slider.setMinWidth(1000);
        slider.setMax(2018);
        slider.setMin(1960);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setSnapToTicks(true);
        slider.setMajorTickUnit(5);
        slider.setMinorTickCount(4);

        CheckBox dateFilter = new CheckBox("Use Date Filter");

        this.menu = new IndicatorPane(this, slider, dateFilter);
        this.mapPane = new MapPane();
        this.legendPane = new LegendPane(this);
        AnchorPane anchorPane = new AnchorPane();

        Button menuButton = new Button("Menu");
        menuButton.setOnMouseClicked(event -> closeOrOpenMenu());
        AnchorPane.setLeftAnchor(menuButton, 20d);
        this.heightProperty().addListener((observable, oldValue, newValue) -> AnchorPane.setTopAnchor(menuButton, newValue.doubleValue()/2));

        AnchorPane.setTopAnchor(slider, 2d);
        this.mapPane.widthProperty().addListener((observable, oldValue, newValue) -> AnchorPane.setLeftAnchor(slider, newValue.doubleValue()/2-slider.getWidth()/2));

        AnchorPane.setTopAnchor(dateFilter, 20d);
        AnchorPane.setLeftAnchor(dateFilter, 20d);

        AnchorPane.setLeftAnchor(this.mapPane, 0d);
        AnchorPane.setRightAnchor(this.mapPane, 0d);
        AnchorPane.setTopAnchor(this.mapPane, 0d);
        AnchorPane.setBottomAnchor(this.mapPane, 0d);

        AnchorPane.setBottomAnchor(this.legendPane, 0d);
        this.mapPane.widthProperty().addListener((observable, oldValue, newValue) -> AnchorPane.setLeftAnchor(legendPane, newValue.doubleValue()/2 - this.legendPane.getWidth()/2));

        anchorPane.getChildren().addAll(this.mapPane, menuButton, this.legendPane, slider, dateFilter);
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

    MapPane getMapPane(){
        return this.mapPane;
    }

    IndicatorPane getMenu() {
        return menu;
    }

    LegendPane getLegendPane() {
        return legendPane;
    }
}
