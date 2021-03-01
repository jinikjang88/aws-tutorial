package com.tutorial.aws.sqs.config;

import java.net.URI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

/**
 * AwsSqsAsyncClient Config
 */
@Configuration
public class AwsSqsConfig {

  @Bean
  public SqsAsyncClient awsSqsAsyncClient() {
    return SqsAsyncClient.builder()
        .endpointOverride(URI.create("http://localhost:4576"))
        .region(Region.AP_NORTHEAST_2)
        .build();
  }
}
