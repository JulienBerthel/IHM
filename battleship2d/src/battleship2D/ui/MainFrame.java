package battleship2D.ui;

import battleship2D.model.BoardModel;
import battleship2D.model.CellModel;
import battleship2D.model.CellType;
import battleship2D.model.Coord2D;
import battleship2D.model.Direction;
import battleship2D.model.Fleet;
import battleship2D.model.Ship;
import battleship2D.model.ShipType;
import battleship2D.model.Turn;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Games stages management
 * @author xskapin
 */
public class MainFrame {
    /*=========================================================================*/
    /* Members                                                                 */       
    /*=========================================================================*/
    
    /** Box grouping both computer and player boards */
    private final HBox boardContainer;
    
    private final VBox bottomVBox;

    /** Global parent node */
    private final BorderPane borderPane;
    
    /** Computer board game  */
    private final BoardUIComputer computerBoard;     

    /** End game animation */
    //private final EndGame endGame;
    private EndGame endGame;
    
    /** Explosion image and animation */
    private final Explosion explosion;    
    
    /** Current game stage */
    private GameStages gameStage;
    
    /** Game notifications */
    private final Messages messages;

    /** Missiles sent by boards */
    private final Missile missile;
    
    /** Coordinates linking the (player-selected or computer-selected) target 
     * and the missile trajectory endpoints */
    private final SimpleDoubleProperty missileSourceX, missileSourceY, missileDestX, missileDestY;
    
    /** Player board game  */
    private final BoardUIPlayer playerBoard; 
    
    /** Opponents turns */
    private Turn turn;
    
    /** This class listens for each of its boards and the missile class */
    private PropertyChangeListener propertyChangeListener;
    
    /** Root containing all elements */
    private final AnchorPane root;
    
    /** Container dedicated to ship selection and insertion on the user's board */
    private final ShipInsertion shipInsertion;

    
    /*=========================================================================*/
    /* Public methods                                                          */       
    /*=========================================================================*/   
    
    /**
     * Constructor
     */
    public MainFrame() {
        this.playerBoard = new BoardUIPlayer("Player", new BoardModel(CellType.OCEAN), true);
        this.shipInsertion = new ShipInsertion(this.playerBoard.getBoardModel().getFleet());
        
        /* Computer board' cells contents will stay unknown,  whatever the kind of related cells */
        this.computerBoard = new BoardUIComputer("Computer", new BoardModel(CellType.UNKNOWN), 
                false, Config.level);
        this.boardContainer = new HBox();        
        this.borderPane = new BorderPane();
        this.missile = new Missile(); /* hidden at start up */        
        this.explosion = new Explosion(20,20); /* hidden at start up */        
        this.root = new AnchorPane(this.borderPane);         
        this.endGame = null;
        
        this.messages = new Messages("[Player] Select your ships and place them on your board (mouse button 1 to validate, mouse button 3 to cancel).\n");
        this.bottomVBox = new VBox();
        initRoot();
        initListener();
        initPlayerBoard();        
        initShipSelection(); 
        initExplosion();
        
        this.missileSourceX = new SimpleDoubleProperty(0);
        this.missileSourceY = new SimpleDoubleProperty(0);
        this.missileDestX = new SimpleDoubleProperty(0);
        this.missileDestY = new SimpleDoubleProperty(0);
        this.turn = Turn.PLAYER;
    }
    
    /**
     * Updates the stage of the game
     * @param gameStage new game stage
     */
    public void changeState(GameStages gameStage) {
        setGameStage(gameStage);
        
        switch(this.gameStage) {
            case PLACE_SHIPS_ON_PLAYER_BOARD:
                /* The main interface is created and the player board is displayed. */                
                break;
                
            case INIT_COMPUTER_BOARD:
                initComputerBoard();                   
                break;
                
            case PLAY:
                if (this.turn == Turn.PLAYER) {
                    /* Enables player interactivity */
                    this.borderPane.setMouseTransparent(false);
                    
                    /* A cell is randomly chosen to be the missile source. 
                        The source may be any ship of the player board. */
                    CellModel sourceCellModel = null;
                    
                    /* Search for the first ship that has not been completely destroyed yet. */
                    for (ShipType shipType : ShipType.values()) {
                        sourceCellModel = this.playerBoard.getBoardModel().findFirstCellOfType(CellType.shipTypeToCellType(shipType));
                        if (sourceCellModel != null) {
                            break;
                        }
                    }
                    if (sourceCellModel != null) {
                        CellUI cellUI = this.playerBoard.findCellUIFromModel(sourceCellModel);
                        if (cellUI != null) {
                            this.playerBoard.setMissileSource(cellUI);
                        }
                    }
                    this.messages.append("[Player] Please choose a target on the computer board.\n");                    
                    
                } 
                else {                   
                    this.borderPane.setMouseTransparent(true);
                    this.messages.append("[Computer] Playing...\n");
                    
                    /* A cell is randomly chosen to be the missile source. 
                        The source may be any cell tagged as "UNKNOWN" in the computer board,
                        to avoid the player discovering a computer's ship.*/
                    CellModel sourceCellModel = this.computerBoard.getBoardModel().randomCell(CellType.UNKNOWN, Boolean.TRUE);
                    if (sourceCellModel != null) {
                        CellUI cellUI = this.computerBoard.findCellUIFromModel(sourceCellModel);
                        if (cellUI != null) {
                            this.computerBoard.setMissileSource(cellUI);
                        }
                    }
                    
                    /* A cell is randomly chosen to be the destination of the computer missile. */                    
                    Coord2D cellCoords = this.computerBoard.findMissileDestinationCell();
                    CellModel destCellModel = this.playerBoard.getBoardModel().getCellModel(cellCoords.getRow(), cellCoords.getColumn());

                    if (destCellModel != null) {
                        CellUI cellUI = this.playerBoard.findCellUIFromModel(destCellModel);
                        if (cellUI != null) {
                            this.playerBoard.setMissileDestination(cellUI);
                            launchMissiles(this.playerBoard, this.computerBoard);
                            
                            /* The computer board keeps a copy of the type of the targeted cell in the player board. */
                            this.computerBoard.getLastCellTargeted().setCellType(cellUI.getCellModel().getCellType());
                            
                        }
                    }
                }
                break;
                
            default:
                break;
        }
    }


    /* 
     * Getters/Setters 
     */
    
    public BoardUI getPlayerBoard() {
        return this.playerBoard;
    }

    public BoardUI getComputerBoard() {
        return this.computerBoard;
    }
    
    public GameStages getGameStage() {
        return this.gameStage;
    }

    public void setGameStage(GameStages gameStage) {
        this.gameStage = gameStage;
    }
    
    public AnchorPane getRoot() {
        return this.root;
    }
    public ShipInsertion getShipSelection() {
        return this.shipInsertion;
    }


    /*=========================================================================*/
    /* Private methods                                                         */       
    /*=========================================================================*/

    /**
     * Displays information about the last ship hit by a missile
     * @param boardUI - hit board
     * @param shipType - the kind of ship that was hit
     * @see initListener()
     */
    private void displayLastHitInfo(BoardUI boardUI, ShipType shipType) {
        Fleet fleet = boardUI.getBoardModel().getFleet();
        Ship hitShip = fleet.findShipFromType(shipType);        
        
        if (hitShip != null) {           
            this.messages.append("[" + boardUI.getName() + "] A ship has been hit!\n");
            
            if (fleet.isLastHitShipDestroyed()) {
                this.messages.append("[" + boardUI.getName() + "] " + hitShip.getDescription() + " has been destroyed!!\n");
                
                /* If the destroyed ship was part of the player's fleet, notifies the computer side */
                if (boardUI == this.playerBoard) {
                    this.computerBoard.updateInfoAboutAdverseDestroyedShip(hitShip.getDescription());
                }
                
                if (fleet.isFleetDestroyed()) {
                    this.messages.append("[" + boardUI.getName() + "] " + "The whole fleet has been destroyed!!!\n");
                    if (boardUI == this.playerBoard) {
                        runEndGame(false);
                    }
                    else {
                        runEndGame(true);
                    }
                }
            }
        }        
    }
    
    /**
     * Shows different locations for placing the currently selected ship type
     * on the player board
     * @param cellModel - first cell of the span representing the ship
     * @see initListener()
     */    
    private void displayValidShipSites(CellModel cellModel) {          
        int selectedShipIndex = getShipSelection().selectedShipIndex();        
        Ship selectedShip = this.playerBoard.getBoardModel().getFleet().getShips().get(selectedShipIndex);        
        int shipSize = selectedShip.getSize();        
        
        /* Searches for horizontal and vertical sets of cells available to receive the ship */
        this.playerBoard.availableCellSpanByDirection(cellModel, Direction.NORTH, shipSize);                
        this.playerBoard.availableCellSpanByDirection(cellModel, Direction.WEST, shipSize);
        this.playerBoard.availableCellSpanByDirection(cellModel, Direction.SOUTH, shipSize);
        this.playerBoard.availableCellSpanByDirection(cellModel, Direction.EAST, shipSize);
    }
    
    /**
     * Initializes computer board's fleet and displays it
     * @see changeState()
     */
    private void initComputerBoard() {
        /* No action from both the player board and the ship selection widgets are allowed anymore */
        this.playerBoard.removePropertyChangeListener(this.propertyChangeListener);
        this.shipInsertion.removePropertyChangeListener(this.propertyChangeListener);

        /* The widgets for setting ships location are made invisible */
        this.shipInsertion.hide();

        /* The computer board is displayed */        
        this.computerBoard.setVisible(true);
        
        /* And its ships are randomly placed on without any user action. */
        this.computerBoard.placeShipsOnBoardAtRandom();
        
        /* The computer board can now send signals to this. */
        this.computerBoard.addPropertyChangeListener(this.propertyChangeListener);
        
        /* The missile trajectory endpoints are bound up with cell coordinates */
        this.missile.getTranslateTransition().fromXProperty().bind(this.missileSourceX);        
        this.missile.getTranslateTransition().fromYProperty().bind(this.missileSourceY);
        this.missile.getTranslateTransition().toXProperty().bind(this.missileDestX);        
        this.missile.getTranslateTransition().toYProperty().bind(this.missileDestY);
        
        /* This MainFrame will be notified when the missile animation has finished */
        this.missile.addPropertyChangeListener(this.propertyChangeListener);
        
        changeState(GameStages.PLAY);
    }
   
    /**
     * Initializes the animation played when the games ends
     * @see MainFrame()
     */
    private void initEndGame() {        
        this.endGame.prefWidthProperty().bind(this.borderPane.widthProperty()); 
        this.endGame.prefHeightProperty().bind(this.borderPane.heightProperty());
        this.endGame.setVisible(false);
        this.root.getChildren().addAll(this.endGame);
    }
    
    /**
     * Initializes data related to ship explosion
     * @see MainFrame()
     */
    private void initExplosion() {
        this.explosion.setVisible(false);
    }
    
    
    /**
     * Initializes the boards listener
     * @see MainFrame()
     */
    private void initListener() {
        this.propertyChangeListener = (PropertyChangeEvent propertyChangeEvent) -> {
            
            if (this.gameStage ==  GameStages.PLACE_SHIPS_ON_PLAYER_BOARD) {
                manageEventsPlaceShipsOnPlayerBoard(propertyChangeEvent);
            }
            else {  
                String property = propertyChangeEvent.getPropertyName();

                /* The player missile targets a cellUI of the computer board */
                if ("boardUIComputerUnknownOrShip".equals(property)) {                     
                    launchMissiles(this.computerBoard, this.playerBoard);
                }
 
               /* After the missile animation */
                if ("missileTargetReached".equals(property)) { 
                    if (this.turn == Turn.COMPUTER) {
                        CellUI destCellUI = this.playerBoard.getMissileDestination();
                        destCellUI.updateAfterMissile();
                        if (destCellUI.isLastHitAShip()) {
                            ShipType shipType = CellType.cellTypeToShipType(destCellUI.getLastHit());
                            this.playerBoard.getBoardModel().getFleet().updateHits(shipType);
                            showExplosion(this.playerBoard);
                            displayLastHitInfo(this.playerBoard, shipType);
                        }
                        this.turn = Turn.PLAYER;
                    }
                    else if (this.turn == Turn.PLAYER) {
                        CellUI destCellUI = this.computerBoard.getMissileDestination();
                        destCellUI.updateAfterMissile();
                        if (destCellUI.isLastHitAShip()) {
                            ShipType shipType = CellType.cellTypeToShipType(destCellUI.getLastHit());
                            this.computerBoard.getBoardModel().getFleet().updateHits(shipType);
                            showExplosion(computerBoard);
                            displayLastHitInfo(this.computerBoard, shipType);                            
                        }
                        this.turn = Turn.COMPUTER;            
                    }
                    changeState(GameStages.PLAY);

                }
            }
        };
    }
    
    /**
     * Initializes player's board
     * @see MainFrame()
     */
    private void initPlayerBoard() {        
        this.playerBoard.addPropertyChangeListener(this.propertyChangeListener);        
    }
    
    /**
     * Starts the construction of all elements
     * @see MainFrame()
     */
    private void initRoot() {
        this.borderPane.setTop(new Title());                
	
	/* At init, the computer board is not displayed.
            It will be shown only when the player has set the location
            of its whole fleet */        
	this.boardContainer.getChildren().addAll(this.playerBoard);
        this.boardContainer.getChildren().addAll(this.computerBoard);
        this.computerBoard.setVisible(false);
        this.borderPane.setCenter(this.boardContainer);        

        this.bottomVBox.getChildren().addAll(this.shipInsertion.getShipSettingBox(), 
                this.messages.getScrollPane());        
        this.borderPane.setBottom(this.bottomVBox);        

        AnchorPane.setTopAnchor(this.borderPane, 0d);
        AnchorPane.setBottomAnchor(this.borderPane, 0d);
        AnchorPane.setLeftAnchor(this.borderPane, 0d);
        AnchorPane.setRightAnchor(this.borderPane, 0d);        

        this.root.getChildren().addAll(missile.getRoot());
        this.root.getChildren().addAll(this.explosion);
    }
    
    /**
     * Initializes data for ship selection
     * @see MainFrame()
     */
    private void initShipSelection() {        
        this.shipInsertion.addPropertyChangeListener(this.propertyChangeListener);
    }

    
    /**
     * Displays missile flight from a board to the other
     * @param destBoardUI - Trajectory destination board
     * @param sourceBoardUI - Trajectory source board
     * @see changeState()
     */    
    private void launchMissiles(BoardUI destBoardUI, BoardUI sourceBoardUI) { 
        this.explosion.stop();        
        CellUI destCellUI = destBoardUI.getMissileDestination();
        CellUI sourceCellUI = sourceBoardUI.getMissileSource();
        
        this.missileDestX.bind(destCellUI.layoutXProperty()
        .add(destCellUI.widthProperty().divide(2))
        .add(destBoardUI.layoutXProperty()).add(this.boardContainer.layoutXProperty()));
        
        this.missileDestY.bind(destCellUI.layoutYProperty()
        .add(destCellUI.heightProperty().divide(2))
        .add(destBoardUI.layoutYProperty()).add(this.boardContainer.layoutYProperty()));
        
        this.missileSourceX.bind(sourceCellUI.layoutXProperty()
        .add(sourceCellUI.widthProperty().divide(2))
        .add(sourceBoardUI.layoutXProperty()).add(this.boardContainer.layoutXProperty()));
        
        this.missileSourceY.bind(sourceCellUI.layoutYProperty()
        .add(sourceCellUI.heightProperty().divide(2))
        .add(sourceBoardUI.layoutYProperty()).add(this.boardContainer.layoutYProperty()));
        
        this.missile.play();    
    }
    
    /**
     * Manages an event triggered during PLACE_SHIPS_ON_PLAYER_BOARD game stage
     * @param propertyChangeEvent - the event to deal with
     * @see initListener()
     */
    private void manageEventsPlaceShipsOnPlayerBoard(PropertyChangeEvent propertyChangeEvent) {
        String property = propertyChangeEvent.getPropertyName();
            
        /* On the player board, canceling ship insertion may be triggered
            either with 3rd mouse button, or by clicking onan "ocean" cell */
        if (("boardUIPlayerCancel".equals(property) || ("boardUIPlayerOcean".equals(property)))) {  
            ShipType shipCellType = selectedShipType(this.playerBoard);
            this.playerBoard.eraseSelectedShipLocations(CellType.shipTypeToCellType(shipCellType));                
        }

        if ("boardUIPlayerOcean".equals(property)) {  
            /* Highlights the positions available to place a ship on the board */     
            CellModel cellModel = (CellModel) propertyChangeEvent.getNewValue();                    
            displayValidShipSites(cellModel);
        }


        /* Actually places a ship on the player board */
        if ("boardUIPlayerAvailableLocation".equals(property)) { 
                ShipType shipCellType = selectedShipType(this.playerBoard);
                this.playerBoard.placeShipOnBoard(CellType.shipTypeToCellType(shipCellType));

        }

        /* Checks whether all ships in the fleet have been placed */
        if ("shipInsertionAreAllShipsOnBoard".equals(property)) {             
            Boolean allShipsOnBoard = true;

            Fleet fleet = this.playerBoard.getBoardModel().getFleet();
            for (Ship ship : fleet.getShips()) {
                CellType cellType = CellType.shipTypeToCellType(ship.getShipType());
                if (! this.playerBoard.isShipOnBoard(cellType)) {
                    allShipsOnBoard = false;
                    break;
                }
            }
            if (! allShipsOnBoard) {                       
                this.messages.append("[Player] At least one ship has not been placed on the board.\n");
            }
            else {
            /* The stage about placing ships has ended. Next stage begins.  */
            changeState(GameStages.INIT_COMPUTER_BOARD);  
            }
        }            
    }
   
    /**
     * Activate the end game animation
     * @param playerWins - true if the player has won the game, false otherwise
     */
    private void runEndGame(Boolean playerWins) {        
        this.endGame = new EndGame(playerWins);
        initEndGame();   
        
        /* Waits for 2.5s before displaying the end game animation, in order to see the last (piece of) ship explosion. */
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.endGame.start(); 
    }
    
    /**
     * @return the type of the currently selected ship 
     * @param boardUI - board upon which the ship is located
     * @see initListener()
     */
    private ShipType selectedShipType(BoardUI boardUI) {
        int selectedShipIndex = this.getShipSelection().selectedShipIndex();        
        
        Ship selectedShip = boardUI.getBoardModel().getFleet().getShips().get(selectedShipIndex);
        
        return selectedShip.getShipType();
    }

    /**
     * Places the explosion animation over the last cell representing a ship
     * hit by a missile and plays it
     * @param boardUI - the Board containing the cell
     * @see initListener()
     */
    private void showExplosion(BoardUI boardUI) {
        /* missileDestX and missileDestY represent the center of the hit cell */
        /* To reach the top-left position of the animation, we subtract half its width and height */        
        CellUI cellUI = boardUI.getMissileDestination();
        if((cellUI != null) && (cellUI.getCellModel().getCellType() == CellType.HIT)) {            
            
            this.explosion.translateXProperty().bind(this.missileDestX.subtract(cellUI.widthProperty().divide(2)));                        
            this.explosion.translateYProperty().bind(this.missileDestY.subtract(cellUI.heightProperty().divide(2)));                
            
            this.explosion.prefWidthProperty().bind(cellUI.widthProperty()); // calls explosion.layoutChildren();
            this.explosion.prefHeightProperty().bind(cellUI.heightProperty()); // calls explosion.layoutChildren();
            
            this.explosion.start();
        }            
    }
}
