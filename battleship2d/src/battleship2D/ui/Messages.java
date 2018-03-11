package battleship2D.ui;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;

/**
 * Classe dedicated to games text notifications
 * @author xaviator
 */
public class Messages {
    /*=========================================================================*/
    /* Members                                                                 */       
    /*=========================================================================*/

    /** ScrollPane container */
    private final ScrollPane scrollPane;
    
    /** Notification area */
    private final TextArea textArea;
    

    /*=========================================================================*/
    /* Public methods                                                          */       
    /*=========================================================================*/

    /**
     * Constructor
     * @param firstMessage - default message
     */
    public Messages(String firstMessage) {
        this.scrollPane = new ScrollPane();
        this.textArea = new TextArea(firstMessage);
        
        init();
    }
    

    /*
     * Getters / setters
     */

    public void append(String string) {
        this.textArea.appendText(string);
    }
    
    public ScrollPane getScrollPane() {
        return this.scrollPane;
    }


    /*=========================================================================*/
    /* Private methods                                                         */       
    /*=========================================================================*/

    /**
     * Initialize private members
     */
    private void init() {
        this.textArea.setEditable(false);        
        this.scrollPane.setContent(this.textArea);
        this.scrollPane.setPrefHeight(80);
        this.scrollPane.setFitToHeight(true);
        this.scrollPane.setFitToWidth(true);
    }    
}
