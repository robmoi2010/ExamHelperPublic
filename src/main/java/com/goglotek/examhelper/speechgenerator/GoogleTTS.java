package com.goglotek.examhelper.speechgenerator;

public class GoogleTTS implements SpeechGenerator {

  private GoogleTTS() {
  }

  public static SpeechGenerator newGoogleTTS() {
    return new GoogleTTS();
  }

  @Override
  public void dictateText(String text) {

  }
}
