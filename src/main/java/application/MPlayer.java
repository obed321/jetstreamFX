package application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;

public class MPlayer extends Application implements ChangeListener {

    @FXML
    private ChoiceBox alternatives;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private ChoiceBox rounds;
    @FXML
    private Label titleLabel;

    private File song;
    private ArrayList alternativesList;
    private MediaPlayer mediaPlayer;
    private boolean secondGame;
    private boolean waitGuess;
    private boolean newSong;
    private double maxRounds;
    private int correct;
    private double round;
    private Parent root;
    private Thread thread;


    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            //primaryStage.initModality(Modality.WINDOW_MODAL);
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("user/games/mplayer.fxml")));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Music Quiz");
            primaryStage.show();
            progressBar = (ProgressBar) root.lookup("#progressBar");
            initRounds();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startGame() throws InterruptedException {
        if (secondGame) {
            titleLabel.setText("Game already started.");
        } else {
            titleLabel.setText("Start guessing!");
            correct = 0;
            maxRounds = Integer.parseInt(rounds.getSelectionModel().getSelectedItem().toString());
            secondGame = true;
            game();
            Music music = new Music();
            thread = new Thread(music);
            thread.start();
        }
    }

    private void game() throws InterruptedException {
        if (round==maxRounds) {
            reset();
            progressBar.setProgress(0.0);
            round = 0;
            titleLabel.setText("You got: " + correct + "/" + (int) maxRounds + " correct");
            secondGame = false;
        }
        else if (!newSong) {
            System.out.println(round);
            nextSong(randomSong());
            progressBar.setProgress((1 / maxRounds) * round);
            System.out.println((1 / maxRounds) * round);
            round++;
        }
    }

    private void initRounds() {
        ArrayList roundsArray = new ArrayList();
        for (int i = 1; i < 11; i++) {
            roundsArray.add(i);
        }
        rounds = (ChoiceBox) root.lookup("#rounds");
        rounds.getItems().addAll(roundsArray);
        rounds.getSelectionModel().select(0);
    }

    private void reset() {
        mediaPlayer.stop();
        song = null;
        alternatives.getItems().removeAll(alternativesList);
    }

    public void fillAlternatives(File correct) {
        alternativesList = new ArrayList();
        alternativesList.add(correct.getName());
        for (int i = 0; i < 4; i++) {
            String randomSong = randomSong().getName();
            while (alternativesList.contains(randomSong)) {
                randomSong = randomSong().getName();
            }
            alternativesList.add(randomSong);
        }

        Collections.shuffle(alternativesList);

        alternatives.getItems().addAll(alternativesList);
    }

    public File randomSong() {
        File[] files = new File("music").listFiles();
        Random rand = new Random();
        File song = files[rand.nextInt(files.length)];
        return song;
    }

    public void nextSong(File currentSong) {
        song = currentSong;
        newSong = true;
        fillAlternatives(currentSong);
    }

    public void guess() throws InterruptedException {
        if (alternatives.getSelectionModel().getSelectedItem().equals(song.getName())) {
            titleLabel.setText("Correct answer!");
            correct++;
        } else {
            titleLabel.setText("Wrong answer!");
        }
        reset();
        game();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void changed(ObservableValue observableValue, Object o, Object t1) {

    }

    public class Music implements Runnable {
        @Override
        public void run() {
            while(secondGame) {
                if (newSong) {
                    System.out.println("jieiowre");
                    if (song != null) {
                        System.out.println(song.getName());
                        Media media = new Media(song.toURI().toString());
                        mediaPlayer = new MediaPlayer(media);
                        mediaPlayer.play();
                        newSong = false;
                    }
                }
                try {
                    thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}