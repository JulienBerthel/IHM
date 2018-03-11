package battleship2D.ui;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
//import org.scenicview.ScenicView;

/**
 * Main application
 * @author xaviator
 */
public class BattleShip2D extends Application {

    /*=========================================================================*/
    /* Members                                                                 */       
    /*=========================================================================*/
    
    /** Game manager */    
    private static MainFrame mainFrame;
   
    
    /*=========================================================================*/
    /* Public methods                                                          */       
    /*=========================================================================*/
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        BattleShip2D.mainFrame = new MainFrame();            
        BattleShip2D.mainFrame.changeState(GameStages.PLACE_SHIPS_ON_PLAYER_BOARD);               
        
        Scene scene = new Scene(BattleShip2D.mainFrame.getRoot(), 900,600);
        primaryStage.setScene(scene);
        /* ScenicView.show(scene); // If ScenicView is used */
        primaryStage.show();        
    }
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {        
        launch(args);
    }
    
}
