package solrTest;

import com.sun.javafx.charts.Legend;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.*;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.effect.Glow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.apache.solr.client.solrj.SolrServerException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;

import static java.time.temporal.ChronoUnit.*;

public class Main extends Application {

    public static final String YYYY_MM_DD_T_HH_MM = "yyyy-MM-dd'T'HH:mm";
    XYChart.Series firstSelectionPriceSeries = new XYChart.Series();
    XYChart.Series secondSelectionPriceSeries = new XYChart.Series();
    XYChart.Series seriesDiffBar = new XYChart.Series();
    Double firstChartInitYPos;
    Double secondChartInitYPos;
    String dragArea;
    VBox firstSelectionContainer = new VBox();
    VBox secondSelectionContainer = new VBox();
    SimpleDoubleProperty upperBoundForYMain = new SimpleDoubleProperty();
    SimpleDoubleProperty lowerBoundForYMain = new SimpleDoubleProperty();
    SimpleDoubleProperty secondSelectionUpperYBound = new SimpleDoubleProperty();
    SimpleDoubleProperty secondSelectionLowerYBound = new SimpleDoubleProperty();
    SimpleDoubleProperty selectedMarkSize = new SimpleDoubleProperty();
    Text placeHolder;
    TextField firstListSearch = new TextField();
    TextField secondListSearch = new TextField();

    Button resetButton = new Button("Reset zoom");
    Button resetTableButton = new Button("Clear Table");

    HBox containingBox;
    Pane detailPane;
    VBox chartBox;
    Pane miniMapPane;
    double positionXOverviewChart;
    NumberAxis firstSelectionYAxis;
    DateAxis310 firstSelectionXAxis;
    NumberAxis secondSelectionYAxis;
    DateAxis310 secondSelectionXAxis;
    ListView<String> firstListOfInstruments = new ListView<>();
    ListView<String> firstListOfSeries = new ListView<>();

    Button openFirstInstrumentListButton = new Button("Select Instrument: ");
    Button openFirstSeriesListButton = new Button("Select Series: ");

    Button openSecondInstrumentListButton = new Button("Select Instrument: ");
    Button openSecondSeriesListButton = new Button("Select Series: ");

    ListView<String> secondListOfInstruments = new ListView<>();
    ListView<String> secondListOfSeries = new ListView<>();

    ObservableList<String> firstObservableListInstruments;
    ObservableList<String> firstObservableListSeries;

    ObservableList<String> secondObservableListInstruments;
    ObservableList<String> secondObservableListSeries;
    ObservableList<Item> observableListItems;

    final StringConverter<LocalDateTime> STRING_CONVERTER = new StringConverter<LocalDateTime>() {
        @Override public String toString(LocalDateTime localDateTime) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM yyyy");
            return dtf.format(localDateTime);
        }
        @Override public LocalDateTime fromString(String s) {
            return LocalDateTime.parse(s);
        }
    };

    String lastEarliestValue;
    String lastLatestValue;

    Rectangle zoomBounds;
    Line trackX = new Line(0, 550, 0, 0);
    Label displayAtPosition = new Label();
    Label displayAtTarget = new Label();
    Label labelInstruments;
    VBox listBox;
    SimpleDoubleProperty leftHookPosition = new SimpleDoubleProperty();
    SimpleDoubleProperty rightHookPosition = new SimpleDoubleProperty();
    SimpleDoubleProperty trackXPosition = new SimpleDoubleProperty();
    SimpleDoubleProperty trackXTargetPosition = new SimpleDoubleProperty();
    SimpleDoubleProperty trackYPosition = new SimpleDoubleProperty();

    SimpleDoubleProperty rectinitX = new SimpleDoubleProperty();
    SimpleDoubleProperty rectinitY = new SimpleDoubleProperty();
    SimpleDoubleProperty rectX = new SimpleDoubleProperty();
    SimpleDoubleProperty rectY = new SimpleDoubleProperty();

    LineChart<LocalDateTime, Number> lineChartOverview;
    LineChart<LocalDateTime, Number> firstSelectionChart;
    LineChart<LocalDateTime, Number> secondSelectionChart;

    List<Item> firstSelectionResults;
    List<Item> secondSelectionResults;

    List<String> firstInstrumentResults;
    List<String> firstSeriesResults;

    List<String> secondInstrumentResults;
    List<String> secondSeriesResults;

    List<Item> itemResults = new ArrayList<>();
    ArrayList<XYChart.Data<LocalDateTime, Float>> firstSelectionDataRemovedFromFront;
    ArrayList<XYChart.Data<LocalDateTime, Float>> firstSelectionDataRemovedFromBack;
    ArrayList<XYChart.Data<LocalDateTime, Float>> secondSelectionDataRemovedFromFront = new ArrayList<>();
    ArrayList<XYChart.Data<LocalDateTime, Float>> secondSelectionDataRemovedFromBack = new ArrayList<>();
    ArrayList<XYChart.Data<LocalDateTime, Float>> deltaDataRemovedFromFront = new ArrayList<>();
    ArrayList<XYChart.Data<LocalDateTime, Float>> deltaDataRemovedFromBack = new ArrayList<>();

    double initialLeftHookPosition = 0.0;
    double initialRightHookPosition = 0.0;
    Line lineIndicator = new Line(0, 550, 0, 0);
    Rectangle hookRight = new Rectangle(15,40);
    Rectangle hookLeft = new Rectangle(15,40);
    Rectangle leftRect = new Rectangle(30,150);
    Rectangle rightRect = new Rectangle(30,150);
    Separator separator;
    Pane propertiesPane = new Pane();

    ArrayList<Double> valuesHighRaw;
    SimpleDoubleProperty width;
    ArrayList<LocalDateTime> aboveAverages;
    ObservableList<LocalDateTime> observableAboveAverages;
    XYChart.Series seriesTotal;
    XYChart.Series secondSelectionSeriesTotal;
    HashMap orderOfSeriesPointsToId = new HashMap();
    HashMap orderOfGraphPointsToId = new HashMap();
    HashMap firstSeriesDateToPrice = new HashMap();
    HashMap secondSeriesDateToPrice = new HashMap();
    HashMap overviewPointsDateToId = new HashMap();
    TableView tableOfProperties = new TableView();
    AreaChart<LocalDateTime, Number> diffBarChart;

    Pane lineChartPane;
    NumberAxis yAxis;
    DateAxis310 xAxis;
    NumberAxis yBarAxis;
    DateAxis310 xBarAxis;

    Text miniMapDetail;
    Text detail;
    Pane containingPane;

    public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void init() throws Exception {
	}

	@Override
	public void stop() throws Exception {
	}

	@Override
	public void start(Stage stage) throws Exception {
		final Group root = new Group();
		Scene scene = new Scene(root, 1750, 850, Color.WHITESMOKE);
        initComponents();

        firstInstrumentResults = SolrService.getInstruments("*");
        firstSeriesResults = SolrService.getSeries(firstInstrumentResults.get(0));
        secondInstrumentResults = SolrService.getInstruments("*");
        secondSeriesResults = SolrService.getSeries(secondInstrumentResults.get(0));

        firstObservableListInstruments = FXCollections.observableArrayList(firstInstrumentResults);
        firstObservableListSeries = FXCollections.observableArrayList(firstSeriesResults);
        secondObservableListInstruments = FXCollections.observableArrayList(secondInstrumentResults);
        secondObservableListSeries = FXCollections.observableArrayList(secondSeriesResults);

        firstSelectionResults = SolrService.getResults("*", "*", firstObservableListInstruments.get(0), firstObservableListSeries.get(0));
        secondSelectionResults = SolrService.getResults("*", "*", secondObservableListInstruments.get(0), secondObservableListSeries.get(1));

        firstListOfInstruments = ListViewActions.makeListView(firstObservableListInstruments);
        secondListOfInstruments = ListViewActions.makeListView(secondObservableListInstruments);

        resetTableButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                itemResults.clear();
                observableListItems = FXCollections.observableArrayList(itemResults);
                tableOfProperties.setItems(observableListItems);
                selectedMarkSize.setValue(1);
                propertiesPane.setVisible(false);
            }
        });

        firstListOfSeries.getItems().setAll(firstObservableListSeries);
        secondListOfSeries.getItems().setAll(secondObservableListSeries);

        lineChartOverview = ChartActions.makeZoomLineChart(overviewPointsDateToId, "chart-series-line-overview", "", miniMapDetail, firstListOfSeries, xAxis, yAxis);
        firstSelectionChart = ChartActions.makeZoomLineChart(orderOfGraphPointsToId, "chart-series-lineFirst", "", detail, firstListOfSeries, firstSelectionXAxis, firstSelectionYAxis);
        secondSelectionChart = ChartActions.makeZoomLineChart(orderOfGraphPointsToId, "chart-series-lineSecond", "chart-symbol2", detail, secondListOfSeries, secondSelectionXAxis, secondSelectionYAxis);

        secondSelectionChart.setFocusTraversable(true);
        try {
            secondSelectionResults = SolrService.getResultsOnInstrumentAndSeries(secondObservableListInstruments.get(0), secondObservableListSeries.get(0));
            if (secondSelectionResults.size() == 1){
                System.out.println("ONE POINT OF DATA IN THIS SERIES ONLY");
                return;
            }else{
                secondSelectionPriceSeries.getData().clear();
                secondSelectionSeriesTotal.getData().clear();
                for (int c = 0; c <= secondSelectionResults.size() - 1; c++) {
                    Item item = secondSelectionResults.get(c);
                    secondSeriesDateToPrice.put(item.getPrice_date(), item.getPrice());
                    try{
                        secondSelectionPriceSeries.getData().add(ChartActions.createChartData(item));
                        secondSelectionSeriesTotal.getData().add(ChartActions.createChartData(item));
                        orderOfSeriesPointsToId.put(c, item.getId());
                    }catch (Exception e){
                        secondSelectionPriceSeries.getData().add(ChartActions.createChartData(item));
                        orderOfSeriesPointsToId.put(c, item.getId());

                        secondSelectionSeriesTotal.getData().add(ChartActions.createChartData(item));
                    }
                }
            }

        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            firstSelectionResults = SolrService.getResultsOnInstrumentAndSeries(firstObservableListInstruments.get(0), firstObservableListSeries.get(1));
            if (firstSelectionResults.size() == 1){
                System.out.println("ONLY ONE POINT OF DATA IN THIS SERIES");
                return;
            } else{

                firstSelectionPriceSeries.getData().clear();
                seriesTotal.getData().clear();
                populateAllSeriesAndMatchBounds(firstSelectionResults, secondSeriesDateToPrice, firstSelectionPriceSeries, seriesTotal, firstSeriesDateToPrice, orderOfSeriesPointsToId, seriesDiffBar);
                matchBoundsBetweenCharts();
            }
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        observableListItems = FXCollections.observableArrayList(itemResults);
        propertiesPane.setVisible(false);
        propertiesPane.getStyleClass().add("b2");
        detailPane.getStyleClass().add("b2");
        secondListOfInstruments.getStyleClass().add("list-view2");
        secondListOfSeries.getStyleClass().add("list-view2");
        firstListOfInstruments.getStyleClass().add("list-view3");
        firstListOfSeries.getStyleClass().add("list-view3");
        setupCharts();
        addListeners();
        ChartActions.turnOffPickOnBoundsFor(firstSelectionChart);

        ((NumberAxis) firstSelectionChart.getYAxis()).setForceZeroInRange(false);
        lineChartPane.setFocusTraversable(true);
        containingBox.setMinWidth(scene.getWidth());
        firstListOfInstruments.getSelectionModel().selectFirst();
        secondListOfInstruments.getSelectionModel().select(2);

        lineChartPane.getChildren().addAll(secondSelectionChart, firstSelectionChart, lineIndicator);
        miniMapPane.getChildren().addAll(lineChartOverview, leftRect, rightRect, hookRight, hookLeft, miniMapDetail);
        chartBox.getChildren().addAll(lineChartPane, separator, diffBarChart, miniMapPane);
        containingPane.getChildren().addAll(chartBox, zoomBounds, trackX, displayAtPosition, displayAtTarget, detail, propertiesPane);
        containingBox.getChildren().addAll(containingPane, detailPane);
        root.getChildren().addAll(containingBox);

        stage.setTitle("Calculating Important Points");
		stage.setScene(scene);
        scene.getStylesheets().add("demo.css");

		stage.show();
	}

    public static String toUtcDate(String dateStr) {
        HashMap<String, String> monthToMonth = new HashMap<String, String>();
        monthToMonth.put("Jan", "01");
        monthToMonth.put("Feb", "02");
        monthToMonth.put("Mar", "03");
        monthToMonth.put("Apr", "04");
        monthToMonth.put("May", "05");
        monthToMonth.put("Jun", "06");
        monthToMonth.put("Jul", "07");
        monthToMonth.put("Aug", "08");
        monthToMonth.put("Sep", "09");
        monthToMonth.put("Oct", "10");
        monthToMonth.put("Nov", "11");
        monthToMonth.put("Dec", "12");
        String month = monthToMonth.get(dateStr.substring(4, 7));
        String year = dateStr.substring(24);
        String day = dateStr.substring(8, 10);
        dateStr = year + "-" + month + "-" + day;
        SimpleDateFormat out = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String[] dateFormats = {"yyyy-MM-dd", "MMM dd, yyyy hh:mm:ss Z"};
        for (String dateFormat : dateFormats) {
            try {
                return out.format(new SimpleDateFormat(dateFormat).parse(dateStr));
            } catch (ParseException ignore) { }
        }
        throw new IllegalArgumentException("Invalid date: " + dateStr);
    }

    EventHandler<MouseEvent> mouseHandler = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent mouseEvent){
            if (mouseEvent.getSceneX() < 55 || mouseEvent.getSceneX() > 1380) {
                trackX.           setVisible(false);
                displayAtPosition.setVisible(false);
                displayAtTarget.  setVisible(false);

                return;
            }
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
                zoomBounds.setX(mouseEvent.getSceneX());
                zoomBounds.setY(-20);
                rectinitX.set(mouseEvent.getSceneX());
                rectinitY.set(0);
            } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                trackXTargetPosition.set(mouseEvent.getSceneX());
                rectX.               set(mouseEvent.getSceneX());
                rectY.               set(firstSelectionChart.getHeight() + 20);
                displayAtTarget.setVisible(true);
                lineIndicator.  setVisible(false);

            } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
                displayAtTarget.  setVisible(false);
                displayAtPosition.setVisible(false);
                if (rectX.getValue() - rectinitX.getValue() < 20){
                    rectX.set(0);
                    rectY.set(0);

                    return;
                }
                String valueForDisplayStart = "";
                String valueForDisplayEnd   = "";
                LocalDateTime localDateTimeStart  = null;
                LocalDateTime localDateTimeFinish = null;
                try{
                    localDateTimeStart  = firstSelectionChart.getXAxis().getValueForDisplay(rectinitX.getValue() - 55.0);
                    localDateTimeFinish = firstSelectionChart.getXAxis().getValueForDisplay(rectX.getValue() - 55.0);

                    valueForDisplayStart = String.valueOf(localDateTimeStart);
                    valueForDisplayEnd   = String.valueOf(localDateTimeFinish);
                    lastLatestValue = valueForDisplayEnd;
                    lastEarliestValue = valueForDisplayStart;
                    double left  = lineChartOverview.getXAxis().getDisplayPosition(LocalDateTime.parse(valueForDisplayStart));
                    double right = lineChartOverview.getXAxis().getDisplayPosition(LocalDateTime.parse(valueForDisplayEnd));

                    String startDate = valueForDisplayStart.substring(0, 19).concat("Z");
                    String endDate   = valueForDisplayEnd.substring(0, 19).concat("Z");

                    leftHookPosition. set(left);
                    rightHookPosition.set(right + 60);

                    firstSelectionResults  = SolrService.getResults(startDate, endDate, firstListOfInstruments.getSelectionModel().getSelectedItem(), firstListOfSeries.getSelectionModel().getSelectedItem());
                    secondSelectionResults = SolrService.getResults(startDate, endDate, secondListOfInstruments.getSelectionModel().getSelectedItem(), secondListOfSeries.getSelectionModel().getSelectedItem());
                } catch (SolrServerException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e){
                }

                ChartActions.setChartsUpperBound(localDateTimeFinish, firstSelectionChart, secondSelectionChart, firstSelectionYAxis, secondSelectionYAxis, diffBarChart);
                ChartActions.setChartsLowerBound(localDateTimeStart, firstSelectionChart, secondSelectionChart, firstSelectionYAxis, secondSelectionYAxis, diffBarChart);
//
                cutLeftAndRight(secondSelectionPriceSeries, secondSelectionDataRemovedFromBack, secondSelectionDataRemovedFromFront);
                cutLeftAndRight(firstSelectionPriceSeries, firstSelectionDataRemovedFromBack, firstSelectionDataRemovedFromFront);
                cutLeftAndRight(seriesDiffBar, deltaDataRemovedFromBack, deltaDataRemovedFromFront);

                rectX.set(0);
                rectY.set(0);
            }else if (mouseEvent.getEventType() == MouseEvent.MOUSE_MOVED){
                trackX.setVisible(true);
                trackXPosition.set(mouseEvent.getSceneX());
                trackYPosition.set(mouseEvent.getSceneY());
            }else if (mouseEvent.getEventType() == MouseEvent.MOUSE_EXITED){
                trackX.setVisible(false);
                displayAtPosition.setVisible(false);
                displayAtTarget.setVisible(false);
            }else if (mouseEvent.getEventType() == MouseEvent.MOUSE_ENTERED){
                trackX.setVisible(true);
                displayAtPosition.setVisible(true);
            }
        }
    };

    private void cutLeftAndRight(XYChart.Series series, ArrayList dataRemovedFromBack, ArrayList dataRemovedFromFront) {
        XYChart.Data<LocalDateTime, Float> deltaLocalDateTimeFloatData = (XYChart.Data<LocalDateTime, Float>) series.getData().get(series.getData().size() - 2);
        XYChart.Data<LocalDateTime, Float> deltaFirstDate = deltaLocalDateTimeFloatData;
        XYChart.Data<LocalDateTime, Float> deltaLastData = (XYChart.Data<LocalDateTime, Float>) series.getData().get(1);
        SeriesActions.cutLeft(-1.0, deltaLastData, series, dataRemovedFromBack, lastEarliestValue);
        SeriesActions.cutRight(1.0, deltaFirstDate, series, dataRemovedFromFront, lastLatestValue);
    }

    private void setupCharts(){
        ((DateAxis310) firstSelectionChart.getXAxis()).setTickLabelsVisible(true);
        ((DateAxis310) firstSelectionChart.getXAxis()).setTickMarkVisible(false);
        firstSelectionChart.getData().add(firstSelectionPriceSeries);
        firstSelectionChart.setLegendVisible(false);
        firstSelectionChart.setHorizontalGridLinesVisible(true);
        firstSelectionChart.setMaxWidth(1390);
        firstSelectionChart.setMinWidth(1390);
        firstSelectionChart.setMaxHeight(550);
        firstSelectionChart.setMinHeight(550);
        firstSelectionChart.setVerticalZeroLineVisible(false);
        firstSelectionChart.setVerticalGridLinesVisible(false);
        firstSelectionChart.setHorizontalZeroLineVisible(false);
        firstSelectionChart.setVerticalZeroLineVisible(false);
        firstSelectionChart.setHorizontalZeroLineVisible(false);
        firstSelectionChart.setFocusTraversable(true);
        firstSelectionChart.setLegendVisible(true);
        firstSelectionChart.setAnimated(false);
        firstSelectionChart.setTitle("Instrument Price Series");
        firstSelectionChart.setVerticalZeroLineVisible(false);
        firstSelectionChart.setCreateSymbols(true);

        for (Node legend : firstSelectionChart.getChildrenUnmodifiable()){
            if (legend instanceof Legend){
                legend.setTranslateX(-75);
            }
        }

        secondSelectionChart.getData().add(secondSelectionPriceSeries);
        secondSelectionChart.setLegendVisible(false);
        secondSelectionChart.setHorizontalGridLinesVisible(true);
        secondSelectionChart.setMaxWidth(1390);
        secondSelectionChart.setMinWidth(1390);
        secondSelectionChart.setMaxHeight(512);
        secondSelectionChart.setMinHeight(512);
        secondSelectionChart.setVerticalZeroLineVisible(false);
        secondSelectionChart.setHorizontalZeroLineVisible(true);
        secondSelectionChart.setHorizontalGridLinesVisible(false);
        secondSelectionChart.setVerticalGridLinesVisible(true);
        secondSelectionChart.setLegendVisible(true);
        for (Node legend : secondSelectionChart.getChildrenUnmodifiable()){
            if (legend instanceof Legend){
                legend.setTranslateX(100);
                legend.setTranslateY(13);
            }
        }

        diffBarChart.getData().addAll(seriesDiffBar);
        diffBarChart.setMaxWidth(1390);
        diffBarChart.setHorizontalGridLinesVisible(false);
        diffBarChart.getXAxis().setTickMarkVisible(false);
        diffBarChart.getXAxis().setTickLabelsVisible(false);
        ((NumberAxis) diffBarChart.getYAxis()).setAutoRanging(true);
        ((NumberAxis) diffBarChart.getYAxis()).setForceZeroInRange(false);
        diffBarChart.setTranslateX(6);
        diffBarChart.setMaxHeight(150);
        diffBarChart.setCreateSymbols(false);
        diffBarChart.setAnimated(false);

        secondSelectionChart.setAnimated(false);
        secondSelectionChart.setVerticalZeroLineVisible(false);
        secondSelectionChart.setCreateSymbols(true);
        secondSelectionChart.getXAxis().setTickLabelsVisible(false);
        secondSelectionChart.getXAxis().setTickMarkVisible(false);
        secondSelectionChart.getYAxis().setSide(Side.RIGHT);
        secondSelectionChart.getYAxis().setAutoRanging(true);
        secondSelectionChart.setTranslateX(29);
        secondSelectionChart.setTranslateY(25);
        lineChartOverview.legendVisibleProperty().setValue(false);
        lineChartOverview.getData().add(seriesTotal);
        lineChartOverview.getData().add(secondSelectionSeriesTotal);
        lineChartOverview.setVerticalGridLinesVisible(false);
        lineChartOverview.setVerticalGridLinesVisible(false);
        lineChartOverview.setCreateSymbols(false);
        ((DateAxis310) lineChartOverview.getXAxis()).setTickLabelsVisible(false);
        ((DateAxis310) lineChartOverview.getXAxis()).setTickMarkVisible(false);
        lineChartOverview.setMaxHeight(150);
        lineChartOverview.setMinWidth(1390);
        lineChartOverview.setMaxWidth(1390);
        lineChartOverview.setAnimated(false);
        Rectangle rectangle = new Rectangle(0,0, 1375, 550);
        firstSelectionChart.setClip(rectangle);
    }

    private void addListeners(){
        firstListSearch .opacityProperty().bind(firstListOfInstruments.opacityProperty());
        secondListSearch.opacityProperty().bind(secondListOfInstruments.opacityProperty());
        firstListSearch .managedProperty().bind(firstListOfInstruments. managedProperty());
        secondListSearch.managedProperty().bind(secondListOfInstruments.managedProperty());

        hookLeft.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                initialLeftHookPosition = mouseEvent.getSceneX();
            }
        });
        hookRight.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                initialRightHookPosition = mouseEvent.getSceneX();
            }
        });

        trackXPosition.addListener(new Listeners(this, firstSelectionChart, displayAtPosition));

        trackXTargetPosition.addListener(new XTargetChangeListener());

        hookLeft.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (leftRect.getWidth() >= (hookRight.getLayoutX() - 50) || mouseEvent.getSceneX() - 50 >= (hookRight.getLayoutX() - 50)) {
                    leftHookPosition.set(hookRight.getLayoutX() - 60);
                    return;
                }

                if (mouseEvent.getSceneX() < 55) {
                    leftHookPosition.set(0);
                    return;
                }
                double deltaLeft = initialLeftHookPosition - mouseEvent.getSceneX();
                leftHookPosition.set(hookLeft.getLayoutX() - deltaLeft);
                XYChart.Data<LocalDateTime, Float> lastData = (XYChart.Data<LocalDateTime, Float>) firstSelectionPriceSeries.getData().get(1);
                LocalDateTime valueForDisplay = lineChartOverview.getXAxis().getValueForDisplay(mouseEvent.getSceneX() - 40);
                lastEarliestValue = String.valueOf(valueForDisplay);
                try {
                    if (firstSelectionDataRemovedFromBack.get(0) == secondSelectionDataRemovedFromBack.get(0)) {
                        firstSelectionChart. getXAxis().setAutoRanging(false);
                        secondSelectionChart.getXAxis().setAutoRanging(false);
                    } else {
                        firstSelectionChart. getXAxis().setAutoRanging(true);
                        secondSelectionChart.getXAxis().setAutoRanging(true);
                    }
                } catch (Exception e) {
                }
                try {
                    XYChart.Data<LocalDateTime, Float> secondSelectionLastData = (XYChart.Data<LocalDateTime, Float>) secondSelectionPriceSeries.getData().get(1);
                    SeriesActions.cutLeft(deltaLeft, secondSelectionLastData, secondSelectionPriceSeries, secondSelectionDataRemovedFromBack, lastEarliestValue);
                } catch (Exception e) {
                }
                try {
                    SeriesActions.cutLeft(deltaLeft, lastData, firstSelectionPriceSeries, firstSelectionDataRemovedFromBack, lastEarliestValue);
                } catch (Exception e) {
                }
                try{
                    XYChart.Data<LocalDateTime, Float> deltaLastData = (XYChart.Data<LocalDateTime, Float>) seriesDiffBar.getData().get(1);
                    SeriesActions.cutLeft(deltaLeft, deltaLastData, seriesDiffBar, deltaDataRemovedFromBack, lastEarliestValue);
                }catch (Exception e){
                }
                initialLeftHookPosition = mouseEvent.getSceneX();
                ChartActions.setChartsLowerBound(valueForDisplay, firstSelectionChart, secondSelectionChart, firstSelectionYAxis, secondSelectionYAxis, diffBarChart);
            }
        });

        hookLeft.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (leftRect.getWidth() >= (hookRight.getLayoutX() - 50)) {
                    leftHookPosition.set(hookRight.getLayoutX() - 60);
                    return;
                }
                if (mouseEvent.getSceneX() < 55) {
                    leftHookPosition.set(0);
                    return;
                }
            }
        });

        hookRight.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (isHookInBounds(mouseEvent)) return;
                double deltaRight = initialRightHookPosition - mouseEvent.getSceneX();
                rightHookPosition.set(hookRight.getLayoutX() - deltaRight);

                LocalDateTime valueForDisplay = lineChartOverview.getXAxis().getValueForDisplay(mouseEvent.getSceneX() - 45);
                lastLatestValue = String.valueOf(valueForDisplay);
                firstSelectionChart. getXAxis().setAutoRanging(true);
                secondSelectionChart.getXAxis().setAutoRanging(true);

                XYChart.Data<LocalDateTime, Float> firstDate = (XYChart.Data<LocalDateTime, Float>) firstSelectionPriceSeries.getData().get(firstSelectionPriceSeries.getData().size() - 2);
                SeriesActions.cutRight(deltaRight, firstDate, firstSelectionPriceSeries, firstSelectionDataRemovedFromFront, lastLatestValue);

                XYChart.Data<LocalDateTime, Float> secondSelectionFirstDate = (XYChart.Data<LocalDateTime, Float>) secondSelectionPriceSeries.getData().get(secondSelectionPriceSeries.getData().size() - 2);
                SeriesActions.cutRight(deltaRight, secondSelectionFirstDate, secondSelectionPriceSeries, secondSelectionDataRemovedFromFront, lastLatestValue);

                XYChart.Data<LocalDateTime, Float> deltaLocalDateTimeFloatData = getSecondToLatestPoint(seriesDiffBar);
                SeriesActions.cutRight(deltaRight, deltaLocalDateTimeFloatData, seriesDiffBar, deltaDataRemovedFromFront, lastLatestValue);

                ChartActions.setChartsUpperBound(valueForDisplay, firstSelectionChart, secondSelectionChart, firstSelectionYAxis, secondSelectionYAxis, diffBarChart);
                initialRightHookPosition = mouseEvent.getSceneX();

            }
        });

        hookRight.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (isHookInBounds(mouseEvent)) return;
            }
        });
        openSecondInstrumentListButton.getStyleClass().add("button2");
        openSecondSeriesListButton.getStyleClass().add("button2");

        ListViewActions.makeListViewDisappear(secondListOfInstruments);
        ListViewActions.makeListViewDisappear(firstListOfInstruments);
        ListViewActions.makeListViewDisappear(secondListOfSeries);
        ListViewActions.makeListViewDisappear(secondListOfSeries);

        firstSelectionContainer.getChildren().addAll(openFirstInstrumentListButton, firstListSearch, firstListOfInstruments, openFirstSeriesListButton, firstListOfSeries);
        secondSelectionContainer.getChildren().addAll(openSecondInstrumentListButton, secondListSearch, secondListOfInstruments, openSecondSeriesListButton, secondListOfSeries);

        openFirstInstrumentListButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
               Animation.playOpenCloseAnimationOnMenu(firstListOfInstruments, firstListOfSeries, secondListOfInstruments, secondListOfSeries);
            }
        });
        openFirstSeriesListButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Animation.playOpenCloseAnimationOnMenu(firstListOfSeries, firstListOfInstruments, secondListOfInstruments, secondListOfSeries);
            }
        });
        openSecondInstrumentListButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Animation.playOpenCloseAnimationOnMenu(secondListOfInstruments, firstListOfInstruments, firstListOfSeries, secondListOfSeries);
            }
        });
        openSecondSeriesListButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Animation.playOpenCloseAnimationOnMenu(secondListOfSeries, firstListOfInstruments, firstListOfSeries, secondListOfInstruments);
            }
        });
        firstListOfInstruments.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
                SeriesActions.updateSeriesListAndSetButtonText(s2, firstSeriesResults, firstListOfSeries, openFirstInstrumentListButton, firstSelectionPriceSeries);
            }
        });

        secondListOfInstruments.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
                SeriesActions.updateSeriesListAndSetButtonText(s2, secondSeriesResults, secondListOfSeries, openSecondInstrumentListButton, secondSelectionPriceSeries);
            }
        });

        resetButton.setCursor(Cursor.HAND);
        resetButton.getStyleClass().add("button3");
        firstListOfSeries.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableDateValue, String previousSelection, String newSelection) {
                if (newSelection==null){
                    return;
                }
                try {

                    firstSelectionResults = SolrService.getResultsOnInstrumentAndSeries(firstListOfInstruments.getSelectionModel().getSelectedItem(), firstListOfSeries.getSelectionModel().getSelectedItem());
                    if (firstSelectionResults.size() == 1){
                        displayLackOfDataInSeries();
                        return;
                    } else{
                        resetControlsForNewSelection();
                        SeriesActions.completeSeriesFromRemovedPoints(secondSelectionDataRemovedFromBack, secondSelectionDataRemovedFromFront, secondSelectionPriceSeries);
                        firstSelectionDataRemovedFromBack.clear();
                        firstSelectionDataRemovedFromFront.clear();
                        deltaDataRemovedFromBack.clear();
                        deltaDataRemovedFromFront.clear();
                        firstSelectionPriceSeries.getData().clear();
                        firstSeriesDateToPrice.clear();
                        seriesTotal.getData().clear();
                        seriesDiffBar.getData().clear();

                        populateAllSeriesAndMatchBounds(firstSelectionResults, secondSeriesDateToPrice, firstSelectionPriceSeries, seriesTotal, firstSeriesDateToPrice, orderOfSeriesPointsToId, seriesDiffBar);
                    }
                } catch (SolrServerException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                matchBoundsBetweenCharts();

                resetButton.fire();
                openFirstSeriesListButton.setText("Series: " + newSelection);
            }
        });
        secondListOfSeries.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableDateValue, String previousSelection, String newSelection) {
                if (newSelection==null){
                    return;
                }
                try {
                    secondSelectionResults = SolrService.getResultsOnInstrumentAndSeries(secondListOfInstruments.getSelectionModel().getSelectedItem(), secondListOfSeries.getSelectionModel().getSelectedItem());
                    if (secondSelectionResults.size() == 1){
                        displayLackOfDataInSeries();
                        return;
                    }else{
                        resetControlsForNewSelection();
                        SeriesActions.completeSeriesFromRemovedPoints(firstSelectionDataRemovedFromBack, firstSelectionDataRemovedFromFront, firstSelectionPriceSeries);
                        secondSelectionDataRemovedFromBack.clear();
                        secondSelectionDataRemovedFromFront.clear();
                        deltaDataRemovedFromBack.clear();
                        deltaDataRemovedFromFront.clear();
                        secondSelectionPriceSeries.getData().clear();
                        secondSelectionSeriesTotal.getData().clear();
                        seriesDiffBar.getData().clear();
                        secondSeriesDateToPrice.clear();
                        populateAllSeriesAndMatchBounds(secondSelectionResults, firstSeriesDateToPrice, secondSelectionPriceSeries, secondSelectionSeriesTotal, secondSeriesDateToPrice, orderOfSeriesPointsToId, seriesDiffBar);
                    }

                } catch (SolrServerException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                matchBoundsBetweenCharts();
                resetButton.fire();

                openSecondSeriesListButton.setText("Series: " + newSelection);
            }
        });

        firstSelectionContainer.setSpacing(10);
        secondSelectionContainer.setSpacing(10);
        secondSelectionChart.setOnMouseClicked(mouseHandler);
        secondSelectionChart.setOnMouseDragged(mouseHandler);
        secondSelectionChart.setOnMouseMoved(mouseHandler);
        secondSelectionChart.setOnMousePressed(mouseHandler);
        secondSelectionChart.setOnMouseReleased(mouseHandler);
        secondSelectionChart.setOnMouseEntered(mouseHandler);
        secondSelectionChart.setOnMouseExited(mouseHandler);
        lineChartOverview.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                positionXOverviewChart = mouseEvent.getSceneX();
            }
        });

        lineChartOverview.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                double deltaX = positionXOverviewChart - mouseEvent.getSceneX();
                positionXOverviewChart = mouseEvent.getSceneX();
                if (positionXOverviewChart < 45 || positionXOverviewChart > 1390) return;
                if (rightHookPosition.getValue() - deltaX < 1390 && leftHookPosition.getValue() - deltaX >= 0) {

                    leftHookPosition .set(leftHookPosition.getValue() - deltaX);
                    rightHookPosition.set(rightHookPosition.getValue() - deltaX);
                }
                LocalDateTime valueForDisplayFinish = lineChartOverview.getXAxis().getValueForDisplay(rightHookPosition.getValue() - 45.0);
                LocalDateTime valueForDisplayStart  = lineChartOverview.getXAxis().getValueForDisplay(leftHookPosition.getValue());

                lastLatestValue   = String.valueOf(valueForDisplayFinish);
                lastEarliestValue = String.valueOf(valueForDisplayStart);

                firstSelectionChart .getXAxis().setAutoRanging(true);
                secondSelectionChart.getXAxis().setAutoRanging(true);
                try {

                    XYChart.Data<LocalDateTime, Float> firstSelectionFirstDate = getSecondToLatestPoint(firstSelectionPriceSeries);
                    SeriesActions.cutRight(deltaX, firstSelectionFirstDate, firstSelectionPriceSeries, firstSelectionDataRemovedFromFront, lastLatestValue);
                    XYChart.Data<LocalDateTime, Float> firstSelectionLastDate  = (XYChart.Data<LocalDateTime, Float>) firstSelectionPriceSeries.getData().get(1);
                    SeriesActions.cutLeft(deltaX, firstSelectionLastDate, firstSelectionPriceSeries, firstSelectionDataRemovedFromBack, lastEarliestValue);


                    XYChart.Data<LocalDateTime, Float> secondSelectionFirstDate = getSecondToLatestPoint(secondSelectionPriceSeries);
                    SeriesActions.cutRight(deltaX, secondSelectionFirstDate, secondSelectionPriceSeries, secondSelectionDataRemovedFromFront, lastLatestValue);
                    XYChart.Data<LocalDateTime, Float> secondSelectionLastDate  = (XYChart.Data<LocalDateTime, Float>) secondSelectionPriceSeries.getData().get(1);
                    SeriesActions.cutLeft(deltaX, secondSelectionLastDate, secondSelectionPriceSeries, secondSelectionDataRemovedFromBack, lastEarliestValue);


                    XYChart.Data<LocalDateTime, Float> deltaLocalDateTimeFloatData = getSecondToLatestPoint(seriesDiffBar);
                    SeriesActions.cutRight(deltaX, deltaLocalDateTimeFloatData, seriesDiffBar, deltaDataRemovedFromFront, lastLatestValue);
                    XYChart.Data<LocalDateTime, Float> deltaLastData = (XYChart.Data<LocalDateTime, Float>) seriesDiffBar.getData().get(1);
                    SeriesActions.cutLeft(deltaX, deltaLastData, seriesDiffBar, deltaDataRemovedFromBack, lastEarliestValue);

                } catch (Exception e) {
                }
                ChartActions.setChartsUpperBound(valueForDisplayFinish, firstSelectionChart, secondSelectionChart, firstSelectionYAxis, secondSelectionYAxis, diffBarChart);
                ChartActions.setChartsLowerBound(valueForDisplayStart, firstSelectionChart, secondSelectionChart, firstSelectionYAxis, secondSelectionYAxis, diffBarChart);

                initialRightHookPosition = rightHookPosition.getValue();
                initialLeftHookPosition  = leftHookPosition.getValue();
            }
        });
    }

    private XYChart.Data<LocalDateTime, Float> getSecondToLatestPoint(XYChart.Series series) {
        return (XYChart.Data<LocalDateTime, Float>) series.getData().get(series.getData().size() - 2);
    }

    private void displayLackOfDataInSeries() {
        detail.setText("Price Series only contains one point of data");
        detail.setVisible(true);
        detail.setTranslateX(600);
        detail.setTranslateY(300);
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        detail.setVisible(false);
                    }
                });
            }
        };
        timer.schedule(timerTask, 2000);
    }

    private boolean isHookInBounds(MouseEvent mouseEvent) {
        if (rightRect.getLayoutX() - 50 < leftRect.getWidth() || mouseEvent.getSceneX() - 55 < leftRect.getWidth()) {
            rightHookPosition.set(leftRect.getWidth() + 60);
            return true;
        }
        if (mouseEvent.getSceneX() > 1370) {
            rightHookPosition.setValue(1385);
            return true;
        }
        return false;
    }

    private void resetControlsForNewSelection() {
        leftHookPosition .setValue(0);
        rightHookPosition.setValue(1380);
        propertiesPane   .setVisible(false);
        orderOfSeriesPointsToId.clear();
    }

    private void matchBoundsBetweenCharts() {
        DateAxis310 barChartXAxis = (DateAxis310) diffBarChart.getXAxis();
        DateAxis310 firstSelectionChartXAxis = (DateAxis310) firstSelectionChart.getXAxis();
        DateAxis310 secondSelectionChartXAxis = (DateAxis310) secondSelectionChart.getXAxis();

        barChartXAxis.setAutoRanging(false);
        barChartXAxis.lowerBoundProperty().bind(firstSelectionChartXAxis.lowerBoundProperty());
        barChartXAxis.upperBoundProperty().bind(firstSelectionChartXAxis.upperBoundProperty());

        firstSelectionChartXAxis.setAutoRanging(false);
        secondSelectionChartXAxis.setAutoRanging(false);
        Date firstSelectionUpper = new Date();
        Date secondSelectionUpper = new Date();
        XYChart.Data upperDataFirst = (XYChart.Data) firstSelectionPriceSeries.getData().get(firstSelectionPriceSeries.getData().size() - 1);
        XYChart.Data upperDataSecond = (XYChart.Data) secondSelectionPriceSeries.getData().get(secondSelectionPriceSeries.getData().size() - 1);
        try {
            firstSelectionUpper = getSimpleDateFormat(upperDataFirst);
            secondSelectionUpper = getSimpleDateFormat(upperDataSecond);
        } catch (ParseException e) {

        }
        Date firstSelectionLower = new Date();
        Date secondSelectionLower = new Date();
        XYChart.Data lowerDataFirst  = (XYChart.Data) firstSelectionPriceSeries.getData().get(0);
        XYChart.Data lowerDataSecond = (XYChart.Data) secondSelectionPriceSeries.getData().get(0);
        try {
            firstSelectionLower = getSimpleDateFormat(lowerDataFirst);
            secondSelectionLower = getSimpleDateFormat(lowerDataSecond);
        } catch (ParseException e) {

        }
        long lowerBoundFirstSelectionInNano = firstSelectionLower.getTime();
        long lowerBoundSecondSelectionInNano = secondSelectionLower.getTime();

        long upperBoundFirstSelectionInNano = firstSelectionUpper.getTime();
        long upperBoundSecondSelectionInNano = secondSelectionUpper.getTime();
        firstSelectionChartXAxis.setAutoRanging(false);
        secondSelectionChartXAxis.setAutoRanging(false);

        if (lowerBoundFirstSelectionInNano < lowerBoundSecondSelectionInNano){
            secondSelectionChartXAxis.setLowerBound(getLocalDateTime(lowerDataFirst));
            firstSelectionChartXAxis.setLowerBound(getLocalDateTime(lowerDataFirst));
        }else{
            firstSelectionChartXAxis.setLowerBound(getLocalDateTime(lowerDataSecond));
            secondSelectionChartXAxis.setLowerBound(getLocalDateTime(lowerDataSecond));
        }

        if (upperBoundFirstSelectionInNano > upperBoundSecondSelectionInNano){
            secondSelectionChartXAxis.setUpperBound(getLocalDateTime(upperDataFirst));
            firstSelectionChartXAxis.setUpperBound(getLocalDateTime(upperDataFirst));
        }else{
            firstSelectionChartXAxis.setUpperBound(getLocalDateTime(upperDataSecond));
            secondSelectionChartXAxis.setUpperBound(getLocalDateTime(upperDataSecond));
        }
    }

    private Date getSimpleDateFormat(XYChart.Data upperDataFirst) throws ParseException {
        return new SimpleDateFormat(YYYY_MM_DD_T_HH_MM).parse(((LocalDateTime) upperDataFirst.getXValue()).toString());
    }

    public void populateAllSeriesAndMatchBounds(List results, HashMap otherSeriesDateToPrice, XYChart.Series series, XYChart.Series totalSeries, HashMap seriesDateToPrice, HashMap seriesPointsToId, XYChart.Series differenceSeries) {
        for (int c = 0; c <= results.size() - 1; c++) {
            Item item = (Item) results.get(c);
            seriesDateToPrice.put(item.getPrice_date(), item.getPrice());
            try{
                series.getData().add(ChartActions.createChartData(item));
                totalSeries.getData().add(ChartActions.createChartData(item));
                seriesPointsToId.put(c, item.getId());
            }catch (Exception e){
                series.getData().add(ChartActions.createChartData(item));
                seriesPointsToId.put(c, item.getId());
                totalSeries.getData().add(ChartActions.createChartData(item));
            }

            try{
                Float firstPrice  =  Float.valueOf(otherSeriesDateToPrice.get(item.getPrice_date()).toString());
                Float secondPrice = Float.valueOf(item.getPrice());

                Float difference = Math.abs(firstPrice - secondPrice);
                differenceSeries.getData().add(new XYChart.Data((LocalDateTime.parse(Main.toUtcDate(item.getPrice_date()))), difference));
            }catch (Exception e){
            }
        }
        matchBoundsBetweenCharts();
    }

    private LocalDateTime getLocalDateTime(XYChart.Data lowerDataFirst) {
        return LocalDateTime.parse(lowerDataFirst.getXValue().toString());
    }

    private void initComponents(){
        aboveAverages = new ArrayList<>();
        observableAboveAverages = FXCollections.observableArrayList(aboveAverages);
        valuesHighRaw = new ArrayList<>();
        firstSelectionDataRemovedFromFront = new ArrayList<>();
        firstSelectionDataRemovedFromBack  = new ArrayList<>();
        firstListOfInstruments = new ListView<>();

        lineChartPane = new Pane();
        lineChartPane.setFocusTraversable(true);

        yAxis    = new NumberAxis();
        xAxis    = new DateAxis310();
        yBarAxis = new NumberAxis();
        xBarAxis = new DateAxis310();

        firstSelectionYAxis = ChartActions.makeZoomYAxis(lowerBoundForYMain, upperBoundForYMain, firstChartInitYPos, dragArea);
        firstSelectionXAxis = new DateAxis310();
        firstSelectionXAxis.setTickLabelFormatter(STRING_CONVERTER);

        secondSelectionYAxis = ChartActions.makeZoomYAxis(secondSelectionLowerYBound, secondSelectionUpperYBound, secondChartInitYPos, dragArea);
        secondSelectionXAxis = new DateAxis310();
        secondSelectionXAxis.setTickLabelFormatter(STRING_CONVERTER);

        xBarAxis.setTickLabelFormatter(STRING_CONVERTER);
        containingBox = new HBox();
        detailPane  = new Pane();
        miniMapPane = new Pane();
        chartBox = new VBox();

        diffBarChart = new AreaChart<LocalDateTime, Number>(xBarAxis,yBarAxis){
            @Override
            protected void layoutPlotChildren() {
                super.layoutPlotChildren();
                for (Node mark : getPlotChildren()) {
                    if (mark instanceof StackPane) {
                        Bounds bounds = mark.getBoundsInParent();
                        double posX = bounds.getMinX() + (bounds.getMaxX() - bounds.getMinX()) / 2.0;
                        LocalDateTime date = getXAxis().getValueForDisplay(posX).truncatedTo(DAYS);
                    }
                }
            }
        };

        separator = new Separator(Orientation.HORIZONTAL);
        leftRect.setTranslateX(45);
        leftRect.setFill(Color.web("gray", 0.1));
        leftRect.setStroke(Color.GRAY);
        leftRect.widthProperty().bind(leftHookPosition);

        hookLeft.setFill(Color.web("gray", 0.6));
        hookLeft.setTranslateX(37.5);
        hookLeft.setTranslateY(55);
        hookLeft.setStroke(Color.DARKGRAY);
        hookLeft.layoutXProperty().bind(leftHookPosition);
        width = new SimpleDoubleProperty(1450);

        rightRect.setWidth(0);
        rightRect.setFill(Color.web("gray", 0.1));
        rightRect.setStroke(Color.GRAY);
        rightRect.layoutXProperty().bind(rightHookPosition);
        rightRect.widthProperty().bind(width.subtract(rightRect.layoutXProperty()));

        hookRight.setFill(Color.web("gray", 0.6));
        hookRight.setTranslateX(-7.5);
        hookRight.setLayoutX(1310);
        hookRight.setTranslateY(55);
        hookRight.setStroke(Color.DARKGRAY);
        hookRight.layoutXProperty().bind(rightHookPosition);
        rightHookPosition.set(1385);

        formatDisplayLabel(displayAtPosition);
        formatDisplayLabel(displayAtTarget);

        trackX.setMouseTransparent(true);
        trackX.layoutXProperty().bind(trackXPosition);
        trackX.setStroke(Color.DARKGRAY);
        displayAtPosition.layoutXProperty().bind(trackXPosition);
        displayAtTarget.layoutXProperty().bind(trackXTargetPosition);

        zoomBounds = new Rectangle();
        zoomBounds.setFill(Color.web("gray", 0.1));
        zoomBounds.setStroke(Color.DARKGRAY);
        zoomBounds.setStrokeDashOffset(50);
        zoomBounds.setMouseTransparent(true);
        zoomBounds.widthProperty().bind(rectX.subtract(rectinitX));
        zoomBounds.heightProperty().bind(rectY.subtract(rectinitY));

        labelInstruments = new Label("List of Instruments");
        labelInstruments.setFont(new Font("Calibri", 22));
        labelInstruments.setTranslateX(50);

        firstListSearch.textProperty().addListener(new ChangeListener() {
            public void changed(ObservableValue observable, Object oldVal,
                                Object newVal) {
                firstListOfInstruments.getSelectionModel().clearSelection();
                try {
                    search((String) newVal, firstObservableListInstruments, firstListOfInstruments);
                } catch (SolrServerException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        secondListSearch.textProperty().addListener(new ChangeListener() {
            public void changed(ObservableValue observable, Object oldVal,
                                Object newVal) {
                secondListOfInstruments.getSelectionModel().clearSelection();
                try {
                    search((String) newVal, secondObservableListInstruments, secondListOfInstruments);
                } catch (SolrServerException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Reflection r = new Reflection();
        r.setFraction(0.1);
        firstSelectionContainer .getStyleClass().add("b2");
        secondSelectionContainer.getStyleClass().add("b2");
        secondSelectionContainer.setMaxWidth(265);
        firstSelectionContainer .setMaxWidth(265);
        secondSelectionContainer.setMinWidth(265);
        firstSelectionContainer .setMinWidth(265);

        openSecondSeriesListButton    .setEffect(r);
        openSecondInstrumentListButton.setEffect(r);
        openFirstInstrumentListButton .setEffect(r);
        openFirstSeriesListButton     .setEffect(r);

        openSecondSeriesListButton    .setMinWidth(265);
        openSecondInstrumentListButton.setMinWidth(265);
        openFirstInstrumentListButton .setMinWidth(265);
        openFirstSeriesListButton     .setMinWidth(265);

        listBox = new VBox(labelInstruments, resetButton, firstSelectionContainer, secondSelectionContainer);
        listBox.setPadding(new Insets(10, 10, 10, 10));
        listBox.setSpacing(15);
        detailPane.getChildren().addAll(listBox);
        detailPane.setMinWidth(400);

        seriesTotal = new XYChart.Series();
        secondSelectionSeriesTotal = new XYChart.Series();
        firstSelectionPriceSeries.setName("Raw data");

        miniMapDetail = new Text();
        miniMapDetail.setFill(Color.WHITE);
        miniMapDetail.setEffect(new InnerShadow(2, Color.BLACK));
        miniMapDetail.setFont(Font.font(null, FontWeight.BOLD, 10));
        miniMapDetail.setVisible(false);

        detail = new Text();
        detail.setCache(true);
        detail.setFill(Color.GRAY);
        detail.setEffect(new InnerShadow(2, Color.BLACK));
        detail.setFont(Font.font(null, FontWeight.BOLD, 13));
        detail.setVisible(false);

        containingPane = new Pane();

        resetButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                secondSelectionYAxis.lowerBoundProperty().unbind();
                secondSelectionYAxis.upperBoundProperty().unbind();
                secondSelectionYAxis.setAutoRanging(true);
                firstSelectionYAxis.lowerBoundProperty().unbind();
                firstSelectionYAxis.upperBoundProperty().unbind();
                firstSelectionYAxis.setAutoRanging(true);
            }
        });

        lineIndicator.setStroke(Color.DARKGRAY);
        yAxis.setForceZeroInRange(false);
        separator.prefWidthProperty().bind(miniMapPane.widthProperty());
        firstListOfInstruments.setMaxWidth(250);
        firstListOfSeries.setMaxWidth(250);
        firstListOfInstruments.setMaxHeight(300);
        firstListOfSeries.setMaxHeight(300);

        placeHolder = new Text();
        placeHolder.setFill(Color.WHITE);
        placeHolder.setEffect(new InnerShadow(2, Color.BLACK));
        placeHolder.setFont(Font.font(null, FontWeight.BOLD, 13));
        placeHolder.setTranslateY(-100);
        placeHolder.setText("No Spikes");
        firstListOfInstruments.setPlaceholder(placeHolder);
        secondListOfInstruments.setPlaceholder(placeHolder);
    }

    private void formatDisplayLabel(Label label) {
        label.layoutYProperty().bind(trackYPosition);
        label.setMouseTransparent(true);
        label.setTranslateX(10);
        label.setTranslateY(-10);
        label.setEffect(new InnerShadow(2, Color.ORANGE));
        label.setFont(Font.font(null, FontWeight.BOLD, 10));
    }

    private class XTargetChangeListener implements ChangeListener<Number> {
        @Override
        public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
            String displayValueAtLocation = (String.valueOf(firstSelectionChart.getXAxis().getValueForDisplay((Double) number2)));

            displayAtTarget.setText(displayValueAtLocation.substring(0, 10));
        }
    }

    public void search(String newVal, ObservableList list, ListView listView) throws SolrServerException, IOException {
            String value = newVal;
            List<String> instrumentsFromName = SolrService.getInstrumentsFromName(value);
            list = FXCollections.observableArrayList(instrumentsFromName);
            listView.setItems(list);
    }
}
