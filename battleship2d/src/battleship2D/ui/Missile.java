package battleship2D.ui;


import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.util.Duration;

/**
 * Missile flight between boards
 * @author xaviator
 */
public class Missile {
    /*=========================================================================*/
    /* Members                                                                 */       
    /*=========================================================================*/

    /** The missile movement is a combination of transitions */
    private ParallelTransition parallelTransition;
    
    /** Listeners management */
    private final PropertyChangeSupport pcsListeners = new PropertyChangeSupport(this);   

    /** Global root for data to display */
    private Group root;    
    
    /** Missile trajectory, depending on cell bords */
    TranslateTransition translateTransition;
            
    
    /*=========================================================================*/
    /* Public methods                                                          */       
    /*=========================================================================*/

    /**
     * Constructor
     */
    public Missile() {              
        init();               
        hideAll();
    }
    
    /**
     * Runs the animation
     */
    public void play() {    
        unhideAll();
        this.parallelTransition.play();
    }
    
    
    /**
     * Unhides all elements
     */
    public void unhideAll() {
        this.root.setVisible(true);
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
    
    public TranslateTransition getTranslateTransition() {
        return this.translateTransition;
    }
    
    public Group getRoot() {
        return this.root;
    }
    

    /*=========================================================================*/
    /* Private methods                                                         */       
    /*=========================================================================*/
    
    /**
     * Hides all elements
     * @see init()
     */
    private void hideAll() {
        this.root.setVisible(false);
    }
    
    /**
     * Initializes private members
     * @see Missile()
     */
    private void init() {
        this.root = new Group();  

	Sphere sphere = new Sphere(10);
	initSphere(sphere);
        this.root.getChildren().addAll(sphere);
        
	ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(1));       
	initScaleTransition(scaleTransition);							      
	
        this.translateTransition = new TranslateTransition(Duration.seconds(2));
	initTranslateTransition();

	this.parallelTransition = new ParallelTransition(sphere, translateTransition, scaleTransition);
	initParallelTransition();
    }

    /** 
     * Initialize the ParallelTransition applied to the missile during its flight
     * @see init()
     */
    private void initParallelTransition() {
        this.parallelTransition.setOnFinished((ActionEvent ae) -> {
            hideAll();
            this.pcsListeners.firePropertyChange("missileTargetReached", null, null);
        });
    }

    /** 
     * Initialize the ScaleTransition applied to the missile during its flight
     * @param scaleTransition - transition to deal with
     * @see init()
     */
    private void initScaleTransition(ScaleTransition scaleTransition) {
        scaleTransition.setFromX(1f);
        scaleTransition.setFromY(1f);
        scaleTransition.setToX(3f);
        scaleTransition.setToY(3f);
        /* Enables scaling in both directions */
        scaleTransition.setAutoReverse(true);     
        /* First cycle for scaling up, second cycle for scaling down */
        scaleTransition.setCycleCount(2);
    }

    /** 
     * Initialize the sphere representing the missile
     * @see init()
     */
    private void initSphere(Sphere sphere) {        
        sphere.setMaterial(new PhongMaterial(Color.ORANGE));
        sphere.translateXProperty().setValue(0);
    }

    /** 
     * Initialize the TranslateTransition applied to the missile during its flight
     * @see init()
     */
    private void initTranslateTransition() {        
        this.translateTransition.setFromX(0);
        this.translateTransition.setFromY(0);
        this.translateTransition.setToX(0);
        this.translateTransition.setToY(0);
        this.translateTransition.setCycleCount(1);
        this.translateTransition.setAutoReverse(true);       
    }
}
