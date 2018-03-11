package battleship2D.model;

/**
 * (row,column) - coordinates pair inside a 2D array
 * @author xaviator
 */
public class Coord2D {
    /*=========================================================================*/
    /* Members                                                                 */       
    /*=========================================================================*/    
    
    /** 2D coordinates */
    private int column, row;

    
    /*=========================================================================*/
    /* Public methods                                                          */       
    /*=========================================================================*/
    
    /**
     * Constructor
     * @param row - new row value
     * @param column new column value
     */
    public Coord2D(int row, int column) {
        setRow(row);
        setColumn(column);
    }
    
    /*
     * Getters / Setters
     */
    
    public int getRow() {
        return this.row;
    }
    public final void setRow(int row) {
        this.row = row;
    }
    
    public int getColumn() {
        return this.column;
    }
    public final void setColumn(int column) {
        this.column = column;
    }
}
