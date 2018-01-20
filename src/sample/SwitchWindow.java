package sample;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class SwitchWindow {

    public static Invite controllerInvite;

    public static void incomingCall(String desc, boolean type){
        setInteface("invite");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                SwitchWindow.controllerInvite.descCaller.setText(desc);
                SwitchWindow.controllerInvite.type(type);
            }
        });

    }
    public static void goBack(){
        setInteface("home");
    }
    private static void setInteface(String nameFile){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                try {

                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource(
                                    nameFile + ".fxml"
                            )
                    );

                    Pane pane = (Pane) loader.load();
                    if(nameFile.equals("invite"))
                        SwitchWindow.controllerInvite =
                                loader.<Invite>getController();


                    Main.stage.setScene(new Scene(pane, 546, 234));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
