package com.immortalcrab.cfdi.processor;

import com.immortalcrab.cfdi.errors.EngineError;
import com.immortalcrab.cfdi.helpers.AWSEvent;
import com.immortalcrab.cfdi.helpers.Payload;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.arn.Arn;
import com.amazonaws.services.lambda.runtime.events.SQSBatchResponse;
import com.immortalcrab.cfdi.errors.ErrorCodes;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class IssueHandler implements RequestHandler<SQSEvent, SQSBatchResponse> {

    @Override
    public SQSBatchResponse handleRequest(SQSEvent event, Context context) {

        List<SQSBatchResponse.BatchItemFailure> failures = new ArrayList<>();
        event.getRecords().forEach(msg -> {
            try {
                handleMessage(Producer.obtainSteadyPipeline(), msg);
            } catch (EngineError ex) {
                final String emsg = String.format("Exception handling message with id %s", msg.getMessageId());
                log.error(emsg, ex);
                failures.add(new SQSBatchResponse.BatchItemFailure(msg.getMessageId()));
            }
        });

        return new SQSBatchResponse(failures);
    }

    private void handleMessage(Producer producer, SQSEvent.SQSMessage msg) throws EngineError {
        Arn eventSourceArn = Arn.fromString(msg.getEventSourceArn());
        log.info(String.format("We've got a message to process from queue %s", eventSourceArn.getResourceAsString()));
        var details = producer.doIssue(percolatePayload(msg));
        log.debug(String.format("Issue for %s is attained {%s}",
                details.getName(), details.getBuffer().toString()));
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
}
