package com.goglotek.examhelper.textformatters;

import com.goglotek.examhelper.ai.AI;
import java.util.Objects;

public class AnswerTextFormatter implements TextFormatter {

  private final AI ai;

  private AnswerTextFormatter(AI ai) {
    this.ai = ai;
  }

  public static TextFormatter newAnswerTextFormatter(AI ai) {
    return new AnswerTextFormatter(ai);
  }

  @Override
  public String formatText(String text) {

    if (Objects.isNull(text)) {
      return "";
    }

    text = text.trim();

    return text;
  }
}
