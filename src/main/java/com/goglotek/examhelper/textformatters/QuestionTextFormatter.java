package com.goglotek.examhelper.textformatters;

import com.goglotek.examhelper.ai.AI;
import com.goglotek.examhelper.ai.BardClient;
import com.goglotek.examhelper.ai.ChatGPTClient;
import com.goglotek.examhelper.ai.GPT4AllBinding;
import com.goglotek.examhelper.ai.GPT4AllLocalClient;
import java.util.regex.Pattern;

public class QuestionTextFormatter implements TextFormatter {

  private final AI ai;

  private QuestionTextFormatter(AI ai) {
    this.ai = ai;
  }

  public static TextFormatter newQuestionTextFormatter(AI ai) {
    return new QuestionTextFormatter(ai);
  }

  @Override
  public String formatText(String text) {
    // Remove non-english characters
    text = text.replaceAll("[^\\w\\s.:(){}<>_\"\'?~@#$%^&*+;/\\[\\]=,!|-]", "").trim();

    text = addExamLanguageIfAbsent(text);

    String initMessage = "";

    if (BardClient.class.getName().equalsIgnoreCase(ai.getClass().getName())) {
      initMessage = System.getProperty("bard-init-message");
    } else if (ChatGPTClient.class.getName().equalsIgnoreCase(ai.getClass().getName())) {
      initMessage = System.getProperty("chatgpt-init-message");
    } else if (GPT4AllLocalClient.class.getName().equalsIgnoreCase(ai.getClass().getName())
        || GPT4AllBinding.class.getName()
        .equalsIgnoreCase(ai.getClass().getName())) {
      initMessage = System.getProperty("gpt4all-init-message");
    }

    text = initMessage + "\n" + text;

    return text;
  }

  private String addExamLanguageIfAbsent(String text) {
    String examLanguage = System.getProperty("exam-language").trim();

    boolean progLanguageInQuestion = Pattern.compile("\\b" + examLanguage + "\\b", Pattern.CASE_INSENSITIVE)
        .matcher(text)
        .find();

    if (!progLanguageInQuestion) {
      text = "In " + examLanguage + "\n" + text;
    }

    return text;
  }
}
