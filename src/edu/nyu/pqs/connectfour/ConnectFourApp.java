package edu.nyu.pqs.connectfour;
/**
 * This is a class that contains the main method to run the Connect4 game.
 * Once the game is started, the user can choose between single player mode,
 * and multiplayer mode.
 * 
 * @author  Ssangwook Hong
 * @date    Apr 24 2016
 */
public class ConnectFourApp {
  
  /**
   * Creates a model and two instances of views that represent each player and are
   * notified of the actions in the model in order to update the graphical components of the game.
   */
  private void startApp() {
    ConnectFourModel model = new ConnectFourModel(); 
    Player player1 = Player.ONE;
    Player player2 = Player.TWO;    
    ConnectFourView.getInstance(model, player1);
    ConnectFourView.getInstance(model, player2);
  }
  
  public static void main(String[] args) {
    ConnectFourApp app = new ConnectFourApp();
    app.startApp();   
  }
}