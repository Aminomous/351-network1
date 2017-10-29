package client;

//Thanadon Pakawatthippoyom 5810405037

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Modality;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientDataReceiver extends Thread {
    private ClientController controller;
    private Socket client;
    private DataInputStream in;
    private int port;
    private String serverName;

    public ClientDataReceiver(ClientController controller, Socket client, String serverName, int port) throws IOException {

        this.controller = controller;
        this.serverName = serverName;
        this.port = port;
        this.client = client;
        in = new DataInputStream(client.getInputStream());
    }

    public void run() {

        while (true) {
            try {
                String status = in.readUTF();

                System.out.println("server say" + status);

                String[] serverText = status.split(" ");
                //201 mean both player ready to play game
                if ("201".equals(serverText[0])) {
                    break;
                }
                //211 mean playerNumber receive from server
                if ("211".equals(serverText[0])) {
                    controller.setPlayerNumber(Integer.parseInt(serverText[2]));
                }
                //221 mean receive count down number
                if ("221".equals(serverText[0])) {
                    controller.setCountDown(Integer.parseInt(serverText[2]));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        while (true) {
            if (isInterrupted()) {
                this.interrupt();
                break;
            }
            try {

                String status = in.readUTF();

                System.out.println("server say : " + status);

                String[] serverTexts = status.split(" ");
                //30x mean server send about game

                //301 mean notes update
                if ("301".equals(serverTexts[0])) {
                    String[] notes = serverTexts[2].split(",");
                    controller.receiveNotes(notes);
                }

                // 302 mean score update
                if ("302".equals(serverTexts[0])) {

                    String[] information = serverTexts[2].split("_");
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            controller.setInformation(information);
                        }
                    });
                }
                // 352 mean server time
                if ("352".equals(serverTexts[0])) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            controller.setTime(serverTexts[2]);
                        }
                    });
                }
                // 400 mean end game
                if ("400".equals(serverTexts[0])) {
                    controller.sendEndGameInformation();
                }

                if ("401".equals(serverTexts[0])) {
                    String information = serverTexts[2];
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            controller.endGame(information);
                        }
                    });
                    break;

                }

            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Connection Error.");
                alert.setContentText("Cannot connect to server.");
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.showAndWait();
            }
        }

    }

    public void close() throws IOException {
        client.close();
    }
}
