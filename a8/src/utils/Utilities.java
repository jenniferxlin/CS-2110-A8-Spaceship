package utils;

import java.awt.*;
import java.util.Random;

public class Utilities {
  public static final String EARTH_NAME= "Earth";
  public static final String DIRECTORY= System.getProperty("user.dir");
  public static final Color SHIP_COLOR= new Color(100, 100, 200);

  /** Random color that attempts to not be the ship color */
  public static Color randomColor() {
    Random rand= new Random();
    Color c;
    int i= 0;
    while ((c= new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat())).equals(SHIP_COLOR) && i < 100) {
      c= new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
      i++;
    }
    return c;
  }
}