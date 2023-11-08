package com.goglotek.examhelper.speechgenerator;

import com.goglotek.examhelper.exception.ExamHelperException;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FreeTTS implements SpeechGenerator {

  private final Logger logger = LoggerFactory.getLogger(FreeTTS.class);

  private FreeTTS() {
  }

  public static FreeTTS newFreeTTS() {
    return new FreeTTS();
  }

  @Override
  public void dictateText(String text) throws ExamHelperException {
    // checks if dictation is enabled. If not then it prints text to console.
    if (!Boolean.parseBoolean(System.getProperty("enable-answer-dictation"))) {
      logger.info(text);
      return;
    }

    if (text == null || text.isEmpty()) {
      text = System.getProperty("error-dictation-text");
    }

    System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us" + ".cmu_us_kal.KevinVoiceDirectory");

    VoiceManager voiceManager = VoiceManager.getInstance();
    Voice voice = voiceManager.getVoice("kevin16");

    if (voice == null) {
      throw new ExamHelperException("Kevin16 voice not found...");
    }

    voice.allocate();
    voice.speak(text);
    voice.deallocate();
  }
}
