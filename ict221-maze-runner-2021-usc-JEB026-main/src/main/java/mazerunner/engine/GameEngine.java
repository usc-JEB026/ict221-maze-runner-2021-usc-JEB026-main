package mazerunner.engine;

import java.io.Serializable;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public class GameEngine implements Serializable {
    /**Private variables**/
    private Cell[][] map;

    private Player p;

    private int d;

    private boolean finish;

    private String gameState;


    /**Encapsulation**/
    public Cell[][] getMap() {
        return map;
    }

    public void setMap(Cell[][] map) {
        this.map = map;
    }

    public Player getP() {
        return p;
    }

    public void setP(Player p) {
        this.p = p;
    }

    public int getD() {
        return d;
    }

    public void setD(int d) {
        this.d = d;
    }

    public boolean getFinish() {
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    public String getGameState() {
        return gameState;
    }

    public void setGameState(String gameState) {
        this.gameState = gameState;
    }

    /**initialise map with elements**/
    public void mapInit(int d, int size){
        for (int row = 0; row < map.length; row++) {//initialise 2d array with new cell objects
            for (int col = 0; col < map.length; col++) {
                map[row][col]=new Cell();
            }
        }
        /**Set items on map..**/
        int[] position;
        do{
            position = getRand(0, size);
            map[position[0]][position[1]].setCellType("E");
        }
        while (position[0]==9 && position[1]==0);//Set Exit Cell anywhere except {9,0}
        for (int i = 0; i < 5; i++) {//Set 5 Gold coins
            position = getRand(0, size);
            if (map[position[0]][position[1]].getCellType() == "_")
                map[position[0]][position[1]].setCellType("G");
            else
                --i;
        }
        for (int i = 0; i < d; i++) {//Set d Traps
            position = getRand(0, size);
            if (map[position[0]][position[1]].getCellType() == "_")
                map[position[0]][position[1]].setCellType("T");
            else
                --i;
        }
        for (int i = 0; i < 10-d; i++) {//Set 10-d Apples
            position = getRand(0, size);
            if (map[position[0]][position[1]].getCellType() == "_")
                map[position[0]][position[1]].setCellType("A");
            else
                --i;
        }
    }

    /**return array with random values**/
    public int[] getRand(int min, int max) {
        Random random = new Random();
        int[] array = new int [2];
        array[0]=random.nextInt(max - min) + min;
        array[1]=random.nextInt(max - min) + min;
        return array;
    }

    /**Print map**/
    public void printMap(){
        for (int row = 0; row < getSize(); row++) {
            for (int col = 0; col < getSize(); col++) {
                if (p.getLocation()[0]==row && p.getLocation()[1]==col){    //represent player location as M
                    System.out.print("M" + "\t");
                }
                else System.out.print(getMap()[row][col].getCellType() + "\t"); //represent everything else by cell type
            }
            System.out.println();
        }
        System.out.println("\n\n------------Report-----------");
        System.out.printf("\nStamina: %d",p.getStamina());
        System.out.printf("\nGold coins: %d",p.getGoldCoins());
        System.out.printf("\nPlayer location: " + Arrays.toString(p.getLocation()));
        System.out.println("\n\n-----------------------------");
    }

    /**Get user to enter difficulty**/
    public int readDifficulty(){
        Scanner scan = new Scanner(System.in);
        int choice = 5;
        do{
            try{
                System.out.println("Select a difficulty from 1 - 10. Default 5.");
                System.out.printf("\nEnter choice:");
                choice=scan.nextInt();
                if (choice>10 || choice<1){
                    System.err.println("Please select between 1 and 10");
                }
            }
            catch(InputMismatchException e){
                System.err.println("Please enter an Integer");
                scan.nextLine();
            }
        }
        while (choice>10 || choice<1);
        return (choice);
    }

    /**Get user input**/
    public int readDirection(){
        Scanner scan = new Scanner(System.in);
        int choice=0;
        do{
            try{
                System.out.println("Select a move. 1 - up , 2 - down , 3 - left, 4 -right");
                System.out.printf("\nEnter choice:");
                choice=scan.nextInt();
                if (choice>4 || choice<1){
                    System.err.println("Please select between 1 and 4");
                }
            }
            catch(InputMismatchException e){
                System.err.println("Please enter an Integer");
                scan.nextLine();
            }
        }
        while (choice>4 || choice<1);
        return choice;
    }

    /**Process movements**/
    public void movePlayer(int direction){
        int newRow=p.getLocation()[0];
        int newCol=p.getLocation()[1];
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
        System.out.println("new Cell Number will be: " + newRow + "," + newCol);
        if (newRow>9 || newCol<0 || newCol>9 || newRow<0){
            System.out.println("The attempted move is invalid");
        }
        else {
            System.out.println("The attempted move is valid");
            p.setStamina(p.getStamina()-1); //reduce stamina
            if (p.getStamina()==0){
                System.out.println("Not enough stamina to move");
                setFinish(true);
                setGameState("Lost!");
            }
            else{
                String newCellType = map[newRow][newCol].getCellType();  //The Type of Cell player will be moved to
                System.out.println("New cell type is: " + newCellType);
                switch (newCellType){
                    case "E":
                        setFinish(true);
                        setGameState("Won!");
                        break;
                    case "T":
                        p.setGoldCoins(p.getGoldCoins()-1);
                        if (p.getGoldCoins()==0){
                            setFinish(true);
                            setGameState("Lost!");
                        }
                        break;
                    case "G":
                        p.setGoldCoins(p.getGoldCoins()+1);
                        map[newRow][newCol].setCellType("_");//Change new cell to _ when Gold consumed
                        break;
                    case "A":
                        p.setStamina(p.getStamina()+3);
                        map[newRow][newCol].setCellType("_");//Change new cell to _ when Apple consumed
                        break;
                }
                p.setLocation(new int[]{newRow, newCol});
            }
        }
    }

    /**
     * The size of the current game.
     *
     * @return this is both the width and the height.
     */
    public int getSize() {
        return map.length;
    }

    /**
     * Creates a square game board.
     *
     * @param size the width and height.
     */
    public GameEngine(int size) {
        setP(new Player());
        setFinish(false);
        setGameState("In Progress");
        setMap(new Cell[size][size]);
    }

    /**
     * Plays a text-based game
     */
    public static void main(String[] args) {
        GameEngine engine = new GameEngine(10);
        System.out.printf("The size of map is %d * %d\n", engine.getSize(), engine.getSize());
        engine.setD(engine.readDifficulty());//Get input difficulty
        engine.mapInit(engine.getD(), engine.getSize());
        while (!engine.getFinish()){
            engine.printMap();
            int direction= engine.readDirection();//Get input direction
            engine.movePlayer(direction);//validate input and move Player
        }
        System.out.println("Game over, result: " + engine.getGameState());
    }
}
