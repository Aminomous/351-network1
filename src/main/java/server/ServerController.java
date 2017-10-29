package server;

//Thanadon Pakawatthippoyom 5810405037

import java.io.IOException;

public class ServerController extends Thread {
    private ServerReceiveConnector server;
    private int port;

    public ServerController() {

    }

    public void startServer() {
        port = 4321;
        try {
            server = new ServerReceiveConnector(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.start();

    }

    public void closeServer() {

        try {
            server.interrupt();
            server.end();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

