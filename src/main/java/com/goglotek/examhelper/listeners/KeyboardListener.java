package com.goglotek.examhelper.listeners;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.goglotek.examhelper.ExamHelper;
import com.goglotek.examhelper.exception.ExamHelperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeyboardListener implements EventListener {

  private final ExamHelper examHelper;
  StringBuilder keyBuffer = new StringBuilder();
  private Logger logger = LoggerFactory.getLogger(KeyboardListener.class);

  private KeyboardListener(ExamHelper examHelper) {
    this.examHelper = examHelper;
  }

  @Override
  public void nativeKeyPressed(NativeKeyEvent e) {
    String key = NativeKeyEvent.getKeyText(e.getKeyCode());

    if (key.equalsIgnoreCase(System.getProperty("question-trigger-key", "Ctrl"))) {
      if (!examHelper.mouseCaptureActivated) {
        logger.info("activating mouse click capture");
        examHelper.mouseCaptureActivated = true;
      }
    }
  }

  @Override
  public void nativeKeyReleased(NativeKeyEvent e) {
    String key = NativeKeyEvent.getKeyText(e.getKeyCode());

    if (key.equalsIgnoreCase(System.getProperty("question-trigger-key", "Ctrl"))) {
      try {
        if (examHelper.mouseCaptureActivated) {
          logger.info("Deactivating mouse click capture");
          examHelper.mouseCaptureActivated = false;
          examHelper.handleQuestionEvent();
        }
      } catch (ExamHelperException ex) {
        logger.error(ex.getMessage(), ex);
      }
    }
  }

  @Override
  public void nativeKeyTyped(NativeKeyEvent e) {
    // Buffers key if buffer is less than two. This event uses two character combination for activation of various functionalities
    if (keyBuffer.length() < 1) {
      keyBuffer.append(e.getKeyChar());
      return;
    }

    keyBuffer.append(e.getKeyChar());

    String key = keyBuffer.toString();

    //reset buffer
    keyBuffer.delete(0, keyBuffer.length());

    if (key.equalsIgnoreCase(System.getProperty("peek-latest-answer-key"))) {
      try {
        logger.info("Replaying and retaining latest..");

        examHelper.replayAndRetainLatestAnswer();
      } catch (ExamHelperException ex) {
        logger.error(ex.getMessage(), ex);
      }
    } else if (key.equalsIgnoreCase(System.getProperty("pop-latest-answer-key"))) {
      try {
        logger.info("Replaying and removing latest...");

        examHelper.replayAndRemoveLatestAnswer();
      } catch (ExamHelperException ex) {
        logger.error(ex.getMessage(), ex);
      }
    } else if (key.equalsIgnoreCase(System.getProperty("write-to-file-key"))) {
      examHelper.writeQADataToFile();
    }
  }

  @Override
  public void listenForEvents() throws ExamHelperException {
    try {
      if (!examHelper.nativeHookRegistered) {
        GlobalScreen.registerNativeHook();

        examHelper.nativeHookRegistered = true;
      }

      GlobalScreen.addNativeKeyListener(this);

    } catch (NativeHookException ex) {
      throw new ExamHelperException(ex, ex.getMessage());
    }
  }

  @Override
  public void destroyEventListener() throws ExamHelperException {
    GlobalScreen.removeNativeKeyListener(this);
  }

  public static KeyboardListener newKeyboardListener(ExamHelper examHelper) {
    return new KeyboardListener(examHelper);
  }
}
