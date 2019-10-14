package com.esiee.hmi.view;

import com.esiee.hmi.dataaccess.WDIIndicatorDataDecoder;
import com.esiee.hmi.model.DataManager;
import com.esiee.hmi.model.countries.Country;
import com.esiee.hmi.model.countries.GeoPoint;
import com.esiee.hmi.model.countries.Polygon;
import com.esiee.hmi.model.indicators.Indicator;
import com.esiee.hmi.model.indicators.IndicatorData;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class IndicatorPane extends Accordion {

    public IndicatorPane(){
        this.setMaxWidth(550);
        this.setPrefWidth(550);
        this.setMinWidth(0);
        Map<String, Map<String, List<Indicator>>> indicatorsMap = DataManager.INSTANCE.getIndicatorsMap();
        List<TitledPane> listOfPanes = new ArrayList<>();

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
                System.out.println(indicatorDataList);
            } catch (IOException e) {
                e.printStackTrace();
            }

            for(IndicatorData indicatorData : indicatorDataList){
                for(double data : indicatorData.getValues())
                    System.out.println(data);
            }

//            for (Country c : DataManager.INSTANCE.getCountries()) {
//                List<Polygon> poly = c.getGeometry().getPolygons();
//                for (Polygon polygon : poly) {
//                    javafx.scene.shape.Polygon tamponPolygon = new javafx.scene.shape.Polygon();
//                    for (GeoPoint geos : polygon.points) {
//                        tamponPolygon.getPoints().addAll(geos.lon, geos.lat);
//                    }
//                    tamponPolygon.setFill(Color.RED);
//                }
//            }
        }
    }
}
