package battleship2D.ui;


import battleship2D.model.Fleet;
import battleship2D.model.Ship;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * User-selected ship in a fleet
 * @author xaviator
 */
public class ShipInsertion {
    /*=========================================================================*/
    /* Members                                                                 */       
    /*=========================================================================*/
    
    /** Description of ships to select */
    private ChoiceBox<String> choiceBox;
    
    /** Ship settings global box */
    private final HBox shipSettingBox;
    
    /** Listeners management */
    private final PropertyChangeSupport pcsListeners = new PropertyChangeSupport(this);   
    
    
    /*=========================================================================*/
    /* Public methods                                                          */       
    /*=========================================================================*/
    
    /**
     * Constructor
     * @param fleet - set of ships among which the user makes a selection
     */
    public ShipInsertion(Fleet fleet) {
        this.shipSettingBox = new HBox(5);
        initSettingBox(fleet);        
    }
    
    /**
     * Hides all contents
     */
    public void hide() {
        this.choiceBox.setVisible(false);
        this.shipSettingBox.setVisible(false);
    }
    
    /**
     * @return the user-selected ship
     */
    public int selectedShipIndex() {
        return this.choiceBox.getSelectionModel().getSelectedIndex();        
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
     * Getters / Setters
     */
   
    public HBox getShipSettingBox() {
        return this.shipSettingBox;
    }
    
    
    /*=========================================================================*/
    /* Private methods                                                         */       
    /*=========================================================================*/
    
    /**
     * Button to validate the settings of all ships
     * @return the Done button
     * @see initButtonsBox()
     */
    private Node doneButton() {
        Button button = new Button("Done");
        button.setOnAction(e -> {
            this.pcsListeners.firePropertyChange("shipInsertionAreAllShipsOnBoard", false, true);            
        });
        return button;
    }

    /**
     * Builds the VBox containing buttons for ships settings 
     * @see initSettingBox()
     */
    private VBox initButtonsBox() {
        VBox buttonsBox = new VBox(10);        
        
        Node doneButton = doneButton();
        buttonsBox.getChildren().addAll(doneButton);
        
        return buttonsBox;
    }

    /**
     * Initializes the global setting box
     * @param fleet - set of ships for to be user-chosen 
     * @see ShipSelection()
     */
    private void initSettingBox(Fleet fleet) {
        VBox shipChoiceBox = initShipChoiceBox(fleet);
        VBox buttonsBox = initButtonsBox();
        
        this.shipSettingBox.getChildren().addAll(shipChoiceBox, buttonsBox);
        this.shipSettingBox.setAlignment(Pos.TOP_CENTER);
    }
    
    /**
     * Fills a ChoiceBow with a ship fleet
     * @param fleet - set of ships
     * @see ShipSelection()
     */
    private VBox initShipChoiceBox(Fleet fleet) {
        this.choiceBox = new ChoiceBox<>();        
        
        for (Ship ship : fleet.getShips()) {
            this.choiceBox.getItems().add(ship.getDescription());            
        }
       this.choiceBox.getSelectionModel().selectFirst();
        
        VBox shipChoiceBox = new VBox(10);
        shipChoiceBox.getChildren().addAll(this.choiceBox);
        
        shipChoiceBox.setAlignment(Pos.CENTER);
        return shipChoiceBox;
    }    
}
