package com.goglotek.examhelper.ai;

import com.goglotek.examhelper.exception.ExamHelperException;
import com.pkslow.ai.AIClient;
import com.pkslow.ai.GoogleBardClient;
import com.pkslow.ai.domain.Answer;

public class BardClient extends WebClient implements AI {

  @Override
  public String getAnswer(String question) throws ExamHelperException {
    AIClient client = new GoogleBardClient(System.getProperty("bard-key"));
    Answer answer = client.ask(question);
    return answer.getChosenAnswer();
  }

  public static BardClient newBardClient() {
    return new BardClient();
  }
}
