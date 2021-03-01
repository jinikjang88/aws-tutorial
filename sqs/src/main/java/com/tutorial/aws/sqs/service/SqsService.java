package com.tutorial.aws.sqs.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.ListQueuesRequest;
import software.amazon.awssdk.services.sqs.model.ListQueuesResponse;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
public class SqsService {

  private final SqsAsyncClient sqsAsyncClient;


  public SqsService(SqsAsyncClient sqsAsyncClient) {
    this.sqsAsyncClient = sqsAsyncClient;
  }

  public String createQueue(String qName) {
    System.out.println("Create Queue");

    try {
      CreateQueueRequest createQueueRequest = CreateQueueRequest.builder().queueName(qName).build();

      sqsAsyncClient.createQueue(createQueueRequest);

      System.out.println("return queue Url");

      GetQueueUrlRequest getQueueUrlRequest = GetQueueUrlRequest.builder().queueName(qName).build();
      CompletableFuture<GetQueueUrlResponse> getQueueUrlResponse = sqsAsyncClient.getQueueUrl(getQueueUrlRequest);

      return getQueueUrlResponse.get().queueUrl();

    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      throw new RuntimeException("Create Queue");
    }
  }

  public List<String> listQueue(String qName) {

    System.out.println("List Queues");

    try {
      ListQueuesRequest listQueuesRequest = ListQueuesRequest.builder().queueNamePrefix(qName).build();
      CompletableFuture<ListQueuesResponse> listQueuesResponse = sqsAsyncClient.listQueues(listQueuesRequest);

      listQueuesResponse.get().queueUrls().forEach(System.out::println);

      return listQueuesResponse.get().queueUrls();

    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      throw new RuntimeException("List Queues");
    }
  }

  public String sendMessage(String qName, String message) {

    System.out.println("Send Message");

    try {
      GetQueueUrlRequest getQueueUrlRequest = GetQueueUrlRequest.builder().queueName(qName).build();
      CompletableFuture<GetQueueUrlResponse> getQueueUrlResponse = sqsAsyncClient.getQueueUrl(getQueueUrlRequest);

      SendMessageRequest sendMessageRequest = SendMessageRequest
          .builder()
          .queueUrl(getQueueUrlResponse.get().queueUrl())
          .messageBody(message)
          .delaySeconds(5)
          .build();

      return sqsAsyncClient.sendMessage(sendMessageRequest).get().messageId();

    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      throw new RuntimeException("");
    }
  }

  public List<ReceiveMessage> receiveMessages(String qName) {

    System.out.println("Receive messages");

    try {
      GetQueueUrlRequest getQueueUrlRequest = GetQueueUrlRequest.builder().queueName(qName).build();
      CompletableFuture<GetQueueUrlResponse> getQueueUrlResponse = sqsAsyncClient.getQueueUrl(getQueueUrlRequest);

      String queueUrl = getQueueUrlResponse.get().queueUrl();

      ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
          .queueUrl(queueUrl)
          .maxNumberOfMessages(10)
//          .visibilityTimeout(30)
//          .waitTimeSeconds(10)
          .build();

      List<ReceiveMessage> receiveMessages = sqsAsyncClient.receiveMessage(receiveMessageRequest).get().messages()
          .stream()
          .map(message ->
              ReceiveMessage.builder().body(message.body())
                  .messageId(message.messageId())
                  .receiptHandle(message.receiptHandle()).build())
          .collect(Collectors.toList());

      receiveMessages.forEach(
          receiveMessage -> {
            System.out.println("Delete Messages : " + receiveMessage.getMessageId());
              sqsAsyncClient.deleteMessage(
                  DeleteMessageRequest.builder()
                      .receiptHandle(receiveMessage.getReceiptHandle())
                      .queueUrl(queueUrl)
                      .build());
          }
      );

      return receiveMessages;
// ??
//      return sqsAsyncClient.receiveMessage(receiveMessageRequest).get().messages();

    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      throw new RuntimeException("Receive messages");
    }
  }

  private void deleteMessage(String qName) {

  }
}
