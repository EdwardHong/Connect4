package edu.nyu.pqs.connectfour;

import java.awt.Color;

/**
 * This is an enum that indicates the current player of the game. It consists of string 
 * representing name, the color, and java.awt.Color for the graphics. 
 * 
 * @author  Ssangwook Hong
 * @date    Apr 24 2016
 */
public enum Player {
  EMPTY("No Player Yet", "WHITE", Color.WHITE),
  ONE("Player One", "BLUE", Color.BLUE),
  TWO("Player Two", "RED", Color.RED),
  AI("Alpha 4", "GREEN", Color.GREEN);
  //Comment player
  private final String name;
  private final String colorName;
  private final Color color;
  
  Player(String playerName, String colorName, Color color) {
    this.name = playerName;
    this.colorName = colorName;
    this.color = color;
  }
  
  @Override
  public String toString() {
    String str = "(" + this.name + ":" + this.colorName + ") ";
    return str;
  }
  
  public Color getColor() {
    return this.color;
  }  
}
