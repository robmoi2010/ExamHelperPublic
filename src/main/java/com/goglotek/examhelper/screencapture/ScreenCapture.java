package com.goglotek.examhelper.screencapture;

import java.awt.image.BufferedImage;

public interface ScreenCapture {

  public BufferedImage captureScreen();

  public BufferedImage captureScreen(int x, int y, int width, int height);
}
