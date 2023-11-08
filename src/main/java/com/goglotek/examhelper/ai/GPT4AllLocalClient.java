package com.goglotek.examhelper.ai;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goglotek.examhelper.exception.ExamHelperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Accesses local GTP4All server via http
 */
public class GPT4AllLocalClient extends WebClient implements AI {

  private final Logger logger = LoggerFactory.getLogger(GPT4AllLocalClient.class);

  static class RequestData {

    private String model;
    private String prompt;
    private float temperature;
    private int max_tokens;
    private float top_p;
    private int n;
    private boolean echo;
    private boolean stream;

    public String getModel() {
      return model;
    }

    public void setModel(String model) {
      this.model = model;
    }

    public String getPrompt() {
      return prompt;
    }

    public void setPrompt(String prompt) {
      this.prompt = prompt;
    }

    public float getTemperature() {
      return temperature;
    }

    public void setTemperature(float temperature) {
      this.temperature = temperature;
    }

    public int getMax_tokens() {
      return max_tokens;
    }

    public void setMax_tokens(int max_tokens) {
      this.max_tokens = max_tokens;
    }

    public float getTop_p() {
      return top_p;
    }

    public void setTop_p(float top_p) {
      this.top_p = top_p;
    }

    public int getN() {
      return n;
    }

    public void setN(int n) {
      this.n = n;
    }

    public boolean isEcho() {
      return echo;
    }

    public void setEcho(boolean echo) {
      this.echo = echo;
    }

    public boolean isStream() {
      return stream;
    }

    public void setStream(boolean stream) {
      this.stream = stream;
    }
  }

  static class Usage {

    private int prompt_tokens;
    private int completion_tokens;
    private int total_tokens;

    public int getPrompt_tokens() {
      return prompt_tokens;
    }

    public void setPrompt_tokens(int prompt_tokens) {
      this.prompt_tokens = prompt_tokens;
    }

    public int getCompletion_tokens() {
      return completion_tokens;
    }

    public void setCompletion_tokens(int completion_tokens) {
      this.completion_tokens = completion_tokens;
    }

    public int getTotal_tokens() {
      return total_tokens;
    }

    public void setTotal_tokens(int total_tokens) {
      this.total_tokens = total_tokens;
    }
  }

  static class Choices {

    private String finish_reason;
    private int index;
    private String logprobs;
    private String[] references;
    private String text;

    public String getFinish_reason() {
      return finish_reason;
    }

    public void setFinish_reason(String finish_reason) {
      this.finish_reason = finish_reason;
    }

    public int getIndex() {
      return index;
    }

    public void setIndex(int index) {
      this.index = index;
    }


    public String[] getReferences() {
      return references;
    }

    public void setReferences(String[] references) {
      this.references = references;
    }

    public String getLogprobs() {
      return logprobs;
    }

    public void setLogprobs(String logprobs) {
      this.logprobs = logprobs;
    }

    public String getText() {
      return text;
    }

    public void setText(String text) {
      this.text = text;
    }
  }

  static class ResponseData {

    private String id;
    private String object;
    private int created;
    private String model;
    private Usage usage;
    private Choices[] choices;

    public Choices[] getChoices() {
      return choices;
    }

    public void setChoices(Choices[] choices) {
      this.choices = choices;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getObject() {
      return object;
    }

    public void setObject(String object) {
      this.object = object;
    }

    public int getCreated() {
      return created;
    }

    public void setCreated(int created) {
      this.created = created;
    }

    public String getModel() {
      return model;
    }

    public void setModel(String model) {
      this.model = model;
    }

    public Usage getUsage() {
      return usage;
    }

    public void setUsage(Usage usage) {
      this.usage = usage;
    }
  }

  @Override
  public String getAnswer(String question) throws ExamHelperException {
    try {
      String model = System.getProperty("gpt4all-model");

      int maxTokens = Integer.parseInt(System.getProperty("gpt4all-max-tokens", "50"));

      float temperature = Float.parseFloat(System.getProperty("gpt4all-temperature", "0.5"));

      String url = System.getProperty("gpt4all-url");

      RequestData data = new RequestData();
      data.setEcho(false);
      data.setModel(model);
      data.setPrompt(question);
      data.setMax_tokens(maxTokens);
      data.setTop_p(0.9f);
      data.setN(1);
      data.setStream(false);
      data.setTemperature(temperature);
      String request = new ObjectMapper().writeValueAsString(data);

      logger.info(request);

      long startTime = System.currentTimeMillis();

      // Send request and get results immediately via getPOST. getPOST can be called at a later time to get the results.
      String response = sendPostAsync(url, request).getPOST();

      // sendPostAsync(url, request, (e) -> logger.info(e));
      long endTime = System.currentTimeMillis();

      logger.info("total response time for " + System.getProperty("gpt4all-model") + " model is "
          + (endTime - startTime) / (1000) + " seconds");

      if (response == null || response.isEmpty()) {
        return response;
      }

      logger.info(response);

      ResponseData rData = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL)
          .readValue(response, ResponseData.class);

      Choices[] choices = rData.getChoices();

      if (choices == null || choices.length == 0) {
        return null;
      }

      return choices[0].text;
    } catch (JsonProcessingException e) {
      throw new ExamHelperException(e, e.getMessage());
    }
  }

  public static GPT4AllLocalClient newGPT4AllLocalClient() {
    return new GPT4AllLocalClient();
  }
}
