package battleship2D.ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.util.Duration;

/**
 * Animatation triggered when the game has ended
 * @author xaviator
 */
public class EndGame extends Region {
    /*=========================================================================*/
    /* Members                                                                 */       
    /*=========================================================================*/
    
    /** Animation images */    
    private final ImageView winnerImage, winsImage;

    /** Lapse time between two frame iterations */
    private final int duration;
    
    /** Dynamic scaling ration to make image appear */
    private final SimpleDoubleProperty scaleRatioProparty;
    
    /** Animation player */
    private final Timeline timeline;
    
    
    /*=========================================================================*/
    /* Public methods                                                          */       
    /*=========================================================================*/
    
    /**
     * Constructor
     * @param playerWins - Determines whether the winner image to display represents
     * the player (true) or the computer (false)
     */
    public EndGame(boolean playerWins) {    
        super();
        this.duration = 80;
        this.scaleRatioProparty = new SimpleDoubleProperty();
        
        styleProperty().setValue("-fx-background-size:cover; -fx-background-image: url(\"battleship2D/pictures/battleship-endgame.png\")");
        
        if (playerWins) {
            this.winnerImage = new ImageView(getClass().getResource("/battleship2D/pictures/player.png").toExternalForm());
        }
        else {
            this.winnerImage = new ImageView(getClass().getResource("/battleship2D/pictures/computer.png").toExternalForm());
        }
        initWinner();    
        
        this.winsImage = new ImageView(getClass().getResource("/battleship2D/pictures/wins.png").toExternalForm());                
        initWins();
        
        this.timeline = new Timeline();
        initTimeline();
        
        this.setVisible(false);
    }

    /**
     * Plays the animation
     */    
    public void start() {
        this.scaleRatioProparty.setValue(0.0);
        this.setVisible(true);
        this.timeline.play(); 
    }
    
    /**
     * Stops the animation
     */
    public void stop() { 
        this.timeline.stop(); 
        this.setVisible(false);
    }

    
    /*=========================================================================*/
    /* Private methods                                                         */       
    /*=========================================================================*/
    
    /** 
     * Initializes timeline properties 
     * @see EndGame
     */
    private void initTimeline() {        
        this.timeline.setCycleCount(Timeline.INDEFINITE);
        
        KeyFrame keyFrame = new KeyFrame(new Duration(this.duration), (ActionEvent event) -> {            
                runAnimation();
        });
        timeline.getKeyFrames().add(keyFrame);
    }
    
    /** 
     * Initializes winnerImage's properties 
     * @see EndGame()
     */
    private void initWinner() {        
        this.winnerImage.fitHeightProperty().bind(this.prefHeightProperty().divide(3));
        this.winnerImage.fitWidthProperty().bind(this.prefWidthProperty().divide(1.5));
        this.winnerImage.setFocusTraversable(false); 
        this.winnerImage.scaleXProperty().bind(this.scaleRatioProparty);
        this.winnerImage.scaleYProperty().bind(this.scaleRatioProparty);
        getChildren().add(this.winnerImage);
    }
    
    /** 
     * Initializes winsImage's properties 
     * @see EndGame()
     */
    private void initWins() {        
        this.winsImage.fitHeightProperty().bind(this.prefHeightProperty().divide(3));
        this.winsImage.fitWidthProperty().bind(this.prefWidthProperty().divide(2));
        this.winsImage.setFocusTraversable(false);     
        this.winsImage.scaleXProperty().bind(this.scaleRatioProparty);
        this.winsImage.scaleYProperty().bind(this.scaleRatioProparty);
        getChildren().add(this.winsImage);
    }
    
    /**
     * The animation moves images at each step
     * @see initTimeline()
     */
    private void runAnimation() {                
        this.winnerImage.setTranslateX(this.getWidth()/2 - this.winnerImage.getFitWidth()/2);
        this.winnerImage.setTranslateY(this.getHeight()*0.3 - this.winnerImage.getFitHeight()/2);
        
        
        this.winsImage.setTranslateX(this.getWidth()/2 - this.winsImage.getFitWidth()/2);
        this.winsImage.setTranslateY(this.getHeight()*0.75 - this.winsImage.getFitHeight()/2);
        
        if (this.scaleRatioProparty.getValue() <= 1.0) {
            this.scaleRatioProparty.setValue(this.scaleRatioProparty.getValue() + 0.1);
        }
    }
}
