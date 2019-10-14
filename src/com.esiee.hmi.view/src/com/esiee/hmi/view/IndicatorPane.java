package com.esiee.hmi.view;

import com.esiee.hmi.model.DataManager;
import com.esiee.hmi.model.indicators.Indicator;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// https://stackoverflow.com/questions/16235171/how-to-insert-treeview-into-accordion-using-javafx
// http://tutorials.jenkov.com/javafx/accordion.html
// https://docs.oracle.com/javafx/2/ui_controls/tree-view.htm

public class IndicatorPane extends VBox {

    public IndicatorPane(){
        this.setMaxWidth(300);
        this.setPrefWidth(300);
        this.setMinWidth(0);
//        VBox content = new VBox();
//        content.getChildren().add(new Label("Java Swing Tutorial"));
//        content.getChildren().add(new Label("JavaFx Tutorial"));
//        content.getChildren().add(new Label("Java IO Tutorial"));
        Map<String, Map<String, List<Indicator>>> indicatorsMap = DataManager.INSTANCE.getIndicatorsMap();
        List<TitledPane> listOfPanes = new ArrayList<>();

        for(String topic : indicatorsMap.keySet()){
            VBox vBoxLayout = new VBox();
            TitledPane titledPane = new TitledPane(topic, vBoxLayout);
            titledPane.setExpanded(false);

            for(String subTopic : indicatorsMap.get(topic).keySet()){
                TreeItem<String> rootItem = new TreeItem<> (subTopic);
                TreeView treeView = new TreeView<>(rootItem);
                treeView.setMinHeight(30d);
                treeView.setPrefHeight(30d);
                rootItem.expandedProperty().addListener((observable, oldValue, newValue) -> {
                    if(newValue)
                        treeView.setPrefHeight(USE_COMPUTED_SIZE);
                    else
                        treeView.setPrefHeight(30d);
                });
                rootItem.setExpanded(false);

                for (Indicator indicator : indicatorsMap.get(topic).get(subTopic)) {
                    TreeItem<String> item = new TreeItem<>(indicator.getName());
                    rootItem.getChildren().add(item);
                }
                ((VBox) titledPane.getContent()).getChildren().add(treeView);
            }

            listOfPanes.add(titledPane);
        }

        this.getChildren().addAll(listOfPanes);
    }
}
