package com.esiee.hmi.view;

import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

// https://stackoverflow.com/questions/16235171/how-to-insert-treeview-into-accordion-using-javafx
// http://tutorials.jenkov.com/javafx/accordion.html
// https://docs.oracle.com/javafx/2/ui_controls/tree-view.htm

public class IndicatorPane extends TitledPane {

    public IndicatorPane(){
        this.setMaxWidth(300);
        this.setMinWidth(0);
//        VBox content = new VBox();
//        content.getChildren().add(new Label("Java Swing Tutorial"));
//        content.getChildren().add(new Label("JavaFx Tutorial"));
//        content.getChildren().add(new Label("Java IO Tutorial"));
        TreeItem<String> rootItem = new TreeItem<> ("Inbox");
        rootItem.setExpanded(true);
        for (int i = 1; i < 6; i++) {
            TreeItem<String> item = new TreeItem<>("Message" + i);
            rootItem.getChildren().add(item);
        }
        TreeView treeView = new TreeView<>(rootItem);


        this.setContent(treeView);
    }
}
