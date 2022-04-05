package application;

import application.model.Flight;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.FileNotFoundException;
import java.util.ArrayList;


public class NewScene {

    public static ArrayList<Flight> showNewScene(String title, ArrayList<Flight> flights) throws FileNotFoundException {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(title);
        stage.setMinWidth(400);

        ArrayList<Flight> output = new ArrayList<>();
        ArrayList<Flight> compare = new ArrayList<>();
        ListView<String> listv = new ListView<>();
        VBox vbox = new VBox(10);

        if (flights != null) {
            StringBuilder info = new StringBuilder();
            for (int i = 0; i < flights.size(); i++){
                HBox hbox = new HBox(1);
                hbox.setPadding(new Insets(20));
                //hbox.setBorder(new Border(new BorderStroke(Color.PINK,BorderStrokeStyle.DASHED,null,null)));
                hbox.setEffect(new DropShadow(2.0, Color.BLACK));
                hbox.setBackground(new Background(new BackgroundFill(Color.rgb(210,210,210),
                        CornerRadii.EMPTY,
                        Insets.EMPTY)));
                //Label nr = new Label();
                hbox.setSpacing(10);
                //nr.setText(i + ". ");

                Image img = new Image("/application/image/jetStream.png");

                ImageView image = new ImageView(img);
                image.setFitWidth(40);
                image.setFitHeight(50);

                Label titleF = new Label();
                titleF.setText(flights.get(i).getFrom());

                Label titleD = new Label();
                titleD.setText(flights.get(i).getDestination());
                Label date = new Label();
                date.setText(flights.get(i).getDate());

                Button btn = new Button("Select");
                btn.setStyle("-fx-background-color: #eee; -fx-text-fill: #333; -fx-padding: 20px 35");
                int finalI = i;
                btn.setOnAction(l -> {
                    compare.add(new Flight(flights.get(finalI).getFrom(), flights.get(finalI).getDestination(), flights.get(finalI).getDate(), flights.get(finalI).getTime()));
                    stage.close();
                });
                hbox.setAlignment(Pos.CENTER_LEFT);
                hbox.getChildren().addAll(image,titleF, titleD,date,btn);
                vbox.getChildren().addAll(hbox);
                vbox.setMaxSize(500, 400);
                //listv.getItems().
                //listv.getItems().add(i + ". " +
                // resor.get(i).getFrom() + ", " + resor.get(i).getDistination() + ", " + resor.get(i).getDate());
                //output.add(resor.get(i));



            }
            Label list = new Label();
            list.setText(info.toString());
            Button closeBtn = new Button("Choose Flight");
            closeBtn.setOnAction(e -> stage.close());

            listv.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    String selected = listv.getSelectionModel().getSelectedItem();
                    System.out.println(selected + " selected trip");
                    if (selected.contains(".")){
                        String[] tmp = selected.split("\\.");
                        System.out.println(tmp[0]);
                        String tt = tmp[0];
                        int index = Integer.parseInt(tt); // indexen
                        System.out.println(index + "indexen");
                        compare.add(output.get(index));
                    }
                }
            });

            // sil == fitta på somaliska

            //VBox layout = new VBox(10);
            //layout.getChildren().addAll(listv, closeBtn);
            //layout.setAlignment(Pos.CENTER);
            vbox.setAlignment(Pos.TOP_CENTER);
            vbox.setMinWidth(400);
            Scene scene = new Scene(vbox, Color.rgb(1,1,2));
            stage.setScene(scene);
            stage.showAndWait();
        }
        return compare;
    }
}
