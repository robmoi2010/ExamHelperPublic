package com.goglotek.examhelper.screencapture;

import java.awt.*;
import java.awt.image.BufferedImage;

public class RobotScreenCapture implements ScreenCapture {

  private final Robot robot;

  private RobotScreenCapture(Robot robot) {
    this.robot = robot;
  }

  public static ScreenCapture newRobotScreenCapture() throws AWTException {
    return new RobotScreenCapture(new Robot());
  }

  @Override
  public BufferedImage captureScreen() {
    return robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
  }

  @Override
  public BufferedImage captureScreen(int x, int y, int width, int height) {
    return robot.createScreenCapture(new Rectangle(x, y, width, height));
  }
}
