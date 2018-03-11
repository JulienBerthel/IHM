package battleship2D.ui;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Game title container
 * @author xaviator
 */
public class Title extends TilePane {
        
    /*=========================================================================*/
    /* Public methods                                                          */       
    /*=========================================================================*/
    
    /**
     * Constructor
     */
    public Title() {   
        super();
	init();          
    }   


    /*=========================================================================*/
    /* Private methods                                                         */       
    /*=========================================================================*/

    /**
     * Global initialization
     */
    private void init() {	
        StackPane leftPart = new StackPane();
	initLeftPart(leftPart);
	        
        Text rightPart = new Text("2D");
	initRightPart(rightPart);
        
        this.setSnapToPixel(false);
        TilePane.setAlignment(rightPart, Pos.CENTER_LEFT);
        this.getChildren().addAll(leftPart, rightPart);
        this.setPrefTileHeight(40);
    }

    /**
     * Initialize the title's left part
     * @param stackPane - left part
     * @see init()
     */
    private void initLeftPart(StackPane stackPane) {
	stackPane.setStyle("-fx-background-color: black");
        Text text = new Text("BattleShip");
        text.setFont(Font.font(null, FontWeight.BOLD, 18));
        text.setFill(Color.WHITE);
        
        StackPane.setAlignment(text, Pos.CENTER_RIGHT);
        stackPane.getChildren().add(text);        
    }

    /**
     * Initialize the title's right part
     * @param text - right part
     * @see init()
     */
    private void initRightPart(Text text) {
	text.setFont(Font.font(null, FontWeight.BOLD, 18));
    }
}
