package com.goglotek.examhelper.ai;

import com.goglotek.examhelper.exception.ExamHelperException;

public interface AI {

  public String getAnswer(String question) throws ExamHelperException;
}
