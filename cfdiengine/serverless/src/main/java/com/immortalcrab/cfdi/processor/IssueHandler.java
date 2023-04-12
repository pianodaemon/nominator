package com.immortalcrab.cfdi.processor;

import java.io.IOException;
import com.immortalcrab.cfdi.errors.EngineError;
import com.immortalcrab.cfdi.helpers.AWSEvent;
import com.immortalcrab.cfdi.helpers.Payload;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.arn.Arn;
import com.immortalcrab.cfdi.errors.ErrorCodes;

import lombok.extern.log4j.Log4j2;

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
