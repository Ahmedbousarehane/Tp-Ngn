package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;

import javax.print.attribute.standard.Media;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class Invite implements Initializable{

    @FXML
    public Label descCaller;
    @FXML
    public Label timer;
    @FXML
    private Button btnDecline;
    @FXML
    private Button btnAccept;

    public static Invite instance;
    public static Boolean isAccepted = null;
    public static Sip sip;
    public LocalTime duree = null;
    private MediaPlayer mediaPlayer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void onAccept(ActionEvent actionEvent) {
        isAccepted = true;
        duree = LocalTime.of(0,0,0);
        btnAccept.setVisible(false);
        btnDecline.setVisible(true);
        instance = this;
        mediaPlayer.stop();
        new Thread(new Runnable(){

            @Override
            public void run() {
                timer.setVisible(true);
                while (isAccepted){
                    try {
                        duree = duree.plusSeconds(1);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                instance.timer.setText(duree.format(DateTimeFormatter.ISO_LOCAL_TIME));
                            }
                        });
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    public void onDecline(ActionEvent actionEvent) {
        isAccepted = false;
        mediaPlayer.stop();
        sip.onBye();

    }
    public void type(boolean isCaller){
        javafx.scene.media.Media sound;
        if(isCaller){
            btnAccept.setVisible(false);
            btnDecline.setVisible(true);
            sound = new javafx.scene.media.Media(new File("caller.mp3").toURI().toString());
        }
        else {
            btnAccept.setVisible(true);
            btnDecline.setVisible(false);
            sound = new javafx.scene.media.Media(new File("ring.mp3").toURI().toString());


        }
        mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }
}
