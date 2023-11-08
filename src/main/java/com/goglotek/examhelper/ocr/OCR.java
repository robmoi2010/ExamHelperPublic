package com.goglotek.examhelper.ocr;

import com.goglotek.examhelper.exception.ExamHelperException;

import java.awt.image.BufferedImage;

public interface OCR {

  public String textFromImage(BufferedImage image) throws ExamHelperException;
}
