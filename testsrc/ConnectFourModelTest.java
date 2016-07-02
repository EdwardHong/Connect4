package edu.nyu.pqs.connectfour;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 * This is a class that tests methods mainly in the class ConnectFourModel.
 * This will test all the methods implemented to create the connect four game
 * besides the graphical user interface.
 * 
 * @author  Ssangwook Hong
 * @date    Apr 24 2016 
 *
 */
public class ConnectFourModelTest {

  private ConnectFourModel model;
  private ConnectFourViewForTest view;
  private ConnectFourViewForTest view2;
  private Player player1;
  private Player player2;
  private Player playerAI;
  private Player empty;
  private Mode singleMode;
  private Mode multiMode;
  private List<Mode> modes;
  private List<Player> players;

  /**
   * This is a connect four view class created only for testing purpose without any GUI.
   * It enables testing registering and unregistering views along with triggering the view
   * methods from the model.
   *
   */
  class ConnectFourViewForTest implements ConnectFourListener {
    private String text = "";  
    @Override
    public void clearBoard() {
      text = "clearBoard";
    }
  
    @Override
    public void gameStarted(Mode mode) {
      text = "gameStarted:" + mode;
    }
  
    @Override
    public void gameOver(Status status, Player player) {
      if (status == Status.WIN) {
        text = "win:" + player;
      }
      else if (status == Status.DRAWN) {
        text = "draw";
      }
    }
  
    @Override
    public void updateBoard(Player[][] board, Player player, int row, int column) {
      text = "updateBoard";
    }
  
    @Override
    public void alertNotYourTurn(Player player) {
      text = "notYourTurn";
    }
  
    @Override
    public void alertColumnIsFull(Player player) {
      text = "full";
    }
    
    @Override
    public void disposeFrame() {
      text = "disposed";
    }
  }
  
  /**
   * Instantiates model, players and modes that will be used throughout the unit test.
   */
  @Before
  public void setup() {
    model = new ConnectFourModel();
    modes = new ArrayList<Mode>();
    players = new ArrayList<Player>();
    view = new ConnectFourViewForTest();
    view2 = new ConnectFourViewForTest();
    singleMode = Mode.SINGLE;
    multiMode = Mode.MULTI;    
    modes.add(singleMode);
    modes.add(multiMode);
    model.register(view);
    model.register(view2);
    player1 = Player.ONE;
    player2 = Player.TWO;
    players.add(player1);
    players.add(player2);    
    playerAI = Player.AI;
    empty = Player.EMPTY;
  }

  /**
   * Tests register() by registering the view and confirming that the corresponding view exist
   * in the list of subscribers.
   */
  @Test
  public void testRegisterListener() {
    boolean viewRegistered = false;
    List<ConnectFourListener> listeners = model.getListeners();
    for (ConnectFourListener listener : listeners) {
      if (listener == view) {
        viewRegistered = true;
        break;
      }
    }
    assertTrue(viewRegistered);
  }
  

  /**
   * Tests unregister() by registering the view and unregistering it to see if the view has been
   * removed from the list of subscribers.
   */
  @Test
  public void testunregisterListener() {
    boolean viewNotRegisteredAnymore = true;
    ConnectFourViewForTest unregisterThis = new ConnectFourViewForTest();
    model.register(unregisterThis);    
    model.unregister(unregisterThis);
    List<ConnectFourListener> listeners = model.getListeners();
    for (ConnectFourListener listener : listeners) {
      if (listener == unregisterThis) {
        viewNotRegisteredAnymore = false;
      }
    }
    assertTrue(viewNotRegisteredAnymore);
  }
  
  /**
   * Tests start() in ConnectFourModel which will set the mode of the game, create an empty
   * board, and call fireGameStarted() which will send the notification to gameStarted() to view.
   * 
   */
  @Test
  public void testRestart() {
    for (Mode mode : modes) {
      Player[][] board = createEmptyBoard();
      model.startGame(mode);
      assertEquals(board, model.getBoard());
      assertTrue(model.getPreviousTurn() == Player.EMPTY);
      assertTrue(view.text.equals("gameStarted:" + mode));
      if (mode == Mode.SINGLE) {
        assertTrue(model.isSinglePlayer());
        assertTrue(model.getNumListeners() == 1);
      }
      else {
        assertFalse(model.isSinglePlayer());
        assertTrue(model.getNumListeners() == 2);
      }
    }   
  }
  
  /**
   * Tests playerTies() and checkStatus() by passing a board that has been filled with no
   * four consecutive cells. (no winner)
   * Also tests if checkStatus calls fireGameOver() which sends notification to the
   * ConnectFourView with a proper notification that the game ended in a draw.
   */
  @Test
  public void testDraw() {
    Player[][] board = new Player[][] {
      { player2, player1, player2, player1, player2, player1, player2 },
      { player2, player1, player2, player1, player2, player1, player2 },
      { player2, player1, player2, player1, player2, player1, player2 },
      { player1, player2, player1, player2, player1, player2, player1 },
      { player1, player2, player1, player2, player1, player2, player1 },
      { player1, player2, player1, player2, player1, player2, player1 } };
      model.setBoard(board);
      for (Player player : players) {
        model.checkStatus(player);
        assertTrue(Status.DRAWN == model.getStatus());
        assertTrue(view.text.equals("draw"));
      }
  }
  
  /**
   * Tests playerWin() and checkStatus() by passing a board that has been filled with
   * four consecutive cells from both player1 and player2. (In real game, the game will not
   * get to this state where there are two winners.)
   * Also tests if checkStatus calls fireGameOver() which sends notification to the
   * ConnectFourView with a proper notification that the gamed ended in a win of a player.
   */
  @Test
  public void testWin() {
    Player[][] board = new Player[][] {
      { player2, player1, player2, player1, player2, player1, player2 },
      { player2, player1, player2, player1, player2, player1, player2 },
      { player2, player1, player1, player2, player2, player1, player2 },
      { player1, player2, player1, player2, player1, player2, player1 },
      { player1, player2, player1, player2, player1, player2, player1 },
      { player1, player2, player1, player2, player1, player2, player1 } };
      model.setBoard(board);
      for (Player player : players) {
        model.checkStatus(player);
        assertTrue(Status.WIN == model.getStatus());
        assertTrue(view.text.equals("win:" + player));
      }     
  }
  
  /**
   * Tests putDisc() by putting discs when there are empty columns, filling all slots with player1
   * and player2's discs respectively and checking if they are in the correct position.
   */
  @Test
  public void testPutDiscWhenColumnIsAvailable() {
    for (Player player : players) {
      model.clearBoard();
      for (int row = model.getBoardRowLength()-1 ; row >= 0 ; row--) {
        for (int column = 0 ; column < model.getBoardColumnLength() ; column++) {
          alternateTurn(player);
          model.putDisc(player, column); 
          assertTrue(model.getDiscAt(row, column) == player);
        }
      } 
    }
  }
  
  /**
   * Tests putDisc() by putting discs when all columns are full by attempting to put each disc
   * into all columns that are already filled. 
   * Also tests fireColumnIsFull() which sends notification to the ConnectFourView that the column
   * is full.
   */
  @Test
  public void testPutDiscWhenColumnIsNotAvailable() {
    Player[][] fullBoard = createFullBoard();
    model.setBoard(fullBoard);
    for (Player player : players) {
        for (int column = 0 ; column < model.getBoardColumnLength() ; column++) {
          alternateTurn(player);
          assertFalse(model.putDisc(player, column));
          assertTrue(view.text.equals("full"));
        }
    }
  }
  
  /**
   * Tests putDisc() by putting discs when it's not the player's turn.
   * Also tests fireNotYourTurn() which sends notification to the ConnectFourView that it is
   * not the player's turn.
   */
  @Test
  public void testPutDiscWhenItsNotYourTurn() {
    for (Player player : players) {
      for (int row = model.getBoardRowLength()-1 ; row >= 0 ; row--) {
        for (int column = 0 ; column < model.getBoardColumnLength() ; column++) {
          model.setPreviousTurn(player);
          assertFalse(model.putDisc(player, column));
          assertTrue(view.text.equals("notYourTurn"));
        }
      } 
    }
  }
  
  /**
   * Tests clearBoard() by clearing board and getting an empty board.
   * Also tests fireClearBoard() which sends notification to the ConnectFourView that the
   * apperance of the cells need to be updated.
   */
  @Test
  public void testClearBoardAndGetBoard() {
    Player[][] board = new Player[][] {
      { empty, empty, empty, empty, empty, empty, empty },
      { empty, empty, empty, empty, empty, empty, empty },
      { empty, empty, empty, empty, empty, empty, empty },
      { empty, empty, playerAI, empty, empty, empty, empty },
      { player1, player1, player1, empty, empty, empty, empty },
      { playerAI, playerAI, player1, empty, player1, empty, empty } };
    model.setBoard(board);
    model.clearBoard();
    assertTrue(view.text.equals("clearBoard"));
    assertEquals(board, model.getBoard());
  }
  
  /**
   * Tests smartColumn() by confirming that a random move has been placed by the AI
   * and that the number of cells occupied by playerAI has changed. 
   */
  @Test
  public void testAIrandomMove() {
    Player[][] board = new Player[][] {
      { empty, empty, empty, empty, empty, empty, empty },
      { empty, empty, empty, empty, empty, empty, empty },
      { empty, empty, empty, empty, empty, empty, empty },
      { empty, empty, playerAI, empty, empty, empty, empty },
      { player1, player1, player1, empty, empty, empty, empty },
      { playerAI, playerAI, player1, empty, player1, empty, empty } };
      int aiCountBefore = countPlayer(board, playerAI);
      model.setBoard(board);
      model.smartColumn();
      Player[][] boardAfterMove = model.getBoard();
      int aiCountAfter = countPlayer (boardAfterMove, playerAI);      
      assertFalse(aiCountBefore == aiCountAfter);
  }
  
  /**
   * Tests smartColumn() if one more move by the AI can result in a 4 consecutive AI
   * occupied cells (horizontal) and victory of the AI player.
   */
  @Test
  public void testAIfinishMoveHorizontal() {
    Player[][] board = new Player[][] {
      { empty, empty, empty, empty, empty, empty, empty },
      { empty, empty, empty, empty, empty, empty, empty },
      { empty, empty, empty, empty, empty, empty, empty },
      { empty, empty, playerAI, empty, empty, empty, empty },
      { player1, player1, player1, empty, empty, empty, empty },
      { playerAI, playerAI, playerAI, empty, player1, empty, empty } };
      model.setBoard(board);
      model.smartColumn();
      assertTrue(model.playerWins(playerAI));
  }
  
  /**
   * Tests smartColumn() if one more move by the AI can result in a 4 consecutive AI
   * occupied cells (vertical) and victory of the AI player.
   */
  @Test
  public void testAIfinishMoveVertical() {
    Player[][] board = new Player[][] {
      { empty, empty, empty, empty, empty, empty, empty },
      { empty, empty, empty, empty, empty, empty, empty },
      { empty, empty, empty, empty, empty, empty, empty },
      { empty, empty, playerAI, empty, empty, empty, empty },
      { player1, player1, player1, empty, empty, empty, empty },
      { playerAI, playerAI, playerAI, empty, player1, empty, empty } };
      model.setBoard(board);
      model.smartColumn();
      assertTrue(model.playerWins(playerAI));
  }
  
  /**
   * Tests smartColumn() if one more move by the AI can result in a 4 consecutive AI
   * occupied cells (diagonal) and victory of the AI player.
   */
  @Test
  public void testAIfinishMoveDiagonal() {
    Player[][] board = new Player[][] {
      { empty, empty, empty, empty, empty, empty, empty },
      { empty, empty, empty, empty, empty, empty, empty },
      { empty, empty, empty, empty, empty, empty, empty },
      { empty, empty, playerAI, player1, empty, empty, empty },
      { empty, playerAI, player1, playerAI, empty, empty, empty },
      { playerAI, player1, player1, player1, empty, empty, empty } };
      model.setBoard(board);
      model.smartColumn();
      assertTrue(model.playerWins(playerAI));
  }

  /**
   * Tests topAvailableRow() by getting the top index for columns of different heights.
   */
  @Test
  public void testTopAvailableRow() {
    Player[][] board = new Player[][] {
      { player1, empty, empty, empty, empty, empty },
      { player2, player1, empty, empty, empty, empty },
      { player1, player2, player2, empty, empty, empty },
      { player2, player1, player1, player1, empty, empty },
      { player1, player2, player2, player2, player1, empty },
      { player2, player1, player1, player1, player2, player1 } };
      model.setBoard(board);
      for (int column = 0 ; column < model.getBoardColumnLength() - 1 ; column++) {
        assertEquals(model.topAvailableRow(column), column - 1);
      }
  }
  
  /**
   * Tests getColumn() to see if the model can successfully detect horizontal lines when given
   * a location of the cell. 
   */
  @Test
  public void testCheckHorizontalWhenFourCellsAreInHorizontal() {
    Player[][] board = new Player[][] {
      { empty, empty, empty, empty, empty, empty, empty },
      { empty, empty, empty, empty, empty, empty, empty },
      { empty, empty, empty, empty, empty, empty, empty },
      { empty, empty, empty, empty, empty, empty, empty },
      { empty, empty, empty, empty, empty, empty, empty },
      { player1, player1, player1, empty, empty, empty, empty } };
      model.setBoard(board);
      model.putDisc(player1, 3);
      assertTrue(model.checkColumn(player1, 5, 0));
  }

  /**
   * Tests getColumn() to see if the model can successfully detect that there are not sufficient
   * cells to form a horizontal line. (4 in the game)
   */
  @Test
  public void testCheckHorizontalWhenFourCellsAreNotInHorizontal() {
    Player[][] board = new Player[][] {
      { empty, empty, empty, empty, empty, empty, empty },
      { empty, empty, empty, empty, empty, empty, empty },
      { empty, empty, empty, empty, empty, empty, empty },
      { empty, empty, empty, empty, empty, empty, empty },
      { empty, empty, empty, empty, empty, empty, empty },
      { empty, player1, player1, player1, empty, empty, empty } };
      model.setBoard(board);
      assertFalse(model.checkColumn(player1, 5, 0));
  }
 
  /**
   * Tests getRow() to see if the model can successfully detect vertical lines when given
   * a location of the cell. 
   */
  @Test
  public void testCheckVerticalWhenFourCellsAreInVertical() {
    Player[][] board = new Player[][] {
      { empty, empty, empty, empty, empty, empty, empty },
      { empty, empty, empty, empty, empty, empty, empty },
      { empty, empty, empty, empty, empty, empty, empty },
      { empty, empty, player1, empty, empty, empty, empty },
      { empty, empty, player1, empty, empty, empty, empty },
      { empty, empty, player1, empty, empty, empty, empty } };
      model.setBoard(board);
      model.putDisc(player1, 2);
      assertTrue(model.checkRow(player1, 5, 2));
  }
  
  /**
   * Tests getRow() to see if the model can successfully detect that there are not sufficient
   * cells to form a vertical line. (4 in the game)
   */
  @Test
  public void testCheckVerticalWhenFourCellsAreNotInVertical() {
    Player[][] board = new Player[][] {
      { empty, empty, empty, empty, empty, empty, empty },
      { empty, empty, empty, empty, empty, empty, empty },
      { empty, empty, empty, empty, empty, empty, empty },
      { empty, empty, player1, empty, empty, empty, empty },
      { empty, empty, player1, empty, empty, empty, empty },
      { empty, empty, player1, empty, empty, empty, empty } };
      model.setBoard(board);
      assertFalse(model.checkRow(player1, 5, 2));
  }
  
  /**
   * Tests if the model can successfully detect diagonal lines when given a location of the cell.
   * It tests four cases: up right, up down, down right, and down left.
   */
  @Test
  public void testCheckDiagonalWhenFourCellsAreInDiagnal() {
    Player[][] board = new Player[][] {
      { empty, empty, empty, empty, empty, empty, empty },
      { empty, empty, empty, empty, empty, empty, empty },
      { player2, player1, player1, player1, empty, empty, empty },
      { player1, player2, player1, player1, empty, empty, empty },
      { player1, player1, player2, player1, empty, empty, empty },
      { player1, player1, player2, player2, empty, empty, empty } };
      
      model.setBoard(board);
      assertTrue(model.checkDiagonal(player1, 5, 0));
      assertTrue(model.checkDiagonal(player1, 2, 3));
      assertTrue(model.checkDiagonal(player2, 5, 3));
      assertTrue(model.checkDiagonal(player2, 2, 0));
  }

  /**
   * Tests if the model can successfully detect that there are not sufficient cells to form
   * a diagonal line. (4 in the game)
   * It tests four cases: up right, up down, down right, and down left.
   */
  @Test
  public void testCheckDiagonalWhenFourCellsAreNotInDiagnal() {
    Player[][] board = new Player[][] {
      { empty, empty, empty, empty, empty, empty, empty },
      { empty, empty, empty, empty, empty, empty, empty },
      { player2, player1, player1, player2, empty, empty, empty },
      { player1, player2, player1, player1, empty, empty, empty },
      { player1, player1, player2, player1, empty, empty, empty },
      { player1, player1, player2, player1, empty, empty, empty } };
      
      model.setBoard(board);
      assertFalse(model.checkDiagonal(player1, 5, 0));
      assertFalse(model.checkDiagonal(player1, 2, 3));
      assertFalse(model.checkDiagonal(player2, 5, 3));
      assertFalse(model.checkDiagonal(player2, 2, 0));
  }
  
  /**
   * Tests several getters of the method
   */
  @Test
  public void testGetAndSetPreviousTurn() {
    model.setPreviousTurn(player1);
    assertEquals(model.getPreviousTurn(), player1);
  }
  
  @Test
  public void testGetDiscAt() {
    int row = 4;
    int column = 5;
    model.setDiscAt(Player.ONE, row, column);
    assertEquals(model.getDiscAt(row, column), Player.ONE);    
    model.setDiscAt(Player.TWO, row, column);
    assertEquals(model.getDiscAt(row, column), Player.TWO);  
  }
  
  /**
   * Creates an empty board that will be used for testing purposes.
   * 
   * @return  an empty board
   */
  private Player[][] createEmptyBoard() {
    Player[][] board = new Player[model.getBoardRowLength()][model.getBoardColumnLength()];
    for (int row = 0 ; row < model.getBoardRowLength() ; row++) {
      for (int column = 0 ; column < model.getBoardColumnLength() ; column++) {
        board[row][column] = Player.EMPTY;
      }
    }
    return board;
  }
  
  /**
   * Creates a board that is full with discs.
   * 
   * @return  a full board
   */
  private Player[][] createFullBoard() {
    Player[][] board = new Player[model.getBoardRowLength()][model.getBoardColumnLength()];
    for (int row = 0 ; row < model.getBoardRowLength() ; row++) {
      for (int column = 0 ; column < model.getBoardColumnLength() ; column++) {
        board[row][column] = Player.ONE;
      }
    }
    return board;
  }
  
  /**
   * Alternates player in order to enable a player to place multiple discs without having to
   * switch the turn through the game.  
   * 
   * @param player  the player who needs to put multiple discs for testing purpose.
   */
  private void alternateTurn(Player player) {
    Player differentPlayer = Player.EMPTY;
    if (player == Player.ONE) {
      differentPlayer = Player.TWO;      
    }
    else if (player == Player.TWO) {
      differentPlayer = Player.ONE;
    }
    else if (player == Player.AI) {
      differentPlayer = Player.ONE;
    }
    else {
      differentPlayer = Player.EMPTY;
    }
    model.setPreviousTurn(differentPlayer);
  }
  
  /**
   * Counts the number of cells occupied by a certain player in the board.
   * @param board the board where we want to search for discs.
   * @param player  the player of our interest.
   * @return  the number of discs put on this board by the player.
   */
  private int countPlayer(Player[][] board, Player player) {
    int count = 0;
    for (int row = 0 ; row < model.getBoardRowLength() ; row++) {
      for (int column = 0 ; column < model.getBoardColumnLength() ; column++) {
        if (board[row][column] == player) {
          count++;
        }
      }
    }
    return count;
  }  
}