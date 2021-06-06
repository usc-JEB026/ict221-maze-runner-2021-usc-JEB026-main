package mazerunner.engine;

import java.io.Serializable;

public class Player implements Serializable {
    private int stamina;
    private int goldCoins;
    private int [] location;

    public Player(){
        setStamina(12);
        setGoldCoins(5);
        setLocation(new int [] {9,0});
    }

    public int getStamina() {
        return stamina;
    }

    public void setStamina(int stamina) {
        this.stamina = stamina;
    }

    public int getGoldCoins() {
        return goldCoins;
    }

    public void setGoldCoins(int goldCoins) {
        this.goldCoins = goldCoins;
    }

    public int[] getLocation() {
        return location;
    }

    public void setLocation(int[] location) {
        this.location = location;
    }
}
