package worldMap;

import application.Controller;
import application.model.Flight;
import javafx.application.Platform;
import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.StyleablePropertyFactory;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.event.WeakEventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static javafx.scene.input.MouseEvent.MOUSE_ENTERED;
import static javafx.scene.input.MouseEvent.MOUSE_EXITED;
import static javafx.scene.input.MouseEvent.MOUSE_PRESSED;
import static javafx.scene.input.MouseEvent.MOUSE_RELEASED;


@DefaultProperty("children")
public class World extends Region {
    public enum Resolution { HI_RES, LO_RES };
    private static final StyleablePropertyFactory<World> FACTORY          = new StyleablePropertyFactory<>(Region.getClassCssMetaData());
    private static final String                          HIRES_PROPERTIES = "worldMap/hires.properties";
    private static final String                          LORES_PROPERTIES = "worldMap/lores.properties";
    private static final double                          PREFERRED_WIDTH  = 980;
    private static final double                          PREFERRED_HEIGHT = 665;
    private static final double                          MINIMUM_WIDTH    = 100;
    private static final double                          MINIMUM_HEIGHT    = 66;
    private static final double                          MAXIMUM_WIDTH    = 2018/3;
    private static final double                          MAXIMUM_HEIGHT   = 1330/3;
    private static       double                          MAP_OFFSET_X     = -PREFERRED_WIDTH * 0.0285;
    private static       double                          MAP_OFFSET_Y     = PREFERRED_HEIGHT * 0.195;
    private static final double                          ASPECT_RATIO     = PREFERRED_HEIGHT / PREFERRED_WIDTH;
    private static final CssMetaData<World, Color>       BACKGROUND_COLOR = FACTORY.createColorCssMetaData("-background-color", s -> s.backgroundColor, Color.web("#3f3f4f"), false);
    private        final StyleableProperty<Color>        backgroundColor;
    private static final CssMetaData<World, Color>       FILL_COLOR = FACTORY.createColorCssMetaData("-fill-color", s -> s.fillColor, Color.web("#222222"), false);
    private        final StyleableProperty<Color>        fillColor;
    private static final CssMetaData<World, Color>       STROKE_COLOR = FACTORY.createColorCssMetaData("-stroke-color", s -> s.strokeColor, Color.BLACK, false);
    private        final StyleableProperty<Color>        strokeColor;
    private static final CssMetaData<World, Color>       HOVER_COLOR = FACTORY.createColorCssMetaData("-hover-color", s -> s.hoverColor, Color.web("#456acf"), false);
    private        final StyleableProperty<Color>        hoverColor;
    private static final CssMetaData<World, Color>       PRESSED_COLOR = FACTORY.createColorCssMetaData("-pressed-color", s -> s.pressedColor, Color.web("#789dff"), false);
    private        final StyleableProperty<Color>        pressedColor;
    private static final CssMetaData<World, Color>       SELECTED_COLOR = FACTORY.createColorCssMetaData("-selected-color", s-> s.selectedColor, Color.web("#9dff78"), false);
    private        final StyleableProperty<Color>        selectedColor;
    private static final CssMetaData<World, Color>       LOCATION_COLOR = FACTORY.createColorCssMetaData("-location-color", s -> s.locationColor, Color.web("#ff0000"), false);
    private        final StyleableProperty<Color>        locationColor;
    private              BooleanProperty                 hoverEnabled;
    private              BooleanProperty                 selectionEnabled;
    private              ObjectProperty<Country>         selectedCountry;
    private              BooleanProperty                 zoomEnabled;
    private              DoubleProperty                  scaleFactor;
    private              Properties                      resolutionProperties;
    private              Country                         formerSelectedCountry;
    private              double                          zoomSceneX;
    private              double                          zoomSceneY;
    private              double                          width;
    private              double                          height;
    protected            Ikon                            locationIconCode;
    protected            Pane                            pane;
    protected            Group                           group;
    protected            Map<String, List<CountryPath>>  countryPaths;// internal event handlers
    protected            EventHandler<MouseEvent>        _mouseEnterHandler;
    protected            EventHandler<MouseEvent>        _mousePressHandler;
    protected            EventHandler<MouseEvent>        _mouseReleaseHandler;
    protected            EventHandler<MouseEvent>        _mouseExitHandler;
    private              EventHandler<ScrollEvent>       _scrollEventHandler;
    // exposed event handlers
    private              EventHandler<MouseEvent>        mouseEnterHandler;
    private              EventHandler<MouseEvent>        mousePressHandler;
    private              EventHandler<MouseEvent>        mouseReleaseHandler;
    private              EventHandler<MouseEvent>        mouseExitHandler;
    private              Controller                      controller;

    //private              Game                            game;


    // ******************** Constructors **************************************
    public World(final Resolution RESOLUTION,Controller controller) {
        this.controller = controller;
        if (controller == null) {
            System.out.println("this controller");
        }
        //game = new Game(this);
        System.out.println(Resolution.HI_RES == RESOLUTION ? World.HIRES_PROPERTIES : World.LORES_PROPERTIES);
        resolutionProperties = readProperties(Resolution.HI_RES == RESOLUTION ? World.HIRES_PROPERTIES : World.LORES_PROPERTIES);
        backgroundColor      = new StyleableObjectProperty<Color>(BACKGROUND_COLOR.getInitialValue(World.this)) {
            @Override protected void invalidated() { setBackground(new Background(new BackgroundFill(get(), CornerRadii.EMPTY, Insets.EMPTY))); }
            @Override public Object getBean() { return World.this; }
            @Override public String getName() { return "backgroundColor"; }
            @Override public CssMetaData<? extends Styleable, Color> getCssMetaData() { return BACKGROUND_COLOR; }
        };
        fillColor            = new StyleableObjectProperty<Color>(FILL_COLOR.getInitialValue(World.this)) {
            @Override protected void invalidated() { setFillAndStroke(); }
            @Override public Object getBean() { return World.this; }
            @Override public String getName() { return "fillColor"; }
            @Override public CssMetaData<? extends Styleable, Color> getCssMetaData() { return FILL_COLOR; }
        };
        strokeColor          = new StyleableObjectProperty<Color>(STROKE_COLOR.getInitialValue(World.this)) {
            @Override protected void invalidated() { setFillAndStroke(); }
            @Override public Object getBean() { return World.this; }
            @Override public String getName() { return "strokeColor"; }
            @Override public CssMetaData<? extends Styleable, Color> getCssMetaData() { return STROKE_COLOR; }
        };
        hoverColor           = new StyleableObjectProperty<Color>(HOVER_COLOR.getInitialValue(World.this)) {
            @Override protected void invalidated() { }
            @Override public Object getBean() { return World.this; }
            @Override public String getName() { return "hoverColor"; }
            @Override public CssMetaData<? extends Styleable, Color> getCssMetaData() { return HOVER_COLOR; }
        };
        pressedColor         = new StyleableObjectProperty<Color>(PRESSED_COLOR.getInitialValue(this)) {
            @Override protected void invalidated() {}
            @Override public Object getBean() { return World.this; }
            @Override public String getName() { return "pressedColor"; }
            @Override public CssMetaData<? extends Styleable, Color> getCssMetaData() { return PRESSED_COLOR; }
        };
        selectedColor        = new StyleableObjectProperty<Color>(SELECTED_COLOR.getInitialValue(this)) {
            @Override protected void invalidated() {}
            @Override public Object getBean() { return World.this; }
            @Override public String getName() { return "selectedColor"; }
            @Override public CssMetaData<? extends Styleable, Color> getCssMetaData() { return SELECTED_COLOR; }
        };
        locationColor        = new StyleableObjectProperty<Color>(LOCATION_COLOR.getInitialValue(this)) {
            @Override protected void invalidated() {
            }
            @Override public Object getBean() { return World.this; }
            @Override public String getName() { return "locationColor"; }
            @Override public CssMetaData<? extends Styleable, Color> getCssMetaData() { return LOCATION_COLOR; }
        };
        hoverEnabled         = new BooleanPropertyBase(true) {
            @Override protected void invalidated() {}
            @Override public Object getBean() { return World.this; }
            @Override public String getName() { return "hoverEnabled"; }
        };
        selectionEnabled     = new BooleanPropertyBase(false) {
            @Override protected void invalidated() {}
            @Override public Object getBean() { return World.this; }
            @Override public String getName() { return "selectionEnabled"; }
        };
        selectedCountry      = new ObjectPropertyBase<Country>() {
            @Override protected void invalidated() {}
            @Override public Object getBean() { return World.this; }
            @Override public String getName() { return "selectedCountry"; }
        };
        zoomEnabled          = new BooleanPropertyBase(false) {
            @Override protected void invalidated() {
                if (null == getScene()) return;
                if (get()) {
                    getScene().addEventFilter(ScrollEvent.ANY, _scrollEventHandler);
                } else {
                    getScene().removeEventFilter(ScrollEvent.ANY, _scrollEventHandler);
                }
            }
            @Override public Object getBean() { return World.this; }
            @Override public String getName() { return "zoomEnabled"; }
        };
        scaleFactor          = new DoublePropertyBase(1.0) {
            @Override protected void invalidated() {
                if (isZoomEnabled()) {
                    setScaleX(get());
                    setScaleY(get());
                }
            }
            @Override public Object getBean() { return World.this; }
            @Override public String getName() { return "scaleFactor"; }
        };
        countryPaths         = createCountryPaths();

        locationIconCode     = MaterialDesign.MDI_CHECKBOX_BLANK_CIRCLE;
        pane                 = new Pane();
        group                = new Group();

        _mouseEnterHandler   = evt -> handleMouseEvent(evt, mouseEnterHandler);
        _mousePressHandler   = evt -> handleMouseEvent(evt, mousePressHandler);
        _mouseReleaseHandler = evt -> handleMouseEvent(evt, mouseReleaseHandler);
        _mouseExitHandler    = evt -> handleMouseEvent(evt, mouseExitHandler);
        _scrollEventHandler  = evt -> {
            if (group.getTranslateX() != 0 || group.getTranslateY() != 0) { resetZoom(); }
            double delta    = 1.2;
            double scale    = getScaleFactor();
            double oldScale = scale;
            scale           = evt.getDeltaY() < 0 ? scale / delta : scale * delta;
            scale           = clamp( 1, 10, scale);
            double factor   = (scale / oldScale) - 1;
            if (Double.compare(1, getScaleFactor()) == 0) {
                zoomSceneX = evt.getSceneX();
                zoomSceneY = evt.getSceneY();
                resetZoom();
            }
            double deltaX = (zoomSceneX - (getBoundsInParent().getWidth() / 2 + getBoundsInParent().getMinX()));
            double deltaY = (zoomSceneY - (getBoundsInParent().getHeight() / 2 + getBoundsInParent().getMinY()));
            setScaleFactor(scale);
            setPivot(deltaX * factor, deltaY * factor);

            evt.consume();
        };

        initGraphics();
        registerListeners();
    }


    // ******************** Initialization ************************************
    private void initGraphics() {
        if (Double.compare(getPrefWidth(), 0.0) <= 0 || Double.compare(getPrefHeight(), 0.0) <= 0 ||
            Double.compare(getWidth(), 0.0) <= 0 || Double.compare(getHeight(), 0.0) <= 0) {
            if (getPrefWidth() > 0 && getPrefHeight() > 0) {
                setPrefSize(getPrefWidth(), getPrefHeight());
            } else {
                setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
            }
        }

        getStyleClass().add("world");

        Color fill   = getFillColor();
        Color stroke = getStrokeColor();

        countryPaths.forEach((name, pathList) -> {
            Country country = Country.valueOf(name);
            pathList.forEach(path -> {
                path.setFill(null == country.getColor() ? fill : country.getColor());
                path.setStroke(stroke);
                path.setStrokeWidth(0.2);
                path.setOnMouseEntered(new WeakEventHandler<>(_mouseEnterHandler));
                path.setOnMousePressed(new WeakEventHandler<>(_mousePressHandler));
                path.setOnMouseReleased(new WeakEventHandler<>(_mouseReleaseHandler));
                path.setOnMouseExited(new WeakEventHandler<>(_mouseExitHandler));
            });
            pane.getChildren().addAll(pathList);
        });

        group.getChildren().add(pane);

        getChildren().setAll(group);

        setBackground(new Background(new BackgroundFill(getBackgroundColor(), CornerRadii.EMPTY, Insets.EMPTY)));
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        sceneProperty().addListener(o -> {
            if (isZoomEnabled()) { getScene().addEventFilter( ScrollEvent.ANY, new WeakEventHandler<>(_scrollEventHandler)); }
        });
    }


    // ******************** Methods *******************************************
    @Override protected double computeMinWidth(final double HEIGHT)  { return MINIMUM_WIDTH; }
    @Override protected double computeMinHeight(final double WIDTH)  { return MINIMUM_HEIGHT; }
    @Override protected double computePrefWidth(final double HEIGHT) { return super.computePrefWidth(HEIGHT); }
    @Override protected double computePrefHeight(final double WIDTH) { return super.computePrefHeight(WIDTH); }
    @Override protected double computeMaxWidth(final double HEIGHT)  { return MAXIMUM_WIDTH; }
    @Override protected double computeMaxHeight(final double WIDTH)  { return MAXIMUM_HEIGHT; }

    @Override public ObservableList<Node> getChildren() { return super.getChildren(); }

    public Map<String, List<CountryPath>> getCountryPaths() { return countryPaths; }

    public void setMouseEnterHandler(final EventHandler<MouseEvent> HANDLER) { mouseEnterHandler = HANDLER; }
    public void setMousePressHandler(final EventHandler<MouseEvent> HANDLER) { mousePressHandler = HANDLER; }
    public void setMouseReleaseHandler(final EventHandler<MouseEvent> HANDLER) { mouseReleaseHandler = HANDLER;  }
    public void setMouseExitHandler(final EventHandler<MouseEvent> HANDLER) { mouseExitHandler = HANDLER; }

    public Color getBackgroundColor() { return backgroundColor.getValue(); }
    public void setBackgroundColor(final Color COLOR) { backgroundColor.setValue(COLOR); }
    public ObjectProperty<Color> backgroundColorProperty() { return (ObjectProperty<Color>) backgroundColor; }

    public Color getFillColor() { return fillColor.getValue(); }
    public void setFillColor(final Color COLOR) { fillColor.setValue(COLOR); }
    public ObjectProperty<Color> fillColorProperty() { return (ObjectProperty<Color>) fillColor; }

    public Color getStrokeColor() { return strokeColor.getValue(); }
    public void setStrokeColor(final Color COLOR) { strokeColor.setValue(COLOR); }
    public ObjectProperty<Color> strokeColorProperty() { return (ObjectProperty<Color>) strokeColor; }

    public Color getHoverColor() { return hoverColor.getValue(); }
    public void setHoverColor(final Color COLOR) { hoverColor.setValue(COLOR); }
    public ObjectProperty<Color> hoverColorProperty() { return (ObjectProperty<Color>) hoverColor; }

    public Color getPressedColor() { return pressedColor.getValue(); }
    public void setPressedColor(final Color COLOR) { pressedColor.setValue(COLOR); }
    public ObjectProperty<Color> pressedColorProperty() { return (ObjectProperty<Color>) pressedColor; }

    public Color getSelectedColor() { return selectedColor.getValue(); }
    public void setSelectedColor(final Color COLOR) { selectedColor.setValue(COLOR); }
    public ObjectProperty<Color> selectedColorProperty() { return (ObjectProperty<Color>) selectedColor; }

    public Color getLocationColor() { return locationColor.getValue(); }
    public void setLocationColor(final Color COLOR) { locationColor.setValue(COLOR); }
    public ObjectProperty<Color> locationColorProperty() { return (ObjectProperty<Color>) locationColor; }

    public boolean isHoverEnabled() { return hoverEnabled.get(); }
    public void setHoverEnabled(final boolean ENABLED) { hoverEnabled.set(ENABLED); }
    public BooleanProperty hoverEnabledProperty() { return hoverEnabled; }

    public boolean isSelectionEnabled() { return selectionEnabled.get(); }
    public void setSelectionEnabled(final boolean ENABLED) { selectionEnabled.set(ENABLED); }
    public BooleanProperty selectionEnabledProperty() { return selectionEnabled; }

    public Country getSelectedCountry() { return selectedCountry.get(); }
    public void setSelectedCountry(final Country COUNTRY) { selectedCountry.set(COUNTRY); }
    public ObjectProperty<Country> selectedCountryProperty() { return selectedCountry; }

    public boolean isZoomEnabled() { return zoomEnabled.get(); }
    public void setZoomEnabled(final boolean ENABLED) { zoomEnabled.set(ENABLED); }
    public BooleanProperty zoomEnabledProperty() { return zoomEnabled; }

    public double getScaleFactor() { return scaleFactor.get(); }
    public void setScaleFactor(final double FACTOR) { scaleFactor.set(FACTOR); }
    public DoubleProperty scaleFactorProperty() { return scaleFactor; }

    public void resetZoom() {
        setScaleFactor(1.0);
        setTranslateX(0);
        setTranslateY(0);
        group.setTranslateX(0);
        group.setTranslateY(0);
    }

    public Ikon getLocationIconCode() { return locationIconCode; }
    public void setLocationIconCode(final Ikon ICON_CODE) { locationIconCode = ICON_CODE; }

    public void zoomToCountry(final Country COUNTRY) {
        if (!isZoomEnabled()) return;
        if (null != getSelectedCountry()) {
            setCountryFillAndStroke(getSelectedCountry(), getFillColor(), getStrokeColor());
        }
        zoomToArea(getBounds(COUNTRY));
    }

    public void zoomToRegion(final CRegion REGION) {
        if (!isZoomEnabled()) return;
        if (null != getSelectedCountry()) {
            setCountryFillAndStroke(getSelectedCountry(), getFillColor(), getStrokeColor());
        }
        zoomToArea(getBounds(REGION.getCountries()));
    }

    public static double[] latLonToXY(final double LATITUDE, final double LONGITUDE) {
        double x = (LONGITUDE + 180) * (PREFERRED_WIDTH / 360) + MAP_OFFSET_X;
        double y = (PREFERRED_HEIGHT / 2) - (PREFERRED_WIDTH * (Math.log(Math.tan((Math.PI / 4) + (Math.toRadians(LATITUDE) / 2)))) / (2 * Math.PI)) + MAP_OFFSET_Y;
        return new double[]{ x, y };
    }

    private double[] getBounds(final Country... COUNTRIES) { return getBounds(Arrays.asList(COUNTRIES)); }
    private double[] getBounds(final List<Country> COUNTRIES) {
        double upperLeftX  = PREFERRED_WIDTH;
        double upperLeftY  = PREFERRED_HEIGHT;
        double lowerRightX = 0;
        double lowerRightY = 0;
        for (Country country : COUNTRIES) {
            List<CountryPath> paths = countryPaths.get(country.getName());
            for (int i = 0; i < paths.size(); i++) {
                CountryPath path   = paths.get(i);
                Bounds      bounds = path.getLayoutBounds();
                upperLeftX  = Math.min(bounds.getMinX(), upperLeftX);
                upperLeftY  = Math.min(bounds.getMinY(), upperLeftY);
                lowerRightX = Math.max(bounds.getMaxX(), lowerRightX);
                lowerRightY = Math.max(bounds.getMaxY(), lowerRightY);
            }
        }
        return new double[]{ upperLeftX, upperLeftY, lowerRightX, lowerRightY };
    }

    private void zoomToArea(final double[] BOUNDS) {
        group.setTranslateX(0);
        group.setTranslateY(0);
        double      areaWidth   = BOUNDS[2] - BOUNDS[0];
        double      areaHeight  = BOUNDS[3] - BOUNDS[1];
        double      areaCenterX = BOUNDS[0] + areaWidth * 0.5;
        double      areaCenterY = BOUNDS[1] + areaHeight * 0.5;
        Orientation orientation = areaWidth < areaHeight ? Orientation.VERTICAL : Orientation.HORIZONTAL;
        double sf = 1.0;
        switch(orientation) {
            case VERTICAL  : sf = clamp(1.0, 10.0, 1 / (areaHeight / height)); break;
            case HORIZONTAL: sf = clamp(1.0, 10.0, 1 / (areaWidth / width)); break;
        }

        /*
        Rectangle bounds = new Rectangle(BOUNDS[0], BOUNDS[1], areaWidth, areaHeight);
        bounds.setFill(Color.TRANSPARENT);
        bounds.setStroke(Color.RED);
        bounds.setStrokeWidth(0.5);
        bounds.setMouseTransparent(true);
        group.getChildren().add(bounds);
        */

        setScaleFactor(sf);
        group.setTranslateX(width * 0.8 - (areaCenterX));
        group.setTranslateY(height * 0.8 - (areaCenterY));
    }

    private void setPivot(final double X, final double Y) {
        setTranslateX(getTranslateX() - X);
        setTranslateY(getTranslateY() - Y);
    }

    private static ArrayList<Flight> flights = new ArrayList<>();
    private void handleMouseEvent(final MouseEvent EVENT, final EventHandler<MouseEvent> HANDLER) {
        final CountryPath       COUNTRY_PATH = (CountryPath) EVENT.getSource();
        final String            COUNTRY_NAME = COUNTRY_PATH.getName();
        final Country           COUNTRY      = Country.valueOf(COUNTRY_NAME);
        final List<CountryPath> PATHS        = countryPaths.get(COUNTRY_NAME);

        final EventType TYPE = EVENT.getEventType();
        if (MOUSE_ENTERED == TYPE) {
            if (isHoverEnabled()) {
            Color color = isSelectionEnabled() && COUNTRY.equals(getSelectedCountry()) ? getSelectedColor() : getHoverColor();
            for (SVGPath path : PATHS) { path.setFill(color); }
            }
        } else if (MOUSE_PRESSED == TYPE) {
            if (isSelectionEnabled()) {
                Color color;
                if (null == getSelectedCountry()) {
                    setSelectedCountry(COUNTRY);
                    color = getSelectedColor();
                } else {
                    color = null == getSelectedCountry().getColor() ? getFillColor() : getSelectedCountry().getColor();
                }
                for (SVGPath path : countryPaths.get(getSelectedCountry().getName())) { path.setFill(color); }
            } else {
                if (isHoverEnabled()) {
                for (SVGPath path : PATHS) { path.setFill(getPressedColor()); }
            }
            }
        } else if (MOUSE_RELEASED == TYPE) {
            Color color;
            if (isSelectionEnabled()) {
                if (formerSelectedCountry == COUNTRY) {
                    setSelectedCountry(null);
                    color = null == COUNTRY.getColor() ? getFillColor() : COUNTRY.getColor();
                } else {
                    setSelectedCountry(COUNTRY);
                    System.out.println("Pressed country: " + COUNTRY_NAME);
                    //game.checkAnswer(COUNTRY_NAME);
                    try {
                        getFlights(convert(COUNTRY_NAME));
                        //Controller.setOutput_info(NewScene.showNewScene(COUNTRY_NAME, resor));
                        //Controller.fyllTable(NewScene.showNewScene(COUNTRY_NAME, resor));
                        System.out.println(flights.get(1));
                        if (controller == null) {
                            System.out.println("Null controller");
                        }
                        controller.fillFlights(flights);


                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    color = getSelectedColor();
                }
                formerSelectedCountry = getSelectedCountry();
            } else {
                color = getHoverColor();
            }
            if (isHoverEnabled()) {
            for (SVGPath path : PATHS) { path.setFill(color); }
            }
        } else if (MOUSE_EXITED == TYPE) {
            if (isHoverEnabled()) {
            Color color = isSelectionEnabled() && COUNTRY.equals(getSelectedCountry()) ? getSelectedColor() : getFillColor();
            for (SVGPath path : PATHS) {
                path.setFill(null == COUNTRY.getColor() || COUNTRY == getSelectedCountry() ? color : COUNTRY.getColor());
            }
        }
        }

        if (null != HANDLER) HANDLER.handle(EVENT);
    }

    private void setFillAndStroke() {
        countryPaths.keySet().forEach(name -> {
            Country country = Country.valueOf(name);
            setCountryFillAndStroke(country, null == country.getColor() ? getFillColor() : country.getColor(), getStrokeColor());
        });
    }
    private void setCountryFillAndStroke(final Country COUNTRY, final Color FILL, final Color STROKE) {
        List<CountryPath> paths = countryPaths.get(COUNTRY.getName());
        for (CountryPath path : paths) {
            path.setFill(FILL);
            path.setStroke(STROKE);
        }
    }

    private void addShapesToScene(final Shape... SHAPES) {
        addShapesToScene(Arrays.asList(SHAPES));
    }
    private void addShapesToScene(final Collection<Shape> SHAPES) {
        if (null == getScene()) return;
        Platform.runLater(() -> pane.getChildren().addAll(SHAPES));
    }

    private double clamp(final double MIN, final double MAX, final double VALUE) {
        if (VALUE < MIN) return MIN;
        if (VALUE > MAX) return MAX;
        return VALUE;
    }

    private Properties readProperties(final String FILE_NAME) {
        System.out.println(Thread.currentThread().getContextClassLoader());
        final ClassLoader LOADER     = Thread.currentThread().getContextClassLoader();
        final Properties  PROPERTIES = new Properties();
        try(InputStream resourceStream = LOADER.getResourceAsStream(FILE_NAME)) {
            PROPERTIES.load(resourceStream);
        } catch (IOException exception) {
            System.out.println(exception);
        }
        return PROPERTIES;
    }

    private Map<String, List<CountryPath>> createCountryPaths() {
        Map<String, List<CountryPath>> countryPaths = new HashMap<>();
        resolutionProperties.forEach((key, value) -> {
            String            name     = key.toString();
            List<CountryPath> pathList = new ArrayList<>();
            for (String path : value.toString().split(";")) { pathList.add(new CountryPath(name, path)); }
            countryPaths.put(name, pathList);
        });
        return countryPaths;
    }


    // ******************** Style related *************************************
    @Override public String getUserAgentStylesheet() {
        return World.class.getResource("world.css").toExternalForm();
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() { return FACTORY.getCssMetaData(); }

    @Override public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() { return FACTORY.getCssMetaData(); }


    // ******************** Resizing ******************************************
    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();

        if (ASPECT_RATIO * width > height) {
            width = 1 / (ASPECT_RATIO / height);
        } else if (1 / (ASPECT_RATIO / height) > width) {
            height = ASPECT_RATIO * width;
        }

        if (width > 0 && height > 0) {
            if (isZoomEnabled()) resetZoom();

            pane.setCache(true);
            pane.setCacheHint(CacheHint.SCALE);

            pane.setScaleX(width / PREFERRED_WIDTH);
            pane.setScaleY(height / PREFERRED_HEIGHT);

            group.resize(width, height);
            group.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);

            pane.setCache(false);
        }
    }

    public static ArrayList<Flight> getFlights(String country) throws SQLException {
        Connection con = getDatabaseConnection();
        Statement stmt = con.createStatement();
        flights.clear();
        stmt.executeUpdate("SET search_path TO jetstream;");
        ResultSet flight = stmt.executeQuery("select * from flight where f_departure = '" + country + "';");
        while (flight.next()){
            String destination = flight.getString("f_destination");
            String date = flight.getString("f_date");
            String time = flight.getString("f_time");
            System.out.println(destination + " | Date: " + date);
            flights.add(new Flight(country, destination,date, time));
        }

        con.close();
        stmt.close();
        return flights;
    }

    public static Connection getDatabaseConnection() {

        String url = "jdbc:postgresql://pgserver.mau.se:5432/am2510";
        String user = "am2510";
        String password = "zyvl0ir7";

        Connection con = null;

        try {
            con = DriverManager.getConnection(url, user, password);
            return con;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String convert(String s){
        switch(s){
            case "AD": s = "Andorra"; break;
            case "AE": s = "United_Arab_Emirates"; break;
            case "AF": s = "Afghanistan"; break;
            case "AG": s = "Antigua_And_Barbuda"; break;
            case "AI": s = "Anguilla"; break;
            case "AL": s = "Albania"; break;
            case "AM": s = "Armenia"; break;
            case "AO": s = "Angola"; break;
            case "AR": s = "Argentina"; break;
            case "AS": s = "American_Samoa"; break;
            case "AT": s = "Austria"; break;
            case "AU": s = "Australia"; break;
            case "AW": s = "Aruba"; break;
            case "AX": s = "Åland"; break;
            case "AZ": s = "Azerbaijan"; break;
            case "BA": s = "Bosnia_And_Herzegovina"; break;
            case "BB": s = "Barbados"; break;
            case "BD": s = "Bangladesh"; break;
            case "BE": s = "Belgium"; break;
            case "BF": s = "Burkina_Faso"; break;
            case "BG": s = "Bulgaria"; break;
            case "BH": s = "Bahrain"; break;
            case "BI": s = "Burundi"; break;
            case "BJ": s = "Benin"; break;
            case "BL": s = "Saint_Barthelemy"; break;
            case "BN": s = "Brunei_Darussalam"; break;
            case "BO": s = "Bolivia"; break;
            case "BM": s = "Bermuda"; break;
            case "BQ": s = "Bonaire"; break;
            case "BR": s = "Brazil"; break;
            case "BS": s = "Bahamas"; break;
            case "BT": s = "Bhutan"; break;
            case "BV": s = "Bouvet_Island"; break;
            case "BW": s = "Botswana"; break;
            case "BY": s = "Belarus"; break;
            case "BZ": s = "Belize"; break;
            case "CA": s = "Canada"; break;
            case "CC": s = "Cocos_Islands"; break;
            case "CD": s = "Congo_Democratic_Republic"; break;
            case "CF": s = "Central_African_Republic"; break;
            case "CG": s = "Congo_Republic"; break;
            case "CH": s = "Switzerland"; break;
            case "CI": s = "Cote_D’Ivoire"; break;
            case "CK": s = "Cook_Islands"; break;
            case "CL": s = "Chile"; break;
            case "CM": s = "Cameroon"; break;
            case "CN": s = "China"; break;
            case "CO": s = "Colombia"; break;
            case "CR": s = "Costa_Rica"; break;
            case "CU": s = "Cuba"; break;
            case "CV": s = "Cape_Verde"; break;
            case "CW": s = "Curaçao"; break;
            case "CX": s = "Christmas Island"; break;
            case "CY": s = "Cyprus"; break;
            case "CZ": s = "Czech Republic"; break;
            case "DE": s = "Germany"; break;
            case "DJ": s = "Djibouti"; break;
            case "DK": s = "Denmark"; break;
            case "DM": s = "Dominica"; break;
            case "DO": s = "Dominican_Republic"; break;
            case "DZ": s = "Algeria"; break;
            case "EC": s = "Ecuador"; break;
            case "EG": s = "Egypt"; break;
            case "EE": s = "Estonia"; break;
            case "EH": s = "Western_Sahara"; break;
            case "ER": s = "Eritrea"; break;
            case "ES": s = "Spain"; break;
            case "ET": s = "Ethiopia"; break;
            case "FI": s = "Finland"; break;
            case "FJ": s = "Fiji"; break;
            case "FK": s = "Falkland_Islands"; break;
            case "FM": s = "Micronesia"; break;
            case "FO": s = "Faroe_Islands"; break;
            case "FR": s = "France"; break;
            case "GA": s = "Gabon"; break;
            case "GB": s = "United_Kingdom"; break;
            case "GE": s = "Georgia"; break;
            case "GD": s = "Grenada"; break;
            case "GF": s = "French_Guiana"; break;
            case "GG": s = "Guernsey"; break;
            case "GH": s = "Ghana"; break;
            case "GI": s = "Gibraltar"; break;
            case "GL": s = "Greenland"; break;
            case "GM": s = "Gambia"; break;
            case "GN": s = "Guinea"; break;
            case "GO": s = "Gabon"; break;
            case "GP": s = "Guadeloupe"; break;
            case "GQ": s = "Equatorial_Guinea"; break;
            case "GR": s = "Greece"; break;
            case "GS": s = "South_Georgia"; break;
            case "GT": s = "Guatemala"; break;
            case "GU": s = "Guam"; break;
            case "GW": s = "Guinea-Bissau"; break;
            case "GY": s = "Guyana"; break;
            case "HK": s = "Hong_Kong"; break;
            case "HM": s = "Heard_And_Mc_Donald_Islands"; break;
            case "HN": s = "Honduras"; break;
            case "HR": s = "Croatia"; break;
            case "HT": s = "Haiti"; break;
            case "HU": s = "Hungary"; break;
            case "ID": s = "Indonesia"; break;
            case "IE": s = "Ireland"; break;
            case "IL": s = "Israel"; break;
            case "IM": s = "Isle_of_Man"; break;
            case "IN": s = "India"; break;
            case "IO": s = "British_Indian_Ocean_Territory"; break;
            case "IQ": s = "Iraq"; break;
            case "IR": s = "Iran"; break;
            case "IS": s = "Iceland"; break;
            case "IT": s = "Italy"; break;
            case "JE": s = "Jersey"; break;
            case "JM": s = "Jamaica"; break;
            case "JO": s = "Jordan"; break;
            case "JP": s = "Japan"; break;
            case "JU": s = "Juan_de_Nova_Island"; break;
            case "KE": s = "Kenya"; break;
            case "KG": s = "Kyrgyzstan"; break;
            case "KH": s = "Cambodia"; break;
            case "KI": s = "Kiribati"; break;
            case "KM": s = "Comoros"; break;
            case "KN": s = "Saint_Kitts_And_Nevis"; break;
            case "KP": s = "North Korea"; break;
            case "KR": s = "South Korea"; break;
            case "XK": s = "Kosovo"; break;
            case "KV": s = "Kosovo"; break;
            case "KW": s = "Kuwait"; break;
            case "KY": s = "Cayman_Islands"; break;
            case "KZ": s = "Kazakhstan"; break;
            case "LA": s = "Lao"; break;
            case "LB": s = "Lebanon"; break;
            case "LC": s = "Saint_Lucia"; break;
            case "LI": s = "Liechtenstein"; break;
            case "LK": s = "Sri_Lanka"; break;
            case "LR": s = "Liberia"; break;
            case "LS": s = "Lesotho"; break;
            case "LT": s = "Lithuania"; break;
            case "LU": s = "Luxembourg"; break;
            case "LV": s = "Latvia"; break;
            case "LY": s = "Libyan"; break;
            case "MA": s = "Morocco"; break;
            case "MC": s = "Monaco"; break;
            case "MD": s = "Moldova"; break;
            case "MG": s = "Madagascar"; break;
            case "ME": s = "Montenegro"; break;
            case "MF": s = "Saint_Martin"; break;
            case "MH": s = "Marshall_Islands"; break;
            case "MK": s = "Macedonia"; break;
            case "ML": s = "Mali"; break;
            case "MO": s = "Macau"; break;
            case "MM": s = "Myanmar"; break;
            case "MN": s = "Mongolia"; break;
            case "MP": s = "Northern_Mariana_Islands"; break;
            case "MQ": s = "Martinique"; break;
            case "MR": s = "Mauritania"; break;
            case "MS": s = "Montserrat"; break;
            case "MT": s = "Malta"; break;
            case "MU": s = "Mauritius"; break;
            case "MV": s = "Maldives"; break;
            case "MW": s = "Malawi"; break;
            case "MX": s = "Mexico"; break;
            case "MY": s = "Malaysia"; break;
            case "MZ": s = "Mozambique"; break;
            case "NA": s = "Namibia"; break;
            case "NC": s = "New Caledonia"; break;
            case "NE": s = "Niger"; break;
            case "NF": s = "Norfolk_Island"; break;
            case "NG": s = "Nigeria"; break;
            case "NI": s = "Nicaragua"; break;
            case "NL": s = "Netherlands"; break;
            case "NO": s = "Norway"; break;
            case "NP": s = "Nepal"; break;
            case "NR": s = "Nauru"; break;
            case "NU": s = "Niue"; break;
            case "NZ": s = "New Zealand"; break;
            case "OM": s = "Oman"; break;
            case "PA": s = "Panama"; break;
            case "PE": s = "Peru"; break;
            case "PF": s = "French_Polynesia"; break;
            case "PG": s = "Papua_New_Guinea"; break;
            case "PH": s = "Philippines"; break;
            case "PK": s = "Pakistan"; break;
            case "PL": s = "Poland"; break;
            case "PM": s = "St_Pierre_And_Miquelon"; break;
            case "PN": s = "Pitcairn"; break;
            case "PR": s = "Puerto_Rico"; break;
            case "PS": s = "Palestine"; break;
            case "PT": s = "Portugal"; break;
            case "PW": s = "Palau"; break;
            case "PY": s = "Paraguay"; break;
            case "QA": s = "Qatar"; break;
            case "RE": s = "Reunion"; break;
            case "RO": s = "Romania"; break;
            case "RS": s = "Serbia"; break;
            case "RU": s = "Russia"; break;
            case "RW": s = "Rwanda"; break;
            case "SA": s = "Saudi_Arabia"; break;
            case "SB": s = "Solomon_Islands"; break;
            case "SC": s = "Seychelles"; break;
            case "SD": s = "Sudan"; break;
            case "SE": s = "Sweden"; break;
            case "SG": s = "Singapore"; break;
            case "SH": s = "St_Helena"; break;
            case "SI": s = "Slovenia"; break;
            case "SJ": s = "Svalbard"; break;
            case "SK": s = "Slovakia"; break;
            case "SL": s = "Sierra_Leone"; break;
            case "SM": s = "San_Marino"; break;
            case "SN": s = "Senegal"; break;
            case "SO": s = "Somalia"; break;
            case "SR": s = "Suriname"; break;
            case "SS": s = "South_Sudan"; break;
            case "ST": s = "Sao_Tome_And_Principe"; break;
            case "SV": s = "El_Salvador"; break;
            case "SX": s = "Sint Maarten"; break;
            case "SY": s = "Syria"; break;
            case "SZ": s = "Swaziland"; break;
            case "TC": s = "Turks_And_Caicos_Islands"; break;
            case "TD": s = "Chard"; break;
            case "TF": s = "French_Southern"; break;
            case "TG": s = "Togo"; break;
            case "TH": s = "Thailand"; break;
            case "TJ": s = "Tajikistan"; break;
            case "TK": s = "Tokelau"; break;
            case "TL": s = "Timor_Leste"; break;
            case "TM": s = "Turkmenistan"; break;
            case "TN": s = "Tunisia"; break;
            case "TO": s = "Tonga"; break;
            case "TR": s = "Turkey"; break;
            case "TT": s = "Trinidad_And_Tobago"; break;
            case "TV": s = "Tuvalu"; break;
            case "TW": s = "Taiwan"; break;
            case "TZ": s = "Tanzania"; break;
            case "UA": s = "Uruguay"; break;
            case "UG": s = "Uganda"; break;
            case "UM_DQ": s = "Jarvis_Island"; break;
            case "UM_FQ": s = "Baker_Island"; break;
            case "UM_HQ": s = "Howland_Island"; break;
            case "UM_JQ": s = "Johnston_Atoll"; break;
            case "UM_MQ": s = "Midway_Islands"; break;
            case "UM_WQ": s = "United_States_Virgin_Islands"; break;
            case "US": s = "United_States"; break;
            case "UY": s = "Uruguay"; break;
            case "UZ": s = "Uzbekistan"; break;
            case "VA": s = "Vatikan_City"; break;
            case "VC": s = "Saint_Vincent"; break;
            case "VE": s = "Venezuela"; break;
            case "VG": s = "VirginIslands_BR"; break;
            case "VI": s = "Virgin Islands_US"; break;
            case "VN": s = "Vietnam"; break;
            case "VU": s = "Vanuatu"; break;
            case "WF": s = "Wallis_And_Futuna_Islands"; break;
            case "WS": s = "Samoa"; break;
            case "YE": s = "Yemen"; break;
            case "YT": s = "Mayotte"; break;
            case "ZA": s = "South_Africa"; break;
            case "ZM": s = "Zambia"; break;
            case "ZE": s = "ZE"; break;
        }
        return s;
    }
}
