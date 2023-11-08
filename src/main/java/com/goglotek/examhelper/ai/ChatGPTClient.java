package com.goglotek.examhelper.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goglotek.examhelper.exception.ExamHelperException;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatGPTClient extends WebClient implements AI {

  private final Logger logger = LoggerFactory.getLogger(ChatGPTClient.class);

  class Message {

    private String role;
    private String content;

    public String getRole() {
      return role;
    }

    public void setRole(String role) {
      this.role = role;
    }

    public String getContent() {
      return content;
    }

    public void setContent(String content) {
      this.content = content;
    }
  }

  class ChatGPTRequestData {

    private String model;
    private Message[] messages;
    private float temperature;

    public String getModel() {
      return model;
    }

    public void setModel(String model) {
      this.model = model;
    }

    public Message[] getMessages() {
      return messages;
    }

    public void setMessages(Message[] messages) {
      this.messages = messages;
    }

    public float getTemperature() {
      return temperature;
    }

    public void setTemperature(float temperature) {
      this.temperature = temperature;
    }
  }

  @Override
  public String getAnswer(String question) throws ExamHelperException {
    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");
    headers.put("Authorization", "Bearer " + System.getProperty("chatgpt-key"));

    ChatGPTRequestData requestData = new ChatGPTRequestData();
    Message message = new Message();
    message.setRole("user");
    message.setContent(question);
    requestData.setMessages(new Message[]{message});
    requestData.setModel(System.getProperty("chatgpt-model"));
    requestData.setTemperature(0.7f);

    String data = "";

    try {
      data = new ObjectMapper().writeValueAsString(requestData);
    } catch (JsonProcessingException e) {
      throw new ExamHelperException(e, e.getMessage());
    }

    String responseData = sendPost(System.getProperty("chatgpt-url"), data, headers);

    logger.info(responseData);

    return responseData;
  }

  public static ChatGPTClient newChatGPTClient() {
    return new ChatGPTClient();
  }
}
