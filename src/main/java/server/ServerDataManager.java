package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class ServerDataManager extends Thread {
    private ServerReceiveConnector sender;
    private Socket mainClient;
    private Socket subClient;
    private int[] leader;
    private DataOutputStream outMain;
    private DataOutputStream outSub;
    private int score1[];
    private int score2[];

    public ServerDataManager(ServerReceiveConnector server, Socket client) {
        sender = server;
        mainClient = client;
        subClient = null;
        leader = new int[2];
        score1 = new int[]{-1, -1};
        score2 = new int[]{-1, -1};
    }

    @Override
    public void run() {
        DataInputStream inMain = null;

        DataInputStream inSub = null;

        try {
            inMain = new DataInputStream(mainClient.getInputStream());
            outMain = new DataOutputStream(mainClient.getOutputStream());

            if (subClient == null) {
                sendStatus("211 playerNumber 1", outMain);
                outMain.writeUTF("Wait other player to join");
                while (subClient == null) {
                    TimeUnit.SECONDS.sleep(1);
                }
                inSub = new DataInputStream(subClient.getInputStream());
                outSub = new DataOutputStream(subClient.getOutputStream());
                sendStatus("211 playerNumber 2", outSub);
            }
            Timer timer = new Timer();

            TimerTask taskAddNotes = new TimerTask() {
                private int time = 0;
                @Override
                public void run() {
                    if (9 == time){
                        this.cancel();
                    }
                    int number = ((int) (Math.random() * 100)) % 7 + 1;
                    String notes = randomNotes(number);
                    try {
                        String status = "301 Game " + notes;
                        sendStatus(status);
                        leader[0] = 0;
                        leader[1] = -1;

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    time++;
                }
            };
            TimerTask taskGameStartTimer = new TimerTask() {
                private int countDown = 5;

                @Override
                public void run() {
                    try {
                        String status = "221 CountDown " + countDown;
                        sendStatus(status);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    countDown--;
                    if (countDown == -1) {
                        String status = "201 Game readyToPlay";
                        try {
                            sendStatus(status);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        this.cancel();
                    }
                }
            };
            TimerTask serverSessionTime = new TimerTask() {
                private int time = 0;

                @Override
                public void run() {
                    try {
                        if (time == 31){
                            sendStatus("400 Game end");
                            this.cancel();
                        }else {
                            String status = "352 Time " + time;
                            sendStatus(status);
                            time++;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            timer.scheduleAtFixedRate(taskGameStartTimer, 0, 1000);
            timer.scheduleAtFixedRate(taskAddNotes, 5000, 3000);
            timer.scheduleAtFixedRate(serverSessionTime, 5000, 1000);

            ServerReceiveClientData clientA = new ServerReceiveClientData(this, mainClient);
            ServerReceiveClientData clientB = new ServerReceiveClientData(this, subClient);
            clientA.start();
            clientB.start();
            while ((score1[0] == -1) || (score2[0] == -1)){
                sleep(1000);
            }

            announceWinner();
            mainClient.close();
            subClient.close();
            this.interrupt();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void sendStatus(String status) throws IOException {
        outMain.flush();
        outSub.flush();
        outMain.writeUTF(status);
        outSub.writeUTF(status);
    }

    public void sendStatus(String status, DataOutputStream out) throws IOException {
        out.flush();
        out.writeUTF(status);
    }

    public void scoreCalculateAndUpdate(int playerNumber, int pointer, DataOutputStream out) throws IOException {
        if (leader[1] < pointer) {
            leader[0] = playerNumber;
            leader[1] = pointer;
        }
        String status;
        if (playerNumber == leader[0]) {
            status = "302 Game " + playerNumber + "_100";
        } else {
            status = "302 Game " + playerNumber + "_50";
        }
        sendStatus(status);
    }


    public String randomNotes(int length) {
        HashMap<Integer, String> temp = new HashMap<Integer, String>();
        temp.put(0, "Up");
        temp.put(1, "Left");
        temp.put(2, "Down");
        temp.put(3, "Right");
        String[] notes = new String[length];
        for (int i = 0; i < length; i++) {
            int number = ((int) (Math.random() * 100)) % 4;
            notes[i] = temp.get(number);
        }
        return String.join(",", notes);
    }

    public void setSubClient(Socket subClient) {
        this.subClient = subClient;
    }

    public Socket getMainClient() {
        return mainClient;
    }

    public void setScore(int playerNumber, int score){
        System.out.println("player Number: " + playerNumber + " score: " + score);
        if (playerNumber == 1){
            score1[0] = playerNumber;
            score1[1] = score;
        }else if (playerNumber == 2){
            score2[0] = playerNumber;
            score2[1] = score;
        }
    }
    public void announceWinner(){
        String status = "401 End ";
        if (score1[1] > score2[1]){
            status += score1[0];
        }else if (score1[1] == score2[1]){
            status += "draw";
        }else if (score1[1] < score2[1]){
            status += score2[0];
        }
        try {
            sendStatus(status);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
