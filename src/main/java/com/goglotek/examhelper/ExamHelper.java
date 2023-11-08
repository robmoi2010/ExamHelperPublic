package com.goglotek.examhelper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goglotek.examhelper.ai.AI;
import com.goglotek.examhelper.ai.AIFactory;
import com.goglotek.examhelper.exception.ExamHelperException;
import com.goglotek.examhelper.listeners.EventListener;
import com.goglotek.examhelper.listeners.KeyboardListener;
import com.goglotek.examhelper.listeners.MouseListener;
import com.goglotek.examhelper.ocr.OCR;
import com.goglotek.examhelper.ocr.TesseractOCR;
import com.goglotek.examhelper.screencapture.RobotScreenCapture;
import com.goglotek.examhelper.screencapture.ScreenCapture;
import com.goglotek.examhelper.speechgenerator.SpeechGenerator;
import com.goglotek.examhelper.speechgenerator.SpeechGeneratorFactory;
import com.goglotek.examhelper.textformatters.AnswerTextFormatter;
import com.goglotek.examhelper.textformatters.QuestionTextFormatter;
import com.goglotek.examhelper.textformatters.TextFormatter;
import java.awt.AWTException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExamHelper {

  private EventListener keyboardEventListener;
  private EventListener mouseEventListener;
  private ScreenCapture screenCapture;
  private OCR ocr;
  private TextFormatter questionTextFormatter;
  private TextFormatter answerTextFormatter;
  private AI ai;
  private SpeechGenerator speechGenerator;
  public boolean nativeHookRegistered = false;
  public boolean mouseCaptureActivated = false;
  public Stack<Map<String, Integer>> clickedMouseCoordinate = new Stack<>();
  public Stack<Map<String, String>> questionAndAnswers = new Stack<>();
  private final Logger logger = LoggerFactory.getLogger(ExamHelper.class);

  public ExamHelper() {

  }

  /**
   * App entry method. initializes configs and listeners
   */
  public void initialize() throws ExamHelperException {
    //Load additional properties from file
    Properties props = System.getProperties();
    try {
      props.load(new BufferedReader(new FileReader("Configs.properties")));

      //creates all objects during start-up to improve performance during usage.
      screenCapture = RobotScreenCapture.newRobotScreenCapture();
      ocr = TesseractOCR.newTesseractOCR();
      ai = AIFactory.getDefaultAI();
      answerTextFormatter = AnswerTextFormatter.newAnswerTextFormatter(ai);
      questionTextFormatter = QuestionTextFormatter.newQuestionTextFormatter(ai);
      speechGenerator = SpeechGeneratorFactory.getDefaultSpeechGenerator();
      mouseEventListener = MouseListener.newMouseListener(this);
      keyboardEventListener = KeyboardListener.newKeyboardListener(this);

      //activate keyboard and mouse  events as the last step of initialization
      activateKeyboardEvents();
      activateMouseEvents();
    } catch (IOException | AWTException e) {
      throw new ExamHelperException(e, e.getMessage());
    }
  }

  /**
   * handle question related key trigger
   */
  public void handleQuestionEvent() throws ExamHelperException {
    try {
      // Do nothing if stack is empty i.e no mouse clicks
      if (clickedMouseCoordinate.empty()) {
        return;
      }
      // Gets latest mouse coordinates to represent screen capture area from stack
      Map<String, Integer> dest = clickedMouseCoordinate.pop();
      // Do nothing if stack doesn't contain two sets of coordinates
      if (clickedMouseCoordinate.empty()) {
        return;
      }
      Map<String, Integer> origin = clickedMouseCoordinate.pop();

      // Captures screen for a given coordinates
      BufferedImage image = screenCapture.captureScreen(origin.get("X"), origin.get("Y"),
          (dest.get("X") - origin.get("X")),
          (dest.get("Y") - origin.get("Y")));

      // Generates text from the captured screen area
      String text = ocr.textFromImage(image);

      // Formats question text
      String formattedQuestion = questionTextFormatter.formatText(text);

      // Check if copy question to clipboard is enabled
      if (Boolean.parseBoolean(System.getProperty("copy-question-to-clipboard"))) {
        // Copies to clipboard and returns. For use in online chat ai for tests that doesn't capture screenshots.
        logger.info("Copying \"" + formattedQuestion + "\" to clipboard...");
        copyToClipBoard(formattedQuestion);
        return;
      }

      // Gets answer from configured-default AI
      String answer = ai.getAnswer(formattedQuestion);

      // Formats answer from AI
      String formattedAnswer = answerTextFormatter.formatText(answer);

      // Dictates or prints answer using default-configured Text-to-speech library
      dictateText(formattedAnswer);

      // Pushes question and answer to stack for later replay
      Map<String, String> questionAnswer = new HashMap<>();
      questionAnswer.put(formattedQuestion, formattedAnswer);
      questionAndAnswers.push(questionAnswer);
    } catch (ExamHelperException e) {
      writeQADataToFile();
      logger.error(e.getMessage(), e);
    }

  }


  public void activateMouseEvents() throws ExamHelperException {
    mouseEventListener.listenForEvents();
  }

  public void deactivateMouseEvents() throws ExamHelperException {
    mouseEventListener.destroyEventListener();
  }

  public void activateKeyboardEvents() throws ExamHelperException {
    keyboardEventListener.listenForEvents();
  }

  public void replayAndRemoveLatestAnswer() throws ExamHelperException {
    String answer = "No more answers";
    try {
      Map<String, String> qa = questionAndAnswers.pop();
      answer = qa.values().stream().findFirst().get();
    } catch (EmptyStackException e) {
      logger.error(e.getMessage(), e);
    }
    dictateText(answer);
  }

  public void replayAndRetainLatestAnswer() throws ExamHelperException {
    String answer = "No more answers";
    try {
      Map<String, String> qa = questionAndAnswers.peek();
      answer = qa.values().stream().findFirst().get();
    } catch (EmptyStackException e) {
      logger.error(e.getMessage(), e);
    }
    dictateText(answer);

  }

  public void writeQADataToFile() {
    Path p = Paths.get("data\\" + LocalDateTime.now().toString().replace(":", " "));
    try (BufferedWriter writer = Files.newBufferedWriter(p);) {
      String data = new ObjectMapper().writeValueAsString(questionAndAnswers);
      writer.write(data);
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
    }
  }

  public synchronized void dictateText(String text) throws ExamHelperException {
    speechGenerator.dictateText(text);
  }

  private void copyToClipBoard(String text) {
    Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
    StringSelection selection = new StringSelection(text);
    clip.setContents(selection, selection);
  }
}
