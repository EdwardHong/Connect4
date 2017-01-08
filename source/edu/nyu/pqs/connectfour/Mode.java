package edu.nyu.pqs.connectfour;

/**
 * This is an enum that can take two values between single-player mode and multi-player mode.
 * The values of the mode is determined when a start button (either START, or PLAY AI) is pressed.
 * @author  Ssangwook Hong
 * @date    Apr 24 2016
 */
public enum Mode {
  SINGLE("Single Player Mode"),
  MULTI("Multi Player Mode");
  
  private final String name;
  
  Mode(String gameMode) {
    this.name = gameMode;
  }
  
  @Override
  public String toString() {
    return this.name;
  } 
}
