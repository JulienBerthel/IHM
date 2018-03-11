package battleship2D.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * A Simple Particle that draws its self as a circle.
 * Code derived from Oracle's JavaFX sample "Ensemble.jar"
 */
public class Particle {
    /*=========================================================================*/
    /* Members                                                                 */       
    /*=========================================================================*/

    /** Gravity coefficient */
    private static final double GRAVITY = 0.06;

    /** Alpha blending coefficient to make the particle fade */
    private double alpha;

    /** Particle color */
    private final Paint color;

    /** "Dampening" coefficient for particle position */
    private final double easing;

    /** Fading coefficient */
    private final double fade;

    /** Dtermine whether a particle tail is drawn */
    private final boolean hasTail;

    /** Previous position */
    private double lastPosX, lastPosY;

    /** Current position */
    private double posX, posY;

    /** Determine whether this particle should give birth to other particles */
    private final boolean shouldExplodeChildren;
    
    /** Particle's size */
    private final int size;

    /** New position after moving */
    private final double targetX, targetY;

    /** Use a physics model to update position */
    private final boolean usePhysics;

    /** Velocity 2D coordinates */
    private double velX, velY;
        
    /*=========================================================================*/
    /* Public methods                                                          */       
    /*=========================================================================*/
    
    /**
     * Constructor
     * @param posX - Particle's X-coordinate
     * @param posY - Particle's Y-coordinate
     * @param velX - Particle's X-velocity
     * @param velY - Particle's Y-velocity
     * @param targetX - Particle's new X-coordinate after moving
     * @param targetY - Particle's new Y-coordinate after moving
     * @param color - Particle's color
     * @param size - Particle's size
     * @param usePhysics - true if a physics model should be used to compute the new position
     * @param shouldExplodeChildren - true if the Pärticle's shound give birth to smaller Particles
     * @param hasTail - true is a tail should be displayed following this Particle's move
     */
    public Particle(double posX, double posY, double velX, double velY, double targetX, double targetY, 
		    Paint color,int size, boolean usePhysics, boolean shouldExplodeChildren, boolean hasTail) {
	this.posX = posX;
	this.posY = posY;
	this.velX = velX;
	this.velY = velY;
	this.targetX = targetX;
	this.targetY = targetY;
	this.color = color;
	this.size = size;
	this.usePhysics = usePhysics;
	this.shouldExplodeChildren = shouldExplodeChildren;
	this.hasTail = hasTail;
	this.alpha    = 1;
	this.easing   = Math.random() * 0.02;
	this.fade     = Math.random() * 0.1;
    }

    /**
     * Displays the particle
     * @param context drawing zone
     */
    public void draw(GraphicsContext context) {
	final double x = Math.round(this.posX);
	final double y = Math.round(this.posY);
	final double xVel = (x - this.lastPosX) * -5;
	final double yVel = (y - this.lastPosY) * -5;

	/* Set the opacity for all drawing of this particle */
	context.setGlobalAlpha(Math.random() * this.alpha);

	/* Draw particle */
	context.setFill(color);
	context.fillOval(x-size, y-size, size+size, size+size);

	/* Draw the arrow triangle from where we were to where we are now */
	if (hasTail) {
	    context.setFill(Color.rgb(255,255,255,0.3));
	    context.fillPolygon(new double[]{posX + 1.5,posX + xVel,posX - 1.5}, 
				new double[]{posY,posY + yVel,posY}, 3);
	}
    }

    /**
     * Update internal data
     * @return true if the Particle has expired
     */
    public boolean update() {
	lastPosX = posX;
	lastPosY = posY;

	if (this.usePhysics) { /* On way down */
	    velY += GRAVITY;
	    posY += velY;
	    this.alpha -= this.fade; /* Fade out particle */
	} 
	else { /* on way up */  
	    double distance = (targetY - posY);

	    /* Ease the position */
	    posY += distance * (0.03 + easing);

	    /* Cap to 1 */
	    alpha = Math.min(distance * distance * 0.00005, 1);
	}
	posX += velX;
	return alpha < 0.005;
    }
    
    /*
     * Getters / Setters
     */
    
    public Paint getColor() {
        return this.color;
    }
    
    public double getPosX() {
        return this.posX;
    }
    
    public double getPosY() {
        return this.posY;
    }
    
    public boolean getShoundExploreChildren() {
        return this.shouldExplodeChildren;
    }
    
    public int getSize() {
        return this.size;
    }
}
