package com.goglotek.examhelper.exception;

public class ExamHelperException extends Exception {

  public ExamHelperException(String message) {
    super(message);
  }

  public ExamHelperException(Exception e, String message) {
    super(message, e);
  }

}
