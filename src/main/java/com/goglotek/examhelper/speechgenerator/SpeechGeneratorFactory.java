package com.goglotek.examhelper.speechgenerator;

public class SpeechGeneratorFactory {

  private SpeechGeneratorFactory() {
  }

  public static SpeechGenerator getDefaultSpeechGenerator() {
    String speechGenerator = System.getProperty("default-speech-generator");
    switch (speechGenerator.toLowerCase()) {
      case "googletts":
        return GoogleTTS.newGoogleTTS();
      default:
        return FreeTTS.newFreeTTS();
    }
  }
}
