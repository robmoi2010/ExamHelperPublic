package com.goglotek.examhelper.ai;

import com.goglotek.examhelper.exception.ExamHelperException;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class WebClient {

  private final int timeout = Integer.parseInt(System.getProperty("http-client-timeout-seconds", "60"));
  private OkHttpClient client;
  private ExecutorService executorService = Executors.newCachedThreadPool();
  private Future<String> postFuture;
  private Future<String> getFuture;

  private final Logger logger = LoggerFactory.getLogger(WebClient.class);

  private void initializeHttpClient() {
    if (client == null) {
      this.client = new OkHttpClient.Builder().connectTimeout(timeout, TimeUnit.SECONDS)
          .callTimeout(timeout, TimeUnit.SECONDS)
          .readTimeout(timeout, TimeUnit.SECONDS).writeTimeout(timeout, TimeUnit.SECONDS).build();
    }
  }


  protected String sendGet(String url, Map<String, String> headers) throws ExamHelperException {
    initializeHttpClient();

    Request.Builder builder = new Request.Builder().url(url);

    if (headers != null) {
      headers.forEach((key, value) -> {
        builder.addHeader(key, value);
      });
    }

    Request request = builder.build();

    try (Response response = client.newCall(request).execute()) {
      return response.body().string();
    } catch (IOException e) {
      throw new ExamHelperException(e, e.getMessage());
    }
  }

  protected String sendGet(String url) throws ExamHelperException {
    return sendGet(url, null);
  }

  protected void sendGetAsync(String url, Consumer<String> consumer) throws ExamHelperException {
    executorService.execute(() -> {
      try {
        consumer.accept(sendGet(url));
      } catch (ExamHelperException e) {
        logger.error(e.getMessage(), e);
      }
    });
  }

  protected WebClient sendGetAsync(String url) {
    getFuture = executorService.submit(() -> {
      return sendGet(url);
    });

    return this;
  }


  protected String sendPost(String url, String data, Map<String, String> headers) throws ExamHelperException {
    initializeHttpClient();
    Request.Builder builder = new Request.Builder().url(url);
    builder.post(RequestBody.create(data.getBytes()));
    if (headers != null) {
      headers.forEach((key, value) -> {
        builder.addHeader(key, value);
      });
    }
    Request request = builder.build();
    try (Response response = client.newCall(request).execute()) {
      return response.body().string();
    } catch (IOException e) {
      throw new ExamHelperException(e, e.getMessage());
    }
  }

  protected String sendPost(String url, String data) throws ExamHelperException {
    return sendPost(url, data, null);
  }

  protected void sendPostAsync(String url, String data, Consumer<String> consumer) throws ExamHelperException {
    executorService.execute(() -> {
      try {
        consumer.accept(sendPost(url, data));
      } catch (ExamHelperException e) {
        logger.error(e.getMessage(), e);
      }
    });
  }

  protected WebClient sendPostAsync(String url, String data)
      throws ExamHelperException {
    postFuture = executorService.submit(() -> {
      return sendPost(url, data);
    });

    return this;
  }

  protected String getPOST() throws ExamHelperException {
    try {
      return postFuture.get();
    } catch (InterruptedException | ExecutionException e) {
      throw new ExamHelperException(e, e.getMessage());
    }
  }

  protected String getGET() throws ExamHelperException {
    try {
      return getFuture.get();
    } catch (InterruptedException | ExecutionException e) {
      throw new ExamHelperException(e, e.getMessage());
    }
  }
}
