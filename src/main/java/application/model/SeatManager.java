package application.model;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Objects;

public class SeatManager {
    private static GridPane grid = new GridPane(); //Layout
    private static AnchorPane pane = new AnchorPane();
    private static VBox seatBox = new VBox();
    private static Label label = new Label();      // Label
    private static Label showSeat = new Label();
    private static int height = 600;
    private static int width = 600;
    private static int pixel = 30;
    private static String returnSeat;
    private static int plat;
    private static Stage stage;
    private static Scene scene;

    public static String addSeatPlace() {
        stage = new Stage();
        grid.setGridLinesVisible(true);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle("Choose seat");
        scene = new Scene(seatBox, width, height);
        grid.setGridLinesVisible(true);
        FillingLayoutWithLabels(80);
        setupWindow();
        stage.setScene(scene);
        stage.show();

        return returnSeat;
    }

    private static HBox seatHbox;
    private static Label newSeat = new Label();
    private static void setupWindow() {
        Button btn = new Button("Add seat");
        Label seatTxt = new Label("Chosen seat: ");
        seatHbox = new HBox();
        seatHbox.getChildren().addAll(seatTxt, newSeat, btn);
        seatHbox.setPadding(new Insets(10));
        btn.setOnAction(event -> {
            stage.close();
        });
        pane.getChildren().addAll(btn);
        SeatManager.seatBox.getChildren().addAll(grid, pane);
    }


    private static void FillingLayoutWithLabels(int plats) {
        plat = plats;
        for(int i = 0;i< plats ;i++){
            for(int j = 0;j<plats/10 ; j++){
                addLabel(i,j);
            }
        }

    }

    public static void addLabel(int columnIndex, int rowIndex) {

        Label label = new Label();
        label.setMinWidth(pixel);
        label.setText(label.getId());
        label.setMinHeight(pixel);
        label.setBackground(new Background(new BackgroundFill(Color.rgb(223, 223, 222),
                new CornerRadii (5),
                Insets.EMPTY)));
        label.setBorder(new Border(new BorderStroke(Color.rgb(247, 245, 242), BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(1))));
        GridPane.setColumnIndex(label, columnIndex);
        GridPane.setRowIndex(label, rowIndex);
        label.setId(rowIndex+ " " + columnIndex);
        SeatManager.grid.getChildren().add(label);

        label.setOnMouseClicked((MouseClick) ->{
            System.out.println("hellooo");
            clickedHandle(label.getId());

            label.setBackground(new Background(new BackgroundFill(Color.rgb(255, 142, 0),
                    new CornerRadii (5),
                    Insets.EMPTY)));

            for (int i = 0; i < grid.getChildren().size(); i++){
                grid.getChildren().get(i).setOpacity(1);
                if (!Objects.equals(grid.getChildren().get(i).getId(), label.getId())){
                    grid.getChildren().get(i).setOpacity(0.2);
                }
            }
        });
    }

    private static void clickedHandle(String id) {
        returnSeat = id;
    }

}


