package com.goglotek.examhelper.ai;

public class AIFactory {

  private AIFactory() {
  }

  public static AI getDefaultAI() {
    String ai = System.getProperty("default-ai");
    switch (ai.toLowerCase()) {
      case "bard":
        return BardClient.newBardClient();
      case "chatgpt":
        return ChatGPTClient.newChatGPTClient();
      case "gpt4all-client":
        return GPT4AllLocalClient.newGPT4AllLocalClient();
      default:
        return GPT4AllBinding.newGPT4AllBinding();
    }
  }
}
