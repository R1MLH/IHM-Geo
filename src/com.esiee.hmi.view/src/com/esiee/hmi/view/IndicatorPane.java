package com.esiee.hmi.view;

import com.esiee.hmi.dataaccess.WDIIndicatorDataDecoder;
import com.esiee.hmi.model.DataManager;
import com.esiee.hmi.model.countries.Country;
import com.esiee.hmi.model.countries.GeoPoint;
import com.esiee.hmi.model.indicators.Indicator;
import com.esiee.hmi.model.indicators.IndicatorData;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class IndicatorPane extends Accordion {

    private HashMap<String, Color> listOfColors;
    private GlobalPane parent;

    public IndicatorPane(GlobalPane parent){
        this.setMaxWidth(550);
        this.setPrefWidth(550);
        this.setMinWidth(0);
        this.parent = parent;
        Map<String, Map<String, List<Indicator>>> indicatorsMap = DataManager.INSTANCE.getIndicatorsMap();
        List<TitledPane> listOfPanes = new ArrayList<>();
        this.listOfColors = new HashMap<>();
        this.listOfColors.put("LIME", Color.LIME);
        this.listOfColors.put("LIMEGREEN", Color.LIMEGREEN);
        this.listOfColors.put("DARKGREEN", Color.DARKGREEN);
        this.listOfColors.put("NAVAJOWHITE", Color.NAVAJOWHITE);
        this.listOfColors.put("TOMATO", Color.TOMATO);
        this.listOfColors.put("ORANGE", Color.ORANGERED);
        this.listOfColors.put("RED", Color.RED);

        for(String topic : indicatorsMap.keySet()){
            VBox vBoxLayout = new VBox();
            TitledPane titledPane = new TitledPane(topic, vBoxLayout);
            titledPane.setExpanded(false);

            for(String subTopic : indicatorsMap.get(topic).keySet()){
                TreeItem<String> rootItem = new TreeItem<> (subTopic);
                TreeView treeView = new TreeView<>(rootItem);
                treeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
                treeView.setMinHeight(30d);
                treeView.setPrefHeight(30d);

                rootItem.expandedProperty().addListener((observable, oldValue, newValue) -> {
                    if(newValue)
                        treeView.setPrefHeight(USE_COMPUTED_SIZE);
                    else
                        treeView.setPrefHeight(30d);
                });
                rootItem.setExpanded(false);

                treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> paint(newValue));

                for (Indicator indicator : indicatorsMap.get(topic).get(subTopic)) {
                    TreeItem<String> item = new TreeItem<>(indicator.getName());
                    rootItem.getChildren().add(item);
                }
                ((VBox) titledPane.getContent()).getChildren().add(treeView);
            }

            listOfPanes.add(titledPane);
        }

        this.getPanes().addAll(listOfPanes);
    }

    private void paint(Object newValue) {
        TreeItem indicatorItem = (TreeItem) newValue;
        String indicatorName = (String) indicatorItem.getValue();
        Optional<Indicator> optionalIndicator = DataManager.INSTANCE.getIndicators().filter(indicator -> indicator.getName().equals(indicatorName)).findFirst();
//        optionalIndicator.ifPresent(indicator -> System.out.println(((Indicator) indicator).getName()));

        List<IndicatorData> indicatorDataList = null;
        if (optionalIndicator.isPresent()) {
            try {
                indicatorDataList = WDIIndicatorDataDecoder.decode(optionalIndicator.get().getCode());
//                System.out.println(indicatorDataList);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(indicatorDataList == null)
                return;

            for(IndicatorData indicatorData : indicatorDataList) {
                double max = 0.0;
                double min = Double.MAX_VALUE;
                System.out.println(indicatorData.getCountryCode());
                Optional<Country> optionalCountry = DataManager.INSTANCE.getCountryByCode(indicatorData.getCountryCode());
                Country c = null;
                if (optionalCountry.isPresent())
                    c = optionalCountry.get();
                else
                    return;

                for (double data : indicatorData.getValues()){
                    if(!Double.isNaN(data)){
//                    System.out.println(data);
                        if(data > max)
                            max = data;
                        else if(min > data)
                            min = data;
                    }
                }

                double plage = max - min;
//                double premierecatLimit = 0.0;
                double deuxiemecatLimit = (1.0/7) * plage;
                double troisiemecatLimit = (2.0/7) * plage;
                double quatriemecatLimit = (3.0/7) * plage;
                double cinquiemecatLimit = (4.0/7) * plage;
                double sixiemecatLimit = (5.0/7) * plage;
                double septiemecatLimit = (6.0/7) * plage;

                for (double data : indicatorData.getValues())
                    if(!Double.isNaN(data)){
                        MapPane mapPane = this.parent.getMapPane();
//                        System.out.println(mapPane);
                        for(Node node : mapPane.getChildren()){
                                javafx.scene.shape.Polygon polygon = (javafx.scene.shape.Polygon) node;
                                if(data < deuxiemecatLimit)
                                    polygon.setFill(listOfColors.get("LIME"));
                                else if(data < troisiemecatLimit)
                                    polygon.setFill(listOfColors.get("LIMEGREEN"));
                                else if(data < quatriemecatLimit)
                                    polygon.setFill(listOfColors.get("DARKGREEN"));
                                else if(data < cinquiemecatLimit)
                                    polygon.setFill(listOfColors.get("NAVAJOWHITE"));
                                else if(data < sixiemecatLimit)
                                    polygon.setFill(listOfColors.get("TOMATO"));
                                else if(data < septiemecatLimit)
                                    polygon.setFill(listOfColors.get("ORANGE"));
                                else
                                    polygon.setFill(listOfColors.get("RED"));
                        }
                    }
            }
        }
    }
}
