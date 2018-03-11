package battleship2D.model;

import javafx.beans.property.SimpleStringProperty;

/**
 * Board model element
 * @author xaviator
 */
public class CellModel {
    /*=========================================================================*/
    /* Members                                                                 */       
    /*=========================================================================*/
    
    /** Related type */
    private CellType cellType;
    
    /** Store the asso ciated color */
    private final SimpleStringProperty colorProperty;
    	 ///////
    /** Unique identifier */
    private Integer id;
    //DCDDSSDx
    
    /*=========================================================================*/
    /* Public methods                                                          */       
    /*=========================================================================*/
    
    /**
     * Constructor
     * @param  cellType - associated cell type
     * @param id - identifier
     */
    public CellModel(CellType cellType, Integer id) {
        this.colorProperty = new SimpleStringProperty();
        
        setCellType(cellType);
        setId(id);
    }
    
    /**
     * Displays in formations
     * @return  a brief description
     */
    public String display() {
        return "[" + this.id + " - " + this.cellType.getDescription() + " - " 
                + this.cellType.getAppearance() + "]";
    }
    
    /*
     * Getters / Setters
     */
    
    public CellType getCellType() {
        return this.cellType;
    }
    
    public final void setCellType (CellType cellType) {
        this.cellType = cellType;
        this.colorProperty.setValue(this.cellType.getAppearance());
    }
    
    public SimpleStringProperty getColorProperty() {
        return this.colorProperty;
    }
    
    public Integer getId() {
        return this.id;
    }
    
    public final void setId(Integer id) {
        this.id = id;
    }
}
