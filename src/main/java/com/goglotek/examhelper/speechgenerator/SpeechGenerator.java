package com.goglotek.examhelper.speechgenerator;

import com.goglotek.examhelper.exception.ExamHelperException;

public interface SpeechGenerator {

  public void dictateText(String text) throws ExamHelperException;
}
