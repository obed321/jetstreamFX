package application;

import application.model.*;
import application.moveScreen.MoveScreen;
import application.databaseSQL.Db;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import worldMap.World;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class Controller {

    // Default variables
    private CreateWorld createWorld;
    private World world;
    private Stage stage;
    private Scene scene;
    private Parent root;
    private User user;

    // FXML variables
    @FXML private ButtonBar logout;
    @FXML private TextField login_pass;
    @FXML private ScrollPane scrollPane;
    @FXML private TextField login_email;
    @FXML private Label error;
    @FXML private Label registration_error;
    @FXML private Label success_msg;
    @FXML private Label u_name, u_id;
    @FXML private Label chosen_seat;
    @FXML private VBox display_flight;
    @FXML private Button menuButton1;
    @FXML private Button menuButton2;
    @FXML private TextField search_input;
    @FXML private Label search_matching;
    // From game
    @FXML private StackPane game1;
    @FXML private StackPane game2;
    @FXML private Button quizButton;

    // From registration page
    @FXML private TextField name, lname, adress, email, number, password;
    public String[] args;


    //////////   Home   ///////////
    // the method will switch the user to the Home page
    public void switchToHome(ActionEvent e) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Home.fxml")));
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        //stage.initStyle(StageStyle.TRANSPARENT);
        //MoveScreen.moveScreen(root,stage);
        stage.setTitle("Home");
        stage.setScene(scene);
        stage.show();
    }

    //////////   Play games   ///////////
    public void switchToGames (ActionEvent e) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("user/games/Games.fxml")));
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle("JetStream | Games");
        stage.setScene(scene);
        stage.show();

        menuButton1 = (Button) root.lookup("#menuButton1");
        quizButton = (Button) root.lookup("#quizButton");
        game1 = (StackPane) root.lookup("#game1");
        game2 = (StackPane) root.lookup("#game2");
        ImageView imageView = new ImageView(new Image("application/gamePosters/MusicQuiz.png"));
        ImageView imageView2 = new ImageView(new Image("application/gamePosters/PONG.png"));
        game1.getChildren().add(imageView);
        game2.getChildren().add(imageView2);    }
    public void playPong(){}
    public void playQuiz(){
        MPlayer mPlayer = new MPlayer();
        try {
           Stage primary = new Stage();
            mPlayer.start(primary);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    //////////   navigate to admin pages   ///////////
    public void switchToDashboard(ActionEvent e) throws IOException {
        if (!login_pass.getText().isEmpty() && !login_email.getText().isEmpty()) {
            User user = Db.authenticationUser(login_email.getText(), login_pass.getText());
            if (user != null) {
                renderDashboard(e, user);
            } else {
                error.setText("Wrong email or pass!");
            }
        } else {
            renderDashboard(e, user);
        }
    } // the method will switch the user to the dashboard page
    public void renderDashboard(ActionEvent e, User user) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("user/Dashboard.fxml")));
        this.user = user;
        display_flight = (VBox) root.lookup("#display_flight");
        scrollPane = (ScrollPane) root.lookup("#scrollPane");
        chosen_seat = (Label) root.lookup("#chosen_seat");
        menuButton2 = (Button) root.lookup("#menuButton2");
        search_input = (TextField) root.lookup("#search_input");
        search_matching = (Label) root.lookup("#search_matching");
        createWorld = new CreateWorld();
        world = createWorld.init(this);


        scrollPane.setContent(new StackPane(world));
        scrollPane.setBackground(new Background(new BackgroundFill(world.getBackgroundColor(), CornerRadii.EMPTY, Insets.EMPTY)));
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        System.out.println(user.getName() + ", " + user.getId());
        u_name = (Label) root.lookup("#u_name");
        u_id = (Label) root.lookup("#u_id");
        u_name.setText(user.getName());
        u_id.setText(user.getId());
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        //MoveScreen.moveScreen(root,stage);
        stage.setTitle("JetStream | Dashboard");
        stage.setScene(scene);
        stage.show();
    } // the method will render dashboard page for user
    public void noLoginRequired(ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("user/Dashboard.fxml")));
        display_flight = (VBox) root.lookup("#display_flight");
        scrollPane = (ScrollPane) root.lookup("#scrollPane");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        chosen_seat = (Label) root.lookup("#chosen_seat");
        createWorld = new CreateWorld();
        world = createWorld.init(this);

        scrollPane.setContent(new StackPane(world));
        scrollPane.setBackground(new Background(new BackgroundFill(world.getBackgroundColor(), CornerRadii.EMPTY, Insets.EMPTY)));

        u_name = (Label) root.lookup("#u_name");
        u_id = (Label) root.lookup("#u_id");
        u_name.setText(null);
        u_id.setText(null);
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        MoveScreen.moveScreen(root,stage);
        stage.setTitle("Test dashboard window");
        stage.setScene(scene);
        stage.show();
    }// shortcut login to user dashboard
    public void switchToChecking(ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("user/Checking.fxml")));
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle("Checking window");
        stage.setScene(scene);
        stage.show();
    }// the method will switch the user to the checking page
    public void switchToRegistration(ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("user/Registration.fxml")));
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle("Registration window");
        stage.setScene(scene);
        stage.show();
    }// the method will switch the user to the registration page
    public void registeruser(ActionEvent e) throws SQLException, IOException {
        user = new User(null, name.getText(), lname.getText(), adress.getText(), email.getText(), number.getText(), password.getText(), false);
        System.out.println(user.getName() + "fsdfsdfsdf");
        boolean ok = Db.saveUser(user);
        if (ok) {
            renderLoginPage(e, "successfully registered the user!");
        } else {
            registration_error.setText("Couldn't register the information");
        }
    }// the method will register the user and return to the login page
    public void switchToLogin(ActionEvent e) throws IOException {
        renderLoginPage(e, null);
    }// the method will switch the user to the login page
    public void renderLoginPage(ActionEvent e, String msg) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("user/Login.fxml")));
        success_msg = (Label) root.lookup("#success_msg");
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle("Login window");
        stage.setScene(scene);
        success_msg.setText(msg);
        stage.show();
    }// render login page
    public void chooseSeat() {
        String chosenSeat = SeatManager.addSeatPlace();
        if (chosenSeat != null) {
            chosen_seat.setText(chosenSeat);
        }
    }// the method will show the chosen sit on the screen



    //////////   flight lists dashboard   ///////////
    public void fillFlights (ArrayList <Flight> flights) {
        display_flight.getChildren().clear();
        Stage infoStage = new Stage();
        AtomicBoolean openedStage = new AtomicBoolean(false);
        ArrayList<Flight> compare = new ArrayList<>();

        for (int i = 0; i < flights.size();i++){

            StackPane stackholer = new StackPane();
            HBox hbox = new HBox(1);
            HBox hboxChildCenter = new HBox(1);
            HBox hboxChildRight = new HBox(1);

            Image img = new Image("/application/image/jetStream.png");
            ImageView image = new ImageView(img);
            image.setFitWidth(30);
            image.setFitHeight(40);

            // flight icons
            Image onboard = new Image("/application/image/onboard.png");
            ImageView onboardIcon = new ImageView(onboard);
            onboardIcon.setFitWidth(30);
            onboardIcon.setOpacity(0.5);
            onboardIcon.setFitHeight(30);

            Image path = new Image("/application/image/path.png");
            ImageView pathIcon = new ImageView(path);
            pathIcon.setFitWidth(70);
            pathIcon.setFitHeight(30);
            pathIcon.setStyle("-fx-margin: 0 40 0 40");

            Image landing = new Image("/application/image/landing.png");
            ImageView landingIcon = new ImageView(landing);
            landingIcon.setOpacity(0.5);
            landingIcon.setFitWidth(25);
            landingIcon.setFitHeight(25);

            Label titleF = new Label();
            titleF.setMaxSize(50,40);

            titleF.setText(flights.get(i).getFrom());

            Text depTime = new Text();
            String tmp = flights.get(i).getTime();
            String[] sorted = tmp.split(":");
            String s = sorted[0] + ": " + sorted[1];
            depTime.setText(s);
            depTime.setStyle("-fx-font-weight: bold");
            Text desTime = new Text();
            desTime.setText(s); // calculate arriving time
            desTime.setStyle("-fx-font-weight: bold");


            Label titleD = new Label();
            titleD.setMaxSize(50,40);
            titleD.setText(flights.get(i).getDestination());

            Label date = new Label();
            date.setText(flights.get(i).getDate());
            // box holderx
            VBox boardingBox = new VBox();
            boardingBox.setAlignment(Pos.CENTER_LEFT);
            boardingBox.getChildren().addAll(onboardIcon, depTime, titleF);
            // box holder
            VBox landingBox = new VBox();
            landingBox.setAlignment(Pos.CENTER_LEFT);
            landingBox.getChildren().addAll(landingIcon,desTime, titleD);

            Button btn = new Button("Köpa");
            btn.setStyle("-fx-background-color:  #ff8000; -fx-text-fill: #333; -fx-padding: 10 25; ");
            int finalI1 = i;
            btn.setOnAction(e -> {
                chooseSeat();
                //valdeRese.getChildren().clear();
                //valdeRese.getChildren().add(display_filght.getChildren().get(finalI1));
                display_flight.getChildren().get(finalI1).setOpacity(0.8);
                for (int m = 0; m < display_flight.getChildren().size(); m++){
                    if (display_flight.getChildren().get(m) != display_flight.getChildren().get(finalI1)) {
                        display_flight.getChildren().get(m).setOpacity(1);
                    }
                }
                compare.add(flights.get(finalI1));

                /*HBox ls = new HBox();
                ls.getChildren().add(display_filght.getChildren().get(finalI1));
                hbox.setBackground(new Background(new BackgroundFill(Color.rgb(133, 200, 138),
                        CornerRadii.EMPTY,
                        Insets.EMPTY)));
                hbox.setAlignment(Pos.TOP_CENTER);
                display_filght.getChildren().clear();
                display_filght.getChildren().add(ls);*/
            });

            hboxChildCenter.getChildren().addAll(boardingBox, pathIcon, landingBox);
            hboxChildCenter.setSpacing(15);
            hboxChildCenter.setAlignment(Pos.CENTER_LEFT);

            hboxChildRight.getChildren().add(btn);
            hboxChildRight.setAlignment(Pos.CENTER_RIGHT);

            /***************  main box to hold the list  *********************/
            hbox.setBackground(new Background(new BackgroundFill(Color.rgb(247, 245, 242), CornerRadii.EMPTY, Insets.EMPTY)));
            hbox.getChildren().addAll(hboxChildCenter, hboxChildRight);
            hbox.setHgrow(hboxChildCenter, Priority.ALWAYS);
            hbox.setPadding(new Insets(5));
            hbox.setEffect(new DropShadow(2.0, Color.BLACK));
            hbox.setAlignment(Pos.TOP_LEFT);
            hbox.setSpacing(30);

            stackholer.getChildren().add(hbox);
            stackholer.setAlignment(Pos.TOP_LEFT);
            display_flight.getChildren().addAll(stackholer); // the box
            display_flight.setAlignment(Pos.TOP_LEFT);

            hbox.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
                if (!openedStage.get()){
                    infoStage.show();
                    openedStage.set(true);
                }else if(openedStage.get()){
                    infoStage.close();
                    openedStage.set(false);
                }
                hbox.setBackground(new Background(new BackgroundFill(Color.rgb(223, 223, 222), CornerRadii.EMPTY, Insets.EMPTY)));

            });
            hbox.addEventFilter(MouseEvent.MOUSE_EXITED, e -> {
                hbox.setBackground(new Background(new BackgroundFill(Color.rgb(247, 245, 242), CornerRadii.EMPTY, Insets.EMPTY)));
            });


        }
    } // the method will show the flights list on the right side of the dashboard when a user choose a country



    //////////   navigate to admin pages   ///////////
    public void switchToviewFlights(ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("admin/FlightsView.fxml")));
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle("FlightsView");
        stage.setScene(scene);
        stage.show();
    }
    public void switchToAdminView(ActionEvent e) {

        if (!login_pass.getText().isEmpty() && !login_email.getText().isEmpty()) {
            try {
                User user = Db.authenticationAdmin(login_email.getText(), login_pass.getText());
                if (user != null) {
                    root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("admin/AdminView.fxml")));
                    stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                    scene = new Scene(root);
                    stage.setTitle("Admin window");
                    stage.setScene(scene);
                    stage.show();
                } else {
                    error.setText("Wrong email or pass!");
                }
            }catch (IOException io){
                io.printStackTrace();
            }
        } else {
            error.setText("Fill the field!");
        }
    }
    public void switchToMembersView(ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("admin/MemberView.fxml")));
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle("Members window");
        stage.setScene(scene);
        stage.show();
    }
    public void switchToBookedFligthsView(ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("admin/BookedFlightsView.fxml")));
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle("Checking window");
        stage.setScene(scene);
        stage.show();
    }
    public void logout(ActionEvent event) {
        Alert alert2 = new Alert(Alert.AlertType.CONFIRMATION);
        alert2.setTitle("Logout");
        alert2.setHeaderText("You are about to logout!");
        alert2.setContentText("Do you really want to logout?");

        if (alert2.showAndWait().get() == ButtonType.OK) {
            stage = (Stage) scrollPane.getScene().getWindow();
            System.out.println("You have successfully logged out!");
            stage.close();
        }

    }



    //////////   getters and setters   ///////////
    public Stage getStage() {
        return stage;
    }
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    public Scene getScene() {
        return scene;
    }
    public void setScene(Scene scene) {
        this.scene = scene;
    }




    //////  DEV TEST  ///////
    @FXML private Button iconProfile, iconFlight, iconHistorik, iconGame, iconSuport;
    @FXML private AnchorPane pnlProfile, pnlHistorik, pnlFlight, pnlGame, pnlSupport;
    public void testDev(ActionEvent e){
        System.out.println(e.getSource());
        if (e.getSource() == iconProfile) {
            pnlProfile.toFront();
        }
        else if (e.getSource() == iconFlight) {
            pnlFlight.toFront();
        }
        else if (e.getSource() == iconHistorik) {
            pnlHistorik.toFront();
        }
        else if (e.getSource() == iconGame) {
            pnlGame.toFront();
        }
        else if(e.getSource() == iconSuport){
            pnlSupport.toFront();
        }

    }


    //////  SEARCH FIELD  ///////
    public void searchHit(){
        System.out.println("helllo");
        search_input.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        search_matching.setText("Search for (" + search_input.getText() + ") not found :(");
        System.out.println(" search clicked");
    }



    //////  MENU  /////// none used methods
    public void openMenu() {
        logout.setLayoutX(0); // this will move in menu from outside the window
        System.out.println("Menu opened");
    } // the method will open the menu once the user clicked on his profile
    public void closeMenu() {
        logout.setLayoutX(-84); // this will move out the menu outside the window
        System.out.println("Menu closed");
    } // the method will close the menu
    public void exit(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("You are about to Exit!");
        alert.setContentText("Do you really want to Exit?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            stage = (Stage) scrollPane.getScene().getWindow();
            System.out.println("You have successfully exited!");
            stage.close();
        }
    } // the method will close a scene
    public void openProfile() throws FileNotFoundException {
        int user_id = Integer.parseInt(u_id.getText());
        User user = Db.getUserWithID(user_id);
        NewScene.showNewScene(user.getName() + "'s Profile", null);
    } // the method will open the profile
    public void exitProgram(){
        System.exit(0);
    } // to determinate the program
}
