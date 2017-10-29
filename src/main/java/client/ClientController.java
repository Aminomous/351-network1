package client;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientController {
    private ClientDataReceiver receiver;
    private GameController gameMain;
    private String serverName;
    private int port;
    private DataOutputStream out;
    private Socket client;
    @FXML
    private TextField nameField, ipField;

    public ClientController() {
        port = 4321;
    }

    public void initialize() {
    }

    private void connect() throws IOException {
        serverName = this.ipField.getText().equals("")?"localhost":ipField.getText();
        client = new Socket(serverName, port);
        receiver = new ClientDataReceiver(this, client, serverName, port);
        out = new DataOutputStream(client.getOutputStream());
    }

    public void play() throws IOException {
        connect();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/GameScreen.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene((Parent) loader.load());
            scene.setOnKeyPressed(new EventHandler<KeyEvent>() {

                public void handle(KeyEvent event) {
                    try {
                        sendKeyPressed(event);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            gameMain = loader.getController();
            gameMain.setPlayerNameLabel(this.nameField.getText());

            receiver.start();
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }

        receiver.interrupt();
    }

    public void sendKeyPressed(KeyEvent key) throws IOException {
        boolean status = gameMain.checkKeyPress(key.getCode());
        if (status) {
            out.writeUTF("score " + gameMain.getPlayerNumber() + " " + (gameMain.getPointer() - 1));
        }
    }

    public void receiveNotes(String[] notes) {
        gameMain.setNextNote(notes);
    }

    public void sendEndGameInformation() throws IOException {
        out.writeUTF("End " + gameMain.getPlayerNumber() + "_" + gameMain.getScores());
    }

    public void setInformation(String[] information) {
            gameMain.setScores(Integer.parseInt(information[0]), Integer.parseInt(information[1]));
    }

    public void setPlayerNumber(int number) {
        gameMain.setPlayerNumber(number);
    }

    public void setCountDown(int number) {
        gameMain.setCountDown(number);
    }

    public void setTime(String time) {
        gameMain.setTime(time);
    }

    public void endGame(String status){
        gameMain.endGame(status);
    }

}
