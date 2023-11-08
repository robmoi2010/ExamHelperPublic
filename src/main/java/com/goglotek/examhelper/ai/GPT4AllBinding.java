package com.goglotek.examhelper.ai;

import com.fasterxml.jackson.core.io.schubfach.FloatToDecimal;
import com.goglotek.examhelper.exception.ExamHelperException;
import com.hexadevlabs.gpt4all.LLModel;
import com.hexadevlabs.gpt4all.LLModel.ChatCompletionResponse;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Access GTP4All models programmatically
 */
public class GPT4AllBinding implements AI {

  private final Logger logger = LoggerFactory.getLogger(GPT4AllBinding.class);

  @Override
  public String getAnswer(String question) throws ExamHelperException {
    String modelFileName = System.getProperty("gpt4all-model-filename");
    String modelsFilePath = System.getProperty("gpt4all-models-path");

    LLModel.LIBRARY_SEARCH_PATH = System.getProperty("gpt4all-lib-search-path");

    long startTime = System.currentTimeMillis();

    try (LLModel model = new LLModel(Path.of(modelsFilePath + modelFileName))) {
      LLModel.GenerationConfig config = LLModel.config()
          .withNPredict(Integer.parseInt(System.getProperty("gpt4all-max-tokens", "50")))
          .withTemp(Float.parseFloat(System.getProperty("gpt4all-temperature", "0.5")))
          .withNBatch(Integer.parseInt(System.getProperty("gtp4all-n_batch")))
          .withTopK(Integer.parseInt(System.getProperty("gpt4all-top_k")))
          .withTopP(Float.parseFloat(System.getProperty("gpt4all-top_p")))
          .withRepeatPenalty(Float.parseFloat(System.getProperty("gpt4all-repetition-penalty", "2")))
          .build();

      Map<String, String> messages = new HashMap<>();
      messages.put("role", "system");
      messages.put("content", question);

      logger.info(question);

      ChatCompletionResponse response = model.chatCompletion(
          new ArrayList<Map<String, String>>(Arrays.asList(messages)), config);

      String answer = "";
      if (response != null && response.choices != null) {
        answer = response.choices.get(0).get("content");
      }

      long endTime = System.currentTimeMillis();

      logger.info("Model " + modelFileName + " took " + (endTime - startTime) / 1000 + " seconds to complete");

      return answer;
    } catch (Exception e) {
      throw new ExamHelperException(e,
          e.getMessage());
    }
  }

  public static GPT4AllBinding newGPT4AllBinding() {
    return new GPT4AllBinding();
  }
}
