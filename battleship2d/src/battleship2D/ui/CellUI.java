package battleship2D.ui;

import battleship2D.model.CellModel;
import battleship2D.model.CellType;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javafx.animation.FadeTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.util.Duration;

/**
 * UI associated with a CellType
 * @author xaviator
 */
public class CellUI extends Region {    
    /*=========================================================================*/
    /* Members                                                                 */       
    /*=========================================================================*/
    
    /** Related cell model */
    private final CellModel cellModel;
    
    /** Cell color depends on the related cell model */
    private SimpleStringProperty cellModelColorProperty = null;
    
    /** Appearance attributes*/
    private Region highlight;    
    private FadeTransition highlightTransition;
    
    /** Type of the last ship which has been hit by a missile */
    private CellType lastHit = CellType.UNKNOWN;

    /** Listeners management */
    private final PropertyChangeSupport pcsListeners = new PropertyChangeSupport(this);   
    
    /** (part of) style. The style is updated with regards to the related cell model. */
    private final static String stylePrefix = "-fx-border-width: 1; -fx-border-color: dodgerblue; -fx-background-radius: 10em; -fx-background-insets: 1; -fx-background-size:cover; ";
    
    
    /*=========================================================================*/
    /* Public methods                                                          */       
    /*=========================================================================*/
    
    /**
     * Constructor
     * @param isBound - determines whether this CellUI's style is bound to cell model's
     * @param cellModel - related cell
     */
    public CellUI(CellModel cellModel, Boolean isBound) {     
        super();
        this.cellModel = cellModel;
        
        setStyle(isBound);
        setHighlight();        
        manageMouseEvents();
    }
    
    /**
     * Updates the type of the related CellModel with regard to its current state
     */
    public void updateAfterMissile() {
        /* Depending on the associated BoardUI, the color of this cell 
            may not be bound to its related CellModel at creation. 
            This bind is now realized.
        */
        bindCellModelColor();
        
        switch (this.cellModel.getCellType()) {
            case BATTLESHIP:
                this.lastHit = CellType.BATTLESHIP;
                this.cellModel.setCellType(CellType.HIT);
                break;
                
            case CARRIER:
                this.lastHit = CellType.CARRIER;
                this.cellModel.setCellType(CellType.HIT);
                break;
                
            case CRUISER:
                this.lastHit = CellType.CRUISER;
                this.cellModel.setCellType(CellType.HIT);
                break;
                
            case DESTROYER:
                this.lastHit = CellType.DESTROYER;
                this.cellModel.setCellType(CellType.HIT);
                break;
                
            case SUBMARINE:
                this.lastHit = CellType.SUBMARINE;
                this.cellModel.setCellType(CellType.HIT);
                break;
                
            case UNKNOWN:
                this.lastHit = CellType.UNKNOWN;
                this.cellModel.setCellType(CellType.OCEAN);
                break;
                
            case OCEAN:
                this.lastHit = CellType.OCEAN;
                break;
                
            default:
                this.lastHit = CellType.UNKNOWN;
                break;
        }
    }
    
    /*
     * Property Change Listeners management
     */
    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        this.pcsListeners.addPropertyChangeListener(propertyChangeListener);
    }
    
    public void removePropertyChangeListener (PropertyChangeListener propertyChangeListener) {
        this.pcsListeners.removePropertyChangeListener(propertyChangeListener);
    }

   /*
    *   Getters / Setters
    */
    
    public CellModel getCellModel() {
        return this.cellModel;
    }
    
    public CellType getLastHit() {
        return this.lastHit;
    }
    
    public boolean isLastHitAShip() {
        return this.lastHit == CellType.BATTLESHIP || this.lastHit == CellType.CARRIER
                || this.lastHit == CellType.CRUISER || this.lastHit == CellType.DESTROYER
                || this.lastHit == CellType.SUBMARINE;
    }

    
    /*=========================================================================*/
    /* Protected methods                                                         */       
    /*=========================================================================*/
    
    /**
     * Makes the child region highlight cover the whole layout of the cell
     */
    @Override
    protected void layoutChildren() {
        layoutInArea(this.highlight, 0, 0, getWidth(), getHeight(), getBaselineOffset(), HPos.CENTER, VPos.CENTER);
    }
    
    
    /*=========================================================================*/
    /* Private methods                                                         */       
    /*=========================================================================*/
    
    /**
     * Mouse events management
     * @see CellUI()
     */
    private void manageMouseEvents() {
        addEventHandler(MouseEvent.MOUSE_ENTERED_TARGET, t -> {
                highlightTransition.setRate(1);
                highlightTransition.play();
        });
        
        addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, t -> {
           highlightTransition.setRate(-1);
           highlightTransition.play();            
        });
    
        setOnMouseClicked((MouseEvent t) -> {            
            MouseButton mouseButton = t.getButton();            
            
            /* Third mouse button is used to cancel actions */
            if (mouseButton == MouseButton.SECONDARY) {
                this.pcsListeners.firePropertyChange("cellUICancel", false, true);                
            }
            
            else if (mouseButton == MouseButton.PRIMARY) {                                
                switch (this.cellModel.getCellType()) {
                    case OCEAN:    
                        this.pcsListeners.firePropertyChange("cellUIOcean", null, this.cellModel);
                        break;
                    
                    case AVAILABLE_LOCATION:        
                        this.pcsListeners.firePropertyChange("cellUIAvailableLocation", null, this.cellModel);
                        break;
                    
                    case UNKNOWN:                        
                        case BATTLESHIP:
                        case CARRIER:
                        case CRUISER:
                        case DESTROYER:
                        case SUBMARINE:                                                    
                            this.pcsListeners.firePropertyChange("cellUIUnknownOrShip", null, this);
                        break;                    
                        
                    default:
                        break;
                }
            }
            highlightTransition.setRate(-1);
            highlightTransition.play();
        });
    }
    
    /**
     * Actually bind this with the color of its related cell
     * @see setStyle()
     */
    private void bindCellModelColor() {
        this.cellModelColorProperty = new SimpleStringProperty();
        this.cellModelColorProperty.bind(this.cellModel.getColorProperty());

        /* Initializes style */
        styleProperty().setValue(stylePrefix.concat(this.cellModelColorProperty.getValue()));

        /* Propagate each cell model type modification to this.styleProperty */
        this.cellModelColorProperty.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {            
            styleProperty().setValue(stylePrefix.concat(this.cellModelColorProperty.getValue()));
        });
    }
    
    /**
     * Cell is highlighted when the mouse is hovering on
     * @see CellUI()
     */
    private void setHighlight() {
        /* Mouse hovering changes the appearance */
        this.highlight = new Region();
        this.highlight.setOpacity(0);        
        this.highlight.setStyle("-fx-border-width: 3; -fx-border-color: dodgerblue; -fx-background-color: red");        
        
        /* Animation effect when entering or exiting the cell */
        this.highlightTransition = new FadeTransition(Duration.millis(200), highlight);
        this.highlightTransition.setFromValue(0);
        this.highlightTransition.setToValue(1);        
        
        Light.Distant light = new Light.Distant();        
        light.setAzimuth(-135);
        light.setElevation(30);
        
        setEffect(new Lighting(light));
        setPrefSize(200, 200);
        getChildren().add(highlight);
    }
    
    /**
     * The style depends on the bound cell model
     * @param isBound - determines whether this CellUI's style is bound to cell model's
     * @see CellUI()
     */
    private void setStyle(Boolean isBound) {             
        if (isBound) {
            bindCellModelColor();        
        }
        else {
            styleProperty().setValue(stylePrefix.concat(this.cellModel.getColorProperty().getValue()));
        }
    }
    
}
