package edu.nyu.pqs.connectfour;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This is the model for Connect4 game that contains all the logics of the actual game. 
 * It notifies the view of its change in the state.
 * 
 * @author  Ssangwook Hong
 * @date    Apr 24 2016
 */
public class ConnectFourModel {

  private static List<ConnectFourListener> listeners;
  private boolean AI;
  private final int ROW_LENGTH = 6;
  private final int COLUMN_LENGTH = 7;
  private Player[][] board;
  private Player previousPlayer;
  private Status status;
  private static int frameNum;
  
  public ConnectFourModel() {
    board = new Player[ROW_LENGTH][COLUMN_LENGTH];
    listeners = new ArrayList<ConnectFourListener>();
    clearBoard();
    frameNum = 2;
  }
  
  /**
   * Starts the game by clearing the board, initializing the values for number of discs placed,
   * AI flag, turn flag which keeps track of the correct user to play this turn. 
   * Notifies the view that the game has started with corresponding mode and adjust the number
   * of frames being displayed to the user.
   * 
   * @param mode  the state of the mode either in single-player or multi-player mode.
   */
  public void startGame(Mode mode) {
    AI = false;
    setPreviousTurn(Player.EMPTY);
    List<ConnectFourListener> listeners = getListeners();
    if (mode == Mode.SINGLE) {
      AI = true;
      if (frameNum > 1) {
        ConnectFourListener secondListener = listeners.get(1);
        unregister(secondListener);
        secondListener.disposeFrame();
        frameNum--;
      }
      clearBoard();
    }
    else {
      if (frameNum < 2) {                
        ConnectFourView.getInstance(this, Player.TWO);
        frameNum++;
      }
      clearBoard();
    }
    fireGameStarted(mode);
  }
  
  
  /**
   * Puts the disc to the top available row of the column desired by the player.
   * Checks whether it is the player's turn and also whether the column that the player
   * has chosen is already filled or not.
   * 
   * @param player  the player who is placing the disc.
   * @param column  the column that the player has chosen to place the disc.
   * @return  true if a new disc has been put, false if it has failed.
   */
  public boolean putDisc(Player player, int column) {
    if (player == getPreviousTurn()) {
      fireNotYourTurn(player);   
      return false;
    }   
    else {
      for (int row = ROW_LENGTH-1 ; row >= 0 ; row--) {
        if (columnIsFull(column)) {
          fireColumnIsFull(player);
          return false;
        }
        if (board[row][column] == Player.EMPTY) {
            setDiscAt(player, row, column);
            fireUpdateBoard(player, row, column);
            checkStatus(player);
            setPreviousTurn(player);
            if (AI && !playerWins(player)) {
              smartColumn();
            }     
            return true;
        }      
      }
    }
    return false;
  }
  
  /**
   * Checks if the given column is full or not.
   * 
   * @param column index of the column of interest.
   * @return  true if the column is full. False, otherwise.
   */
  public boolean columnIsFull(int column) {
    return (board[0][column] != Player.EMPTY);
  }
  
  /**
   * Returns the index of the top available row of the given column.
   * 
   * @param  column  the column that the player has chosen.
   * @return  integer value indicating index of the top available row. 
   *          -1 if the column is full and no available index could be found.
   */
  public int topAvailableRow(int column) {
    int topRowIndex = -1;    
    for (int row = ROW_LENGTH-1 ; row >= 0 ; row--) {
      if (board[row][column] == Player.EMPTY) {
        return row;
      }
    }   
    return topRowIndex;
  }

  /**
   * Plays the move of an AI in single-player mode.
   * Places discs onto random columns if a column has not already been filled.
   * If the next move by an AI can result in a win, play that move.
   * 
   */
  public void smartColumn() {
    setPreviousTurn(Player.AI);
    boolean hasPlayedMove = false;
    
    for (int column = 0 ; column <= COLUMN_LENGTH-1 ; column++) {
      int topRow = topAvailableRow(column);
      if (topRow == -1)
        continue;
      if (board[topRow][column] == Player.EMPTY) {
        setDiscAt(Player.AI, topRow, column);
        if (playerWins(Player.AI)) {
          fireUpdateBoard(Player.AI, topRow, column);
          checkStatus(Player.AI);
          hasPlayedMove = true;    
          break;
        }
        else {
          setDiscAt(Player.EMPTY, topRow, column);     
        }
      }
    }
    while (hasPlayedMove == false) {
      int column = new Random().nextInt(COLUMN_LENGTH);
      if (board[0][column] == Player.EMPTY) {
        for (int row = ROW_LENGTH-1 ; row >= 0 ; row--) {
          if (board[row][column] == Player.EMPTY) {
            setDiscAt(Player.AI, row, column);
            hasPlayedMove = true;
            fireUpdateBoard(Player.AI, row, column);
            checkStatus(Player.AI);
            break;
          }
        }
      }
    }    
  }

  /**
   * Checks if the game has ended by the move. 
   * Sets the value of status to either WIN or DRAWN depending on the result of the game.
   * The value of the status is notified to the views that have been registered.
   * 
   * @param player  the player who has just played the move.
   */
  public void checkStatus(Player player) {
    if (playerWins(player)) {
      status = Status.WIN;      
      fireGameOver(status, player);
    }    
    else if (playerTies(player)) {
      status = Status.DRAWN;
      fireGameOver(status, player);
    }
  }
  
  /**
   * Clears the board by resetting the cells to EMPTY, indicating no player has occupied the cell.
   * Notifies the view to update the appearance of the cells accordingly.
   * 
   */
  public void clearBoard() {
    for (int row = ROW_LENGTH-1 ; row >= 0 ; row--) {
      for (int column = COLUMN_LENGTH-1 ; column >= 0 ; column--) {
        board[row][column] = Player.EMPTY;
      }
    }
    fireClearBoard();
  }
  
  /**
   * Checks whether the player has won the game by checking the vertical, 
   * horizontal, and diagonal lines.
   * 
   * @param player  the player with the potential win.
   * @return true if the player has won by connecting 4 lines. False, otherwise.
   */
  public boolean playerWins(Player player) {
    for (int row = 0 ; row <= ROW_LENGTH-1 ; row++) {
      for (int column = 0 ; column <= COLUMN_LENGTH-1 ; column++) {
        if (checkRow(player, row, column) || checkColumn(player, row, column)
            || checkDiagonal(player, row, column))
          return true;
      }
    }
    return false;
  }
  
  /**
   * Checks whether the all the cells have been occupied by the players after checking if either
   * player has won in checkStatus(). It is a draw if all the cells have been occupied and neither
   * has won.
   * 
   * @param player  the player with the potential tie.
   * @return  true if game has been tied. False, otherwise.
   */
  public boolean playerTies(Player player) {
    boolean allColumnsAreFull = true;
    for (int column = 0 ; column < COLUMN_LENGTH ; column++) {
      if (!columnIsFull(column)) {
        allColumnsAreFull = false;
      }
    }
    return (allColumnsAreFull);
  }
  
  /**
   * Checks whether 4 consecutive rows have been occupied by the discs of a same player. 
   * 
   * @param player  the player with the disc.
   * @param row     the row of the cell from where 4 consecutive rows will be counted.
   * @param column  the column of the cell from where 4 consecutive columns will be counted.
   * @return  true if there are 4 consecutive discs by the same player in a row. False, otherwise.
   */
  public boolean checkRow(Player player, int row, int column) {
    if (board[row][column] != player) {
      return false;      
    }
    
    int numConsecutiveRow = 1;
    int numRowBelowToChk = (ROW_LENGTH-1)-row;
    if (numRowBelowToChk > 4) {
      numRowBelowToChk = 4;
    }
    int numRowAboveToChk = 4-numRowBelowToChk;
    
    for (int i = 1 ; i < numRowBelowToChk ; i++) {
      if (board[row+i][column] == player)
        numConsecutiveRow++;
    }
    for (int i = 1 ; i < numRowAboveToChk ; i++) {
      if (board[row-i][column] == player)
        numConsecutiveRow++;
    }
    
    if (numConsecutiveRow == 4)
      return true;
    else
      return false;
  }
  
  /**
   * Checks whether 4 consecutive columns have been occupied by the discs of a same player. 
   * 
   * @param player  the player with the disc.
   * @param row     the row of the cell from where 4 consecutive rows will be counted.
   * @param column  the column of the cell from where 4 consecutive columns will be counted.
   * @return  true if there are 4 consecutive discs by the same player in a column. 
   *          false, otherwise.
   */
  public boolean checkColumn(Player player, int row, int column) {
    if (board[row][column] != player) {
      return false;      
    }
    int numConsecutiveColumn = 1;
    int numColumnRightToChk = (COLUMN_LENGTH-1)-column;
    if (numColumnRightToChk > 3) {
      numColumnRightToChk = 3;
    }
    
    int numColumnLeftToChk = 3-numColumnRightToChk;
    for (int i = 1 ; i <= numColumnRightToChk ; i++) {
      if (board[row][column+i] == player)
        numConsecutiveColumn++;
    }
    for (int i = 1 ; i <= numColumnLeftToChk ; i++) {
      if (board[row][column-i] == player)
        numConsecutiveColumn++;
    }
    if (numConsecutiveColumn == 4)
      return true;
    else
      return false;
  }
  
  /**
   * Checks whether 4 consecutive diagonal lines have been occupied by the discs of a same player.
   * 
   * @param player  the player with the disc.
   * @param row     the row of the cell from where 4 consecutive rows will be counted.
   * @param column  the column of the cell from where 4 consecutive columns will be counted.
   * @return  true if there are 4 consecutive discs by the same player in a diagonal line. 
   *          false, otherwise.
   */
  public boolean checkDiagonal(Player player, int row, int column) {
    if (board[row][column] != player)
      return false; 
    int numConsecutiveDiag = 0;
    int rowUpLeft = row-3;
    int columnUpLeft = column-3;    
    if (rowUpLeft >= 0 && columnUpLeft >= 0) {
      for (int i = 0 ; i < 4 ; i++) {
        if (board[row-i][column-i] == player) {
          numConsecutiveDiag++;
        }
      }
    }
    
    if (numConsecutiveDiag == 4) {
      return true;      
    }
    numConsecutiveDiag = 0;

    int rowUpRight = row-3;
    int columnUpRight = column+3;
    if (rowUpRight >= 0 && columnUpRight <= 6) {
      for (int i = 0 ; i < 4 ; i++) {
        if (board[row-i][column+i] == player) {
          numConsecutiveDiag++;
        }
      }
    }
    
    if (numConsecutiveDiag == 4) {
      return true;      
    }
    numConsecutiveDiag = 0;

    int rowDownRight = row+3;
    int columnDownRight = column+3;
    if (rowDownRight <= 5 && columnDownRight <= 6) {
      for (int i = 0 ; i < 4 ; i++) {
        if (board[row+i][column+i] == player) {
          numConsecutiveDiag++;
        }
      }
    }
    
    if (numConsecutiveDiag == 4) {
      return true;      
    }
    numConsecutiveDiag = 0;
    
    int rowDownLeft = row+3;
    int columnDownLeft = column-3;
    if (rowDownLeft <= 5 && columnDownLeft >= 0) {
      for (int i = 0 ; i < 4 ; i++) {
        if (board[row+i][column-i] == player) {
          numConsecutiveDiag++;
        }
      }
    }
    
    if (numConsecutiveDiag == 4) {
      return true;      
    }
    return false;
  }
  
  /**
   * Register the listener so the listener will get notified of the model.
   * 
   * @param listener  listener that is subscribing to this model.
   */
  public void register(ConnectFourListener listener) {
    listeners.add(listener);
    
  }
  
  /**
   * unregister the listener so the listener will no longer get notified of the model.
   * @param listener
   */
  public void unregister(ConnectFourListener listener) {
    listeners.remove(listener);
  }
  
  /**
   * Gets the value of the indicated variables. 
   * 
   * @return  value of the variables in interest.
   */
  public int getBoardRowLength() {
    return ROW_LENGTH;
  }

  public int getBoardColumnLength() {
    return COLUMN_LENGTH;
  }

  public Status getStatus() {
    return status;
  }
  
  public Player[][] getBoard() {
    return board;
  }
  
  public boolean isSinglePlayer() {
    return AI;
  }
  
  public Player getDiscAt(int row, int column) {
    return board[row][column];
  }
  
  public Player getPreviousTurn() {
    return previousPlayer;
  }
  
  public int getNumListeners() {
    return listeners.size();
  }
  
  public List<ConnectFourListener> getListeners() {
    return listeners;
  }
  
  /**
   * Sets the value of indicated variables.
   * 
   */
  public void setPreviousTurn(Player player) {
    this.previousPlayer = player;
  }
  
  public void setDiscAt(Player player, int row, int column) {
    board[row][column] = player;
  }
  
  public void setBoard(Player[][] board) {
    this.board = board;
  }
  
  /**
   * Notifies all registered views of the changes in the state of the model by triggering
   * the methods in the views. 
   * 
   * @param player  the player that has taken the action.
   * @param row     the index of the row.
   * @param column  the index of the column.
   */
  public void fireUpdateBoard(Player player, int row, int column) {
    for (ConnectFourListener listener:listeners) {
      listener.updateBoard(board, player, row, column);
    }
  }
  
  public void fireNotYourTurn(Player player) {
    for (ConnectFourListener listener : listeners) {
      listener.alertNotYourTurn(player);
    }
  }
  
  public void fireGameStarted(Mode mode) {
    for (ConnectFourListener listener:listeners) {
      listener.gameStarted(mode);
  }
}
  
  void fireGameOver(Status status, Player player) {
    for (ConnectFourListener listener:listeners) {      
      listener.gameOver(status, player);
    }
  }

  void fireColumnIsFull(Player player) {
    for (ConnectFourListener listener:listeners) {      
      listener.alertColumnIsFull(player);
    }
  }

  void fireClearBoard() {
    for (ConnectFourListener listener:listeners) {
      listener.clearBoard();
    }        
  }
}
