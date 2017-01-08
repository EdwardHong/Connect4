package edu.nyu.pqs.connectfour;

import edu.nyu.pqs.connectfour.ConnectFourPanelFactory.ConnectFourControlPanel;
import edu.nyu.pqs.connectfour.ConnectFourPanelFactory.ConnectFourHeaderPanel;
import edu.nyu.pqs.connectfour.ConnectFourPanelFactory.ConnectFourBoardPanel;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * This is a view class that is notified of the actions in the model and update the
 * graphical parts of the game. 
 * 
 * @author  Ssangwook Hong
 * @date    Apr 24 2016
 */
public class ConnectFourView implements ConnectFourListener{
  
  private ConnectFourControlPanel controlPanel;
  private ConnectFourHeaderPanel headerPanel;
  private ConnectFourBoardPanel boardPanel;
  private Player currentPlayer;
  JFrame frame;
  
  private ConnectFourView(ConnectFourModel model, Player player) {
    frame = new JFrame();
    currentPlayer = player;
    model.register(this);

    ConnectFourPanelFactory.Builder builder = new ConnectFourPanelFactory.Builder(model, this);
    ConnectFourPanelFactory factory = builder.build();
    controlPanel = factory.getControlPanel();
    headerPanel = factory.getHeaderPanel();
    boardPanel = factory.getBoardPanel();
    setupBoard();
  }
  
  /**
   * Ensures that only one instance of a View is created through the Singleton pattern.
   * 
   * @param model   the model this view is registering to.
   * @param player  the player represented by the view.
   * @return  instance of the ConnectFourView.
   */
  public static ConnectFourView getInstance(ConnectFourModel model, Player player) {
      return new ConnectFourView(model, player);             
  }


  @Override
  public void gameStarted(Mode mode) {
    headerPanel.setText("You are "+currentPlayer+"\n");
    headerPanel.setText(mode.toString()+" starting now.....\n");   
  }
  
  @Override
  public void updateBoard(Player[][] board, Player player, int row, int column) {
    boardPanel.changeDiscColorAt(player, row, column);
  }

  @Override
  public void gameOver(Status status, Player player) {
    if (status == Status.WIN) {
      if (player == currentPlayer)
        JOptionPane.showMessageDialog(frame, "You" + currentPlayer + "have won!"
            + " Press start to restart.");
      else
        JOptionPane.showMessageDialog(frame, "You" + currentPlayer + "have lost!"
            + " Press start to restart.");
    }
    
    if (status == Status.DRAWN) {
      JOptionPane.showMessageDialog(frame, "Draw! Press start to restart.");      
    }
    controlPanel.disableDiscButtons();
  }
 
  @Override
  public void clearBoard() {
    controlPanel.enableDiscButtons();
    boardPanel.resetBoardColor();
  }
  
  @Override
  public void alertColumnIsFull(Player player) {
    if (player == currentPlayer) {
      JOptionPane.showMessageDialog(frame, "This column is already full.");      
    }
  }
  
  @Override
  public void alertNotYourTurn(Player player) {
    if (player == currentPlayer) {
      JOptionPane.showMessageDialog(frame, "Please wait for your opponent to go first.");      
    }
  }
  
  @Override
  public void disposeFrame() {
    frame.dispose();
  }
  
  public Player getPlayer(){
    return currentPlayer;
  }
  
  /**
   * Groups different panels together to create a frame for the display.
   */
  private void setupBoard() {
    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.add(boardPanel, BorderLayout.CENTER);
    mainPanel.add(controlPanel, BorderLayout.SOUTH);
    JPanel panelsCombined = new JPanel(new BorderLayout());
    panelsCombined.add(headerPanel, BorderLayout.NORTH);
    panelsCombined.add(mainPanel, BorderLayout.CENTER);
    frame.getContentPane().add(panelsCombined);
    frame.setSize(500, 600);
    frame.setAlwaysOnTop(true);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }
}
