package com.goglotek.examhelper.ocr;

import com.goglotek.examhelper.exception.ExamHelperException;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.awt.image.BufferedImage;

public class TesseractOCR implements OCR {

  private final ITesseract tesseract;

  private TesseractOCR(Tesseract tesseract) {

    this.tesseract = tesseract;
  }

  public static OCR newTesseractOCR() {
    return new TesseractOCR(new Tesseract());
  }

  @Override
  public String textFromImage(BufferedImage image) throws ExamHelperException {
    try {
      tesseract.setLanguage("eng");

      tesseract.setDatapath(System.getProperty("tesseract-data-path"));

      return tesseract.doOCR(image);
    } catch (TesseractException e) {
      throw new ExamHelperException(e, e.getMessage());
    }
  }
}
