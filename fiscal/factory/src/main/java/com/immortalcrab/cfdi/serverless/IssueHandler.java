package com.immortalcrab.cfdi.serverless;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;

import lombok.extern.log4j.Log4j;

@Log4j
public class IssueHandler implements RequestHandler<SQSEvent, Void> {

    @Override
    public Void handleRequest(SQSEvent event, Context context) {

        try {

            event.getRecords().forEach(msg -> {

                log.info(msg.getBody());
                // Here we shall call 
                // doIssue(final String kind, InputStreamReader isr)
            });
        } catch (Exception ex) {
            log.error("Exception handling batch seed request.", ex);
            throw ex;
        }
        return null;
    }
}
