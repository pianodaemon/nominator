package com.immortalcrab.cfdi.processor;

import java.io.IOException;
import com.immortalcrab.cfdi.errors.EngineError;
import com.immortalcrab.cfdi.helpers.AWSEvent;
import com.immortalcrab.cfdi.helpers.Payload;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.regions.Region;
import com.amazonaws.arn.Arn;
import com.immortalcrab.cfdi.errors.ErrorCodes;

import lombok.extern.log4j.Log4j2;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.sqs.model.QueueDoesNotExistException;

@Log4j2
public class IssueHandler implements RequestHandler<SQSEvent, Void> {
    
    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        
        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            try {
                handleMessage(Producer.obtainSteadyPipeline(), msg);
            } catch (EngineError ex) {
                log.error("Exception handling batch seed request.", ex);
            }
        }
        
        return null;
    }
    
    private void handleMessage(Producer producer, SQSEvent.SQSMessage msg) throws EngineError {
        Arn eventSourceArn = Arn.fromString(msg.getEventSourceArn());
        log.info(String.format("We've got a message to process from queue %s", eventSourceArn.getResourceAsString()));
        var details = producer.doIssue(percolatePayload(msg));
        log.debug(String.format("Issue for %s is attained {%s}",
                details.getName(), details.getBuffer().toString()));
        erradicateMessageFromQueue(eventSourceArn, msg.getReceiptHandle());
    }
    
    private Payload percolatePayload(SQSEvent.SQSMessage msg) throws EngineError {
        
        log.debug(msg.getBody());
        
        AWSEvent<Payload> body;
        try {
            body = AWSEvent.unmarshallEvent(msg.getBody(), Payload.class);
            log.info(body.toString());
            return body.getDetail();
        } catch (IOException ex) {
            throw new EngineError("Event could not be unmarshall", ex, ErrorCodes.REQUEST_INVALID);
        }
    }
    
    private static void erradicateMessageFromQueue(Arn eventSourceArn, final String receiptHandle) throws QueueDoesNotExistException {
        
        var queueName = eventSourceArn.getResourceAsString();
        Region queueRegion = Region.of(eventSourceArn.getRegion());
        SqsClient sqsClient = SqsClient.builder().region(queueRegion).credentialsProvider(ProfileCredentialsProvider.create()).build();
        
        log.debug(String.format("Attemping to delete message from queue %s in region %s", queueName, queueRegion.toString()));
        GetQueueUrlResponse getQueueUrlResponse = sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build());
        
        DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
                .queueUrl(getQueueUrlResponse.queueUrl())
                .receiptHandle(receiptHandle)
                .build();
        sqsClient.deleteMessage(deleteMessageRequest);
    }
}
