package com.goglotek.examhelper.listeners;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.goglotek.examhelper.ExamHelper;
import com.goglotek.examhelper.exception.ExamHelperException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MouseListener implements EventListener {

  private final ExamHelper examHelper;
  private Logger logger = LoggerFactory.getLogger(MouseListener.class);

  public MouseListener(ExamHelper examHelper) {
    this.examHelper = examHelper;
  }

  public static EventListener newMouseListener(ExamHelper examHelper) {
    return new MouseListener(examHelper);
  }

  @Override
  public void listenForEvents() throws ExamHelperException {
    try {
      if (!examHelper.nativeHookRegistered) {

        GlobalScreen.registerNativeHook();

        examHelper.nativeHookRegistered = true;
      }
    } catch (NativeHookException ex) {
      throw new ExamHelperException(ex, ex.getMessage());
    }

    GlobalScreen.addNativeMouseListener(this);
  }

  @Override
  public void destroyEventListener() throws ExamHelperException {
    GlobalScreen.removeNativeMouseListener(this);
  }

  @Override
  public void nativeMouseClicked(NativeMouseEvent e) {
    try {
      if (examHelper.mouseCaptureActivated) {
        logger.info(e.getX() + "," + e.getY());

        Map<String, Integer> coordinates = new HashMap<>();
        coordinates.put("X", e.getX());
        coordinates.put("Y", e.getY());

        examHelper.clickedMouseCoordinate.push(coordinates);

        examHelper.dictateText("Mouse coordinates captured");
      }
    } catch (ExamHelperException ex) {
      logger.error(ex.getMessage(), ex);
    }
  }
}
