package com.immortalcrab.cfdi.processor;

import java.io.IOException;
import com.immortalcrab.cfdi.errors.EngineError;
import com.immortalcrab.cfdi.helpers.AWSEvent;
import com.immortalcrab.cfdi.helpers.Payload;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.immortalcrab.cfdi.errors.ErrorCodes;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class IssueHandler implements RequestHandler<SQSEvent, Void> {

    @Override
    public Void handleRequest(SQSEvent event, Context context) {

        try {
            for (SQSEvent.SQSMessage msg : event.getRecords()) {
                Producer.obtainSteadyPipeline().doIssue(percolatePayload(msg));
            }
        } catch (EngineError ex) {
            log.error("Exception handling batch seed request.", ex);
        }

        return null;
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
