package edu.nyu.pqs.connectfour;

/**
 * This is an interface that contains methods for the ConnectFourView which will implement
 * ConnectFourListener.
 * 
 * @author  Ssangwook Hong
 * @date    Apr 24 2016
 */
public interface ConnectFourListener {

  /**
   * Replaces the board with white cells.
   */
  void clearBoard();
  
  /**
   * Displays whether game is in a single-player or a multi-player mode.
   * 
   * @param mode  the mode that the current game is in.
   */
  void gameStarted(Mode mode);
  
  /**
   * Displays that the game has ended with a description to restart the game. 
   * Also disable buttons that can be used to put discs on columns.
   * 
   * @param status  the description of whether the game ended in one side's win or draw.
   * @param player  the player with the win or draw.
   */
  void gameOver(Status status, Player player);
  
  /**
   * Updates the appearance of the board by responding to the column that has been clicked.
   * 
   * @param board   the current board that keeps the information of all cells in the board.
   * @param player  the player that has just made the move.
   * @param row     the top available row index of the column that has been clicked.
   * @param column  the index of the column that has been clicked.
   */
  void updateBoard(Player[][] board, Player player, int row, int column);
  
  /**
   * Alerts the user that it is not his or her turn to play.
   * 
   * @param player  the player who has attempted to play this turn.
   */
  void alertNotYourTurn(Player player);
  
  /**
   * Alerts the user that the column he or she has clicked is already full.
   * 
   * @param player the player who has clicked the column.
   */
  void alertColumnIsFull(Player player);
  
  /**
   * Disposes the frame of the current view once it is not subscribed to the model anymore.
   */
  void disposeFrame();
}
