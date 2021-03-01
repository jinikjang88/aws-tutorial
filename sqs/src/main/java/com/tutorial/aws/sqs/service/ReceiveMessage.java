package com.tutorial.aws.sqs.service;

import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReceiveMessage implements Serializable {

  private String MessageId;
  private String ReceiptHandle;
  private String Body;

  @Builder
  public ReceiveMessage(String messageId, String receiptHandle, String body) {
    MessageId = messageId;
    ReceiptHandle = receiptHandle;
    Body = body;
  }
}
