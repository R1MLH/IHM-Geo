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
import javafx.util.Pair;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


class IndicatorPane extends Accordion {

    private LinkedHashMap<Double, Color> catLimit;
    private GlobalPane parent;

    IndicatorPane(GlobalPane parent){
        this.setMaxWidth(550);
        this.setPrefWidth(550);
        this.setMinWidth(0);
        this.parent = parent;
        Map<String, Map<String, List<Indicator>>> indicatorsMap = DataManager.INSTANCE.getIndicatorsMap();
        List<TitledPane> listOfPanes = new ArrayList<>();
        this.catLimit = null;

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
        MapPane mapPane = this.parent.getMapPane();
        List<Country> countryList = DataManager.INSTANCE.getCountries();
        for(Country country : countryList)
            for(Polygon polygon : mapPane.getCountryMap().get(country))
                polygon.setFill(Color.GRAY);

        TreeItem indicatorItem = (TreeItem) newValue;
        String indicatorName = (String) indicatorItem.getValue();
        Optional<Indicator> optionalIndicator = DataManager.INSTANCE.getIndicators().filter(indicator -> indicator.getName().equals(indicatorName)).findFirst();
//        optionalIndicator.ifPresent(indicator -> System.out.println(((Indicator) indicator).getName()));

        List<IndicatorData> indicatorDataList = null;
        if (optionalIndicator.isPresent()) {
            try {
                indicatorDataList = WDIIndicatorDataDecoder.decode(optionalIndicator.get().getCode());
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(indicatorDataList == null)
                return;

            double max = 0.0;
            double min = Double.MAX_VALUE;
            double sum = 0.0;
            double numberOfSamples = 0.0;
            for(IndicatorData indicatorData : indicatorDataList) {
                for (double data : indicatorData.getValues())
                    if(!Double.isNaN(data)){
                        if(data > max)
                            max = data;
                        else if(min > data)
                            min = data;
                        sum += data;
                        numberOfSamples++;
                    }
            }
            double mean = sum / numberOfSamples;

            double plage = max - min;
            this.catLimit = new LinkedHashMap<>();
            this.catLimit.put(min + (1.0/7) * plage, Color.LIME);
            this.catLimit.put(min + (2.0/7) * plage, Color.LIMEGREEN);
            this.catLimit.put(min + (3.0/7) * plage, Color.DARKGREEN);
            this.catLimit.put(min + (4.0/7) * plage, Color.NAVAJOWHITE);
            this.catLimit.put(min + (5.0/7) * plage, Color.TOMATO);
            this.catLimit.put(min + (6.0/7) * plage, Color.ORANGERED);
            this.catLimit.put(min + (7.0/7) * plage, Color.RED);

            for(IndicatorData indicatorData : indicatorDataList) {
                Optional<Country> optionalCountry = DataManager.INSTANCE.getCountryByCode(indicatorData.getCountryCode());
                Country c;
                if (optionalCountry.isPresent())
                    c = optionalCountry.get();
                else
                    continue;

                for (double data : indicatorData.getValues())
                    if(!Double.isNaN(data))
                        for(javafx.scene.shape.Polygon polygon : mapPane.getCountryMap().get(c))
                            for(double key : this.catLimit.keySet())
                                if(data < key){
                                    polygon.setFill(this.catLimit.get(key));
                                    break;
                                }
            }
            this.parent.getLegendPane().refreshLegend(max, min, sum, numberOfSamples, mean);
        }
    }

    LinkedHashMap<Double, Color> getCatLimit() {
        return catLimit;
    }
}
