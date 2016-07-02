package edu.nyu.pqs.connectfour;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * This is a factory class that creates different components responsible for the graphical
 * parts of the game.
 *
 * @author  Ssangwook Hong
 * @date    Apr 24 2016 
 */
public class ConnectFourPanelFactory {

  /**
   * This is the abstract class that 3 different panels of the game extends.
   * It extends JPanel and the layout of the Panel can be selected along with
   * the model and view used to set up the actionlistener for corresponding buttons. 
   */
  static abstract class ConnectFourPanel extends JPanel {

    private static final long serialVersionUID = 1173441891833513260L;
    ConnectFourModel model;
    ConnectFourView view;
    
    ConnectFourPanel( LayoutManager layout, ConnectFourModel model, ConnectFourView view) {
      super(layout);
      this.model = model;
      this.view = view;
    }
  }
  
  /**
   * This panel class provides buttons to let the player choose between
   * single-player and multi-player mode. If single-player (AI) mode is selected, the
   * second frame will be closed and the view will unsubscribe from the model.
   * 
   */
  static class ConnectFourHeaderPanel extends ConnectFourPanel {

    private static final long serialVersionUID = -6078780843118387236L;
    private JTextArea status = new JTextArea(10, 5);
    private JButton start_MULTI = new JButton("Play Multiplayer");
    private JButton start_AI = new JButton("Play AI Mode");
    private JPanel buttonsPanel = new JPanel(new GridLayout(2, 1));

    ConnectFourHeaderPanel(ConnectFourModel model, ConnectFourView view) {
      super(new BorderLayout(), model, view);
      ActionListener listener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent event) {
            if (event.getActionCommand().toString().equals("Play AI Mode")) {            
              model.startGame(Mode.SINGLE);             
            }
            else {
              model.startGame(Mode.MULTI);
            }             
        }
      };
      
      start_MULTI.setActionCommand("Play Multi-Player");
      start_MULTI.addActionListener(listener);
      start_AI.setActionCommand("Play AI Mode");
      start_AI.addActionListener(listener);
      buttonsPanel.add(start_MULTI);
      buttonsPanel.add(start_AI);  
      this.add(new JScrollPane(status), BorderLayout.CENTER);
      this.add(buttonsPanel, BorderLayout.EAST);      
    }
    
    /**
     * Prints the message to be displayed in the game.
     * @param text  the text (status of the game) to be displayed.
     */
    void setText(String text) {
      status.append(text);
    }
  }
  
  /**
   *This panel class creates the display for the main board of the game,  
   *6 by 7 cells.
   */
  static class ConnectFourBoardPanel extends ConnectFourPanel {

    private static final long serialVersionUID = -2592237904305489732L;
    private JPanel[][] cells;
    
    ConnectFourBoardPanel(ConnectFourModel model, ConnectFourView view) {
       super(new GridLayout(model.getBoardRowLength(), model.getBoardColumnLength()),
           model, view);   
       cells = new JPanel[model.getBoardRowLength()][model.getBoardColumnLength()];
       createBoard(model);
    }
    
    /**
     * Creates the graphical component of the 6 by 7 cells and initializes the color of the
     * background to white (empty). 
     * 
     * @param model the board with 42 cells.
     */
    private void createBoard(ConnectFourModel model) {
      for (int row = 0 ; row < model.getBoardRowLength() ; row++) {
        for (int column = 0 ; column < model.getBoardColumnLength() ; column++) {
          JPanel cell = new JPanel();
          cell.setBorder(BorderFactory.createLineBorder(Color.gray));
          cell.setBackground(Color.white);
          cells[row][column] = cell;
          this.add(cell);
        }
      }
    }    
    
    /**
     * Resets the cells in the board to white.
     */
    void resetBoardColor() {
      for (int i = 0 ; i < cells.length ; i++) {
        for (int j = 0 ; j < cells[0].length ; j++) {
          cells[i][j].setBackground(Color.white);
        }        
      }
    }
    
    /**
     * Changes the color of the cell that is being occupied by the player at cell[row][column].
     * 
     * @param player  the player with his or her corresponding color.
     * @param row     the selected row
     * @param column  the selected column
     */
    void changeDiscColorAt(Player player, int row, int column) {
      cells[row][column].setBackground(player.getColor());
    }       
  }
  
  /**
   * This panel class creates buttons to place discs on top of the column.
   *
   */
  static class ConnectFourControlPanel extends ConnectFourPanel {
    private static final long serialVersionUID = -343729305368397371L;
    private List<JButton> buttonList = new ArrayList<JButton>();
    ConnectFourControlPanel(ConnectFourModel model, ConnectFourView view) {
      super(new GridLayout(1, model.getBoardColumnLength(), 0, 0), model, view);
      for (int column = 0 ; column < model.getBoardColumnLength() ; column++) {
        JButton button = new JButton("Put it");
        button.setEnabled(false);
        button.setActionCommand(String.valueOf(column));
        ActionListener columnListener = new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            model.putDisc(view.getPlayer(), Integer.parseInt(e.getActionCommand()));
          }
        };
        button.addActionListener(columnListener);
        this.add(button);
        buttonList.add(button);
      }
    }
    
    /**
     * Disables buttons for placing discs.
     */
    void disableDiscButtons() {
      for (JButton button : buttonList) {
        button.setEnabled(false);
      }
    }

    /**
     * Enables buttons for placing discs.
     */
    void enableDiscButtons() {
      for (JButton button : buttonList) {
        button.setEnabled(true);
      }
    }    
  }
  
  private final ConnectFourHeaderPanel headerPanel;
  private final ConnectFourBoardPanel boardPanel;
  private final ConnectFourControlPanel controlPanel;
  
  /**
   * This class is the builder for the ConnectFourPanel factor which helps to create an
   * object in a readable manner and also enforces consistency with options to impose invariants
   * on parameters. While I do not think the builder pattern was necessary in this, it's a good
   * demonstration as required by the assignment.
   */
  public static class Builder {
    private ConnectFourModel model = null;
    private ConnectFourView view = null;
    
    public Builder (ConnectFourModel model, ConnectFourView view) {
      this.model = model;
      this.view = view;
    }
    
    /**
     * Build the builder with the corresponding model and view.
     * 
     * @return  ConnectFourPanelFactory that has been built.
     */
    public ConnectFourPanelFactory build() {
      return new ConnectFourPanelFactory(model, view);
    }
    
  }
  
  ConnectFourPanelFactory(ConnectFourModel model, ConnectFourView view) {
    headerPanel = new ConnectFourHeaderPanel(model, view);
    boardPanel = new ConnectFourBoardPanel(model, view);
    controlPanel = new ConnectFourControlPanel(model, view);
  }
  
  ConnectFourHeaderPanel getHeaderPanel() {
    return headerPanel;
  }
  
  ConnectFourBoardPanel getBoardPanel() {
    return boardPanel;
  }
  
  ConnectFourControlPanel getControlPanel() {
    return controlPanel;
  }
}
