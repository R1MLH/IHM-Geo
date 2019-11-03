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
    private Slider slider;
    private CheckBox dateFilter;

    IndicatorPane(GlobalPane parent, Slider slider, CheckBox dateFilter){
        this.setMaxWidth(550);
        this.setPrefWidth(550);
        this.setMinWidth(0);
        this.parent = parent;
        Map<String, Map<String, List<Indicator>>> indicatorsMap = DataManager.INSTANCE.getIndicatorsMap();
        List<TitledPane> listOfPanes = new ArrayList<>();
        this.catLimit = null;
        this.slider = slider;
        this.dateFilter = dateFilter;

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

    private void paint(Object indicatorObj) {
        MapPane mapPane = this.parent.getMapPane();
        List<Country> countryList = DataManager.INSTANCE.getCountries();
        this.resetColor(mapPane, countryList);

        TreeItem indicatorItem = (TreeItem) indicatorObj;
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

            if(indicatorDataList == null){
                this.parent.getLegendPane().printMessage("No value for this year. Please try another one");
                return;
            }

            HashMap<String, Double> stats;
            if(this.dateFilter.isSelected())
                stats = this.computeStatsForYear(indicatorDataList, (int) this.slider.getValue());
            else
                stats = this.computeStats(indicatorDataList);

            this.setCategories(stats.get("max"), stats.get("min"));

            for(IndicatorData indicatorData : indicatorDataList) {
                Optional<Country> optionalCountry = DataManager.INSTANCE.getCountryByCode(indicatorData.getCountryCode());
                Country c;
                if (optionalCountry.isPresent())
                    c = optionalCountry.get();
                else
                    continue;

                if(this.dateFilter.isSelected())
                    this.fillCountriesColorForYear(indicatorData, mapPane, c, (int) this.slider.getValue());
                else
                    this.fillCountriesColor(indicatorData, mapPane, c);
            }
            this.parent.getLegendPane().refreshLegend(stats.get("max"), stats.get("min"), stats.get("sum"), stats.get("numberOfSamples"), stats.get("mean"));
        }
    }

    private void fillCountriesColorForYear(IndicatorData indicatorData, MapPane mapPane, Country c, int year){
        double data = indicatorData.getValueForYear(year);
        if(!Double.isNaN(data))
            for(javafx.scene.shape.Polygon polygon : mapPane.getCountryMap().get(c))
                for(double key : this.catLimit.keySet())
                    if(data < key){
                        polygon.setFill(this.catLimit.get(key));
                        break;
                    }
    }

    private void fillCountriesColor(IndicatorData indicatorData, MapPane mapPane, Country c){
        for (double data : indicatorData.getValues())
            if(!Double.isNaN(data))
                for(javafx.scene.shape.Polygon polygon : mapPane.getCountryMap().get(c))
                    for(double key : this.catLimit.keySet())
                        if(data < key){
                            polygon.setFill(this.catLimit.get(key));
                            break;
                        }
    }

    private HashMap<String, Double> computeStats(List<IndicatorData> indicatorDataList){
        double max = 0.0;
        double min = Double.MAX_VALUE;
        double sum = 0.0;
        int numberOfSamples = 0;

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

        HashMap<String, Double> result = new HashMap<>();
        result.put("max", max);
        result.put("min", min);
        result.put("sum", sum);
        result.put("numberOfSamples", (double) numberOfSamples);
        result.put("mean", mean);

        return result;
    }

    private HashMap<String, Double> computeStatsForYear(List<IndicatorData> indicatorDataList, int year){
        double max = 0.0;
        double min = Double.MAX_VALUE;
        double sum = 0.0;
        int numberOfSamples = 0;

        for(IndicatorData indicatorData : indicatorDataList) {
            double data = indicatorData.getValueForYear(year);
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

        HashMap<String, Double> result = new HashMap<>();
        result.put("max", max);
        result.put("min", min);
        result.put("sum", sum);
        result.put("numberOfSamples", (double) numberOfSamples);
        result.put("mean", mean);

        return result;
    }

    private void resetColor(MapPane mapPane, List<Country> countryList){
        for(Country country : countryList)
            for(Polygon polygon : mapPane.getCountryMap().get(country))
                polygon.setFill(Color.GRAY);
    }

    private void setCategories(double max, double min){
        double plage = max - min;
        this.catLimit = new LinkedHashMap<>();
        this.catLimit.put(min + (1.0/7) * plage, Color.LIME);
        this.catLimit.put(min + (2.0/7) * plage, Color.LIMEGREEN);
        this.catLimit.put(min + (3.0/7) * plage, Color.DARKGREEN);
        this.catLimit.put(min + (4.0/7) * plage, Color.NAVAJOWHITE);
        this.catLimit.put(min + (5.0/7) * plage, Color.TOMATO);
        this.catLimit.put(min + (6.0/7) * plage, Color.ORANGERED);
        this.catLimit.put(min + (7.0/7) * plage, Color.RED);
    }

    LinkedHashMap<Double, Color> getCatLimit() {
        return catLimit;
    }
}
