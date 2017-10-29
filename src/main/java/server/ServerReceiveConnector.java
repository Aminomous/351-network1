package server;

//Thanadon Pakawatthippoyom 5810405037

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class ServerReceiveConnector extends Thread {

    private ServerSocket serverSocket;
    private Socket server;
    private ArrayList<ServerDataManager> gameController;

    public ServerReceiveConnector(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        serverSocket.setReuseAddress(true);
        gameController = new ArrayList<ServerDataManager>();
    }

    public void run() {
        while (true) {
            try {
                System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
                server = serverSocket.accept();

                DataOutputStream out = new DataOutputStream(server.getOutputStream());
                out.writeUTF("playerNumber 1");

                ServerDataManager s = new ServerDataManager(this, server);
                s.start();
                gameController.add(s);

                if (gameController.size() % 2 == 0) {
                    gameController.get(gameController.size() - 2).setSubClient(gameController.get(gameController.size() - 1).getMainClient());
                }

            } catch (SocketTimeoutException s) {
                System.out.println("Socket timed out!");
                break;

            } catch (SocketException e) {
                break;
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public void end() throws IOException {
        System.out.println(isInterrupted());
        if (isInterrupted()) {
            try {
                serverSocket.close();
            } catch (SocketException e) {

            }
            this.interrupt();
        }

        System.out.println(serverSocket.isClosed());
    }
}
