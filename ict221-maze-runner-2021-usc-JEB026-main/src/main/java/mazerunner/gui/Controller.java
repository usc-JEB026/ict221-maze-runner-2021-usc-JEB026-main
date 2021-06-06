package mazerunner.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import mazerunner.engine.GameEngine;
import java.io.*;
import java.io.IOException;

import javafx.scene.image.Image ;

public class Controller {
    @FXML
    private Button startBtn;
    @FXML
    private Button upBtn;
    @FXML
    private Button downBtn;
    @FXML
    private Button leftBtn;
    @FXML
    private Button rightBtn;
    @FXML
    private TextField txtBox;
    @FXML
    private TextArea txtArea;
    @FXML
    private GridPane grid;
    @FXML
    private Label helpLabel;

    private GameEngine engine;
    private Image player;
    private Image apple;
    private Image trap;
    private Image gold;
    private Image cell;
    private Image exit;

    private static final double ELEMENT_SIZE = 50;

    public Controller() {
        grid =new GridPane();
        player = new Image ("mazerunner/gui/img/player.bmp");
        apple = new Image ("mazerunner/gui/img/apple.bmp");
        trap = new Image ("mazerunner/gui/img/trap.bmp");
        gold = new Image ("mazerunner/gui/img/gold.bmp");
        cell = new Image ("mazerunner/gui/img/cell.bmp");
        exit = new Image ("mazerunner/gui/img/exit.bmp");
    }

    public boolean readDifficulty() throws IOException {
        int choice = 5;
        boolean result = false;
            try{
                choice=Integer.parseInt(txtBox.getText());
                if (choice>10 || choice<1){
                    txtArea.setText("Select a difficulty from 1 - 10. Default 5.");
                }
                else {
                    txtArea.setText("Difficulty level: "+choice);
                    result = true;
                }
            }
            catch(Exception e){
                txtArea.setText("Please enter an Integer");
            }
        engine.setD(choice);
        return result;
    }

    public void printMap(){
        grid.getChildren().clear();
        for (int row = 0; row < engine.getSize(); row++) {
            for (int col = 0; col < engine.getSize(); col++) {
                if (engine.getP().getLocation()[0]==row && engine.getP().getLocation()[1]==col){//represent player location
                    grid.add(createCell(player),col,row);
                }
                else {
                    grid.add(createCell(player),col,row);
                    switch (engine.getMap()[row][col].getCellType()){//represent everything else by cell type
                        case "_":
                            grid.add(createCell(cell),col,row);
                            break;
                        case "G":
                            grid.add(createCell(gold),col,row);
                            break;
                        case "T":
                            grid.add(createCell(trap),col,row);
                            break;
                        case "A":
                            grid.add(createCell(apple),col,row);
                            break;
                        case "E":
                            grid.add(createCell(exit),col,row);
                            break;
                    }
                }
            }
        }
        txtArea.appendText("\nStamina: " + engine.getP().getStamina());
        txtArea.appendText("\nGold coins: " + engine.getP().getGoldCoins());
    }

    public VBox createCell(Image image) {

        ImageView imageView = new ImageView();
        try {
            imageView.setImage(image);
            imageView.setFitWidth(ELEMENT_SIZE);
            imageView.setFitHeight(ELEMENT_SIZE);
            imageView.setSmooth(true);
            imageView.setCache(true);
        } catch (Exception ex) {

        }
        VBox pageBox = new VBox();
        pageBox.getChildren().add(imageView);
        pageBox.setStyle("-fx-border-color: orange;");
        imageView = null;
        return pageBox;
    }

    public void movePlayer(int direction){
        if (!engine.getFinish()){
            int newRow=engine.getP().getLocation()[0];
            int newCol=engine.getP().getLocation()[1];
            switch (direction) {
                case 1://Up
                    newRow--;
                    break;
                case 2://Down
                    newRow ++;
                    break;
                case 3://Left
                    newCol--;
                    break;
                case 4://Right
                    newCol++;
                    break;
            }
            if (newRow>9 || newCol<0 || newCol>9 || newRow<0){
                txtArea.appendText("\nThe attempted move is invalid!");
            }
            else {
                engine.getP().setStamina(engine.getP().getStamina()-1); //reduce stamina
                if (engine.getP().getStamina()==0){
                    txtArea.appendText("\nNot enough stamina to move");
                    engine.setFinish(true);
                    engine.setGameState("Lost!");
                }
                else{
                    String newCellType = engine.getMap()[newRow][newCol].getCellType();//The Type of Cell player will be moved to
                    switch (newCellType){
                        case "E":
                            engine.setFinish(true);
                            engine.setGameState("Won!");
                            break;
                        case "T":
                            engine.getP().setGoldCoins(engine.getP().getGoldCoins()-1);
                            if (engine.getP().getGoldCoins()==0){
                                engine.setFinish(true);
                                engine.setGameState("Lost!");
                            }
                            break;
                        case "G":
                            engine.getP().setGoldCoins(engine.getP().getGoldCoins()+1);
                            engine.getMap()[newRow][newCol].setCellType("_");//Change new cell to _ when Gold consumed
                            break;
                        case "A":
                            engine.getP().setStamina(engine.getP().getStamina()+3);
                            engine.getMap()[newRow][newCol].setCellType("_");//Change new cell to _ when Apple consumed
                            break;
                    }
                    engine.getP().setLocation(new int[]{newRow, newCol});
                    printMap();
                }
            }
        }
        if (engine.getFinish()) txtArea.appendText("\nGame over, result: " + engine.getGameState());
    }

    public void moveUp(ActionEvent actionEvent) {
        movePlayer(1);
    }

    public void moveDown(ActionEvent actionEvent) {
        movePlayer(2);
    }

    public void moveLeft(ActionEvent actionEvent) {
        movePlayer(3);
    }

    public void moveRight(ActionEvent actionEvent) {
        movePlayer(4);
    }

    public void startGame(ActionEvent actionEvent) throws IOException {  //start a new game
        engine = new GameEngine(10);
        if (readDifficulty()){
            txtArea.appendText("\nGame started!");
            engine.mapInit(engine.getD(), engine.getSize());
            printMap();
        }
    }

    public void saveGame(ActionEvent actionEvent) {
        if (engine!=null){//check if game engine is running
            try
            {
                FileOutputStream outFile = new FileOutputStream(System.getProperty("user.dir")+"/savedGame.ser");
                ObjectOutputStream outObject = new ObjectOutputStream(outFile);
                outObject.writeObject(engine);
                outObject.close();
                txtArea.appendText("\nGame saved!");
            }
            catch (Exception e)
            {
                System.out.println("Error" + e.toString() + " " + e.getMessage());
            }
        }
    }

    public void loadGame(ActionEvent actionEvent) {
        try
        {
            FileInputStream inFile = new FileInputStream(System.getProperty("user.dir")+"/savedGame.ser");
            ObjectInputStream inObject = new ObjectInputStream(inFile);
            engine = (GameEngine) inObject.readObject();
            inObject.close();
            txtArea.appendText("\nGame loaded!");
            printMap();
        }
        catch (Exception e)
        {
            System.out.println("Error" + e.toString() + " " + e.getMessage());
        }
    }
    public void showHelp(ActionEvent actionEvent) {
        helpLabel.setText("The player can move up, down, left and right." +
                "\nThe player has the option from one to five to set their difficulty." +
                "\nTo win this game the player must reach the exit cell and the score is the number of gold coins in the collection." +
                "\nThe player loses the game if the player has zero stamina but not at the exit. The score is -1." +
                "\nThe player loses the game if the player falls in a trap but has no gold left in their collection to consume. The score is -1." +
                "\nEach move reduces your stamina by one." +
                "\nTraps reduces your gold coin collection number by one." +
                "\nApples increases your stamina by three." +
                "\nGold coins increases your gold coin collection number by one."
        );
    }
}