package com.immortalcrab.cfdi.serverless;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class IssueHandler implements RequestHandler<SQSEvent, Void> {

    @Override
    public Void handleRequest(SQSEvent event, Context context) {

        try {

            for (SQSEvent.SQSMessage msg : event.getRecords()) {

                log.debug(msg.getBody());

                AWSEvent<Payload> body = AWSEvent.unmarshalEvent(msg.getBody(), Payload.class);

                log.info(body.toString());
            }

        } catch (Exception ex) {
            log.error("Exception handling batch seed request.", ex);
        }
        return null;
    }

}
