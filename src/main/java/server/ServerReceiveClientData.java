package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class ServerReceiveClientData extends Thread {
    private Socket client;
    private ServerDataManager sender;

    public ServerReceiveClientData(ServerDataManager manager, Socket client) {
        this.client = client;
        this.sender = manager;
    }

    public void run() {
        DataInputStream in = null;
        DataOutputStream out = null;
        try {
            in = new DataInputStream(client.getInputStream());
            out = new DataOutputStream(client.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                String status = in.readUTF();

                System.out.println("client say : " + status);

                String[] clientText = status.split(" ");
                if (clientText[0].equals("score")) {
                    sender.scoreCalculateAndUpdate(Integer.parseInt(clientText[1]), Integer.parseInt(clientText[2]), out);
                }
                if (clientText[0].equals("End")) {
                    String[] information = clientText[1].split("_");
                    sender.setScore(Integer.parseInt(information[0]), Integer.parseInt(information[1]));
                    break;
                }
            } catch(SocketException e) {

            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
