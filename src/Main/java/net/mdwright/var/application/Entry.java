package net.mdwright.var.application;

import java.io.IOException;

/**
 * Entrance class, calls main method in actual Main class.
 * Used to allow javafx to run via the .jar without any additional commandline args
 * As seen in http://fxapps.blogspot.com/2020/11/creating-fat-jars-for-javafx.html
 *
 * @author Matthew Wright
 */
public class Entry {

  /**
   * Main method which simply calls actual Main Method in Main.
   * @param args String array of commandline arguments
   */
  public static void main(String[] args) {
    try {
      Main.main(args);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
