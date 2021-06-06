package mazerunner.engine;

import javafx.scene.layout.Pane;

import java.io.Serializable;

public class Cell implements Serializable {
    private String cellType;

    public Cell(){
        cellType = "_";
    }

    public String getCellType() {
        return cellType;
    }
    public void setCellType(String cellType) {
        this.cellType = cellType;
    }
}
