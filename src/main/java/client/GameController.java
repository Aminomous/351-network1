package client;

//Thanadon Pakawatthippoyom 5810405037

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.HashMap;

public class GameController {
    private ArrayList<String[]> notesList;
    @FXML
    private Canvas canvas;
    @FXML
    private Label playerNameLabel, scoreLabel1, scoreLabel2, timeLabel;
    @FXML
    private GridPane subPane;

    private int pointer;
    private int scores1, scores2;
    private HashMap<String, Color> colorMap;
    private ArrayList<Color> colors;
    private int playerNumber;

    public GameController() {
        notesList = new ArrayList<>();
        colorMap = new HashMap<>();
        colors = new ArrayList<>();

        colors.add(Color.CYAN);
        colors.add(Color.YELLOW);
        colors.add(Color.MAGENTA);
        colors.add(Color.BLACK);
        colors.add(Color.GREY);
        colors.add(Color.ORANGE);
        colors.add(Color.PINK);
        colors.add(Color.GREEN);
        colors.add(Color.LIGHTSTEELBLUE);
        colors.add(Color.SEASHELL);

    }

    public void initialize() {
        colorMap.clear();
        pointer = 0;
        setScores(playerNumber, 0);
        setScores(-1, 0);

        drawInstructionNotes(canvas.getGraphicsContext2D());
    }

    public void setNextNote(String[] notes) {
        this.notesList.add(0, notes);
        pointer = 0;
        draw();
    }

    private void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, canvas.getHeight() - 170, canvas.getWidth(), 200);
        for (int i = 0; i < Math.min(3, notesList.size()); i++) {
            String[] note = notesList.get(i);
            double width = 10;
            double height = canvas.getHeight() - (i * 40) - 41;
            drawNotes(gc, note, canvas.getWidth(), height, i);
            if (i == 0) {
                height += 10;
                double[] xPoints = {width, width + 20, width + 20, width + 40, width + 20, width + 20, width};
                double[] yPoints = {height, height, height - 10, height + 5, height + 20, height + 10, height + 10};
                int nPoints = 7;
                gc.setFill(Color.BLACK);
                gc.fillPolygon(xPoints, yPoints, nPoints);

                xPoints = new double[]{width + 50, canvas.getWidth() - width - 50, canvas.getWidth() - width - 50, width + 50};
                yPoints = new double[]{height - 15, height - 15, height + 25, height + 25};
                gc.strokePolygon(xPoints, yPoints, 4);
            }
        }
    }

    private void drawNotes(GraphicsContext gc, String[] notes, double width, double height, int line) {

        double xSize = 30;
        double ySize = 30;
        double xPos = width / 2 - 15;
        double yPos = height;
        int sep = (int) Math.ceil(notes.length / 2);
        if (notes.length % 2 == 0) {
            xPos += 20;
        }
        for (int i = 0; i < notes.length; i++) {
            double xTrans = -40;
            if (i <= sep) {
                xTrans *= (sep - i);
            } else {
                xTrans *= -(i - sep);
            }

            String direction = notes[i];
            if (line == 0 && i < pointer) {
                gc.setFill(Color.WHITE);
            } else {
                gc.setFill(colorMap.get(direction));
            }
            double[] xPoints = {xPos + xTrans, xPos + xSize + xTrans, xPos + xSize + xTrans, xPos + xTrans};
            double[] yPoints = {yPos, yPos, yPos + ySize, yPos + ySize};

            gc.fillPolygon(xPoints, yPoints, 4);

            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1);
            gc.strokePolygon(xPoints, yPoints, 4);
        }
    }

    private void drawInstructionNotes(GraphicsContext gc) {
        String[] direction = {"Up", "Down", "Left", "Right"};
        int counter = 0;
        while (counter < direction.length) {
            Color color = colors.get((int) (Math.random() * 100) % 10);
            if (colorMap.containsValue(color)) {
                continue;
            }
            colorMap.put(direction[counter], color);
            counter++;
        }
        Object[] keys = colorMap.keySet().toArray();
        double width = canvas.getWidth();
        double xSize = 30;
        double ySize = 30;

        for (int i = 0; i < keys.length; i++) {

            double xPos = width / 2 - 100 + i % 2 * 100;
            double yPos = 1 + Math.min(Math.max(0, i - 1), 1) * 40;

            double[] xPoints = {xPos, xPos + xSize, xPos + xSize, xPos};
            double[] yPoints = {yPos, yPos, yPos + ySize, yPos + ySize};

            gc.setFill(colorMap.get(keys[i]));
            gc.fillRect(xPos, yPos, 30, 30);
            gc.setFill(Color.BLACK);
            gc.fillText(String.valueOf(keys[i]), xPos + 35, yPos + 20);

            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1);
            gc.strokePolygon(xPoints, yPoints, 4);
        }

    }

    public boolean checkKeyPress(KeyCode key) {
        if (pointer < notesList.get(0).length && key.getName().equals(notesList.get(0)[pointer])) {
            pointer++;
            draw();
            return true;
        }
        return false;
    }

    public int getPointer() {
        return pointer;
    }

    public int getScores() {
        return scores1;
    }

    public void setScores(int playerNumber, int scores) {
        if (this.playerNumber == playerNumber) {
            this.scores1 += scores;
            scoreLabel1.setText("Your Scores: " + (this.scores1 + ""));
        } else {
            this.scores2 += scores;
            scoreLabel2.setText("Enemy Scores: " + (this.scores2 + ""));
        }
    }

    public void setPlayerNameLabel(String playerName) {
        this.playerNameLabel.setText("Player's name: " + (playerName.equals("") ? "Anonymous" : playerName));
    }

    public String getPlayerName() {
        String[] temp = playerNameLabel.getText().split(": ");
        return temp[1];
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public void setPlayerNumber(int playerNumber) {
        this.playerNumber = playerNumber;
    }

    public void setCountDown(int number) {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.clearRect(0, canvas.getHeight() / 2 - 50, canvas.getWidth(), canvas.getHeight() - 100);
        gc.setFill(Color.BLACK);
        gc.setFont(new Font(64));
        gc.fillText(number + "", canvas.getWidth() / 2 - 20, canvas.getHeight() / 2);
    }

    public void setTime(String time) {
        this.timeLabel.setText(time + "/30 second");
    }

    public void endGame(String status) {

        Alert announce = new Alert(Alert.AlertType.INFORMATION);
        announce.setHeaderText("RESULT");
        if (status.equals("draw")) {
            announce.setContentText("DRAW");
        } else if (status.equals(playerNumber + "")) {
            announce.setContentText("YOU WIN.");
        } else {
            announce.setContentText("YOU LOSE.");
        }
        announce.showAndWait();
        this.timeLabel.getScene().getWindow().hide();
    }
}
