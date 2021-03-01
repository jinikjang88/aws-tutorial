package com.tutorial.aws.sqs.controller;

import com.tutorial.aws.sqs.service.ReceiveMessage;
import com.tutorial.aws.sqs.service.SqsService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SqsController {

  private final SqsService sqsService;

  public SqsController(SqsService sqsService) {
    this.sqsService = sqsService;
  }

  @PostMapping("/sqs/queue")
  public String createQueue(final @RequestParam("qName") String qName) {
    return sqsService.createQueue(qName);
  }

  @GetMapping("/sqs/queue")
  public List<String> getQueues(final @RequestParam("qName") String qName) {
    return sqsService.listQueue(qName);
  }

  @PostMapping("/sqs/queue/message")
  public String sendMessage(
      final @RequestParam("qName") String qName,
      final @RequestParam("message") String message) {
    return sqsService.sendMessage(qName, message);
  }

  @GetMapping("/sqs/queue/message")
  public List<ReceiveMessage> receiveMessage(
      final @RequestParam("qName") String qName) {

    return sqsService.receiveMessages(qName);
  }
}
