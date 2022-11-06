package com.immortalcrab.cfdi.serverless;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;

import com.immortalcrab.cfdi.serverless.XmlProducer;

import lombok.extern.log4j.Log4j;

@Log4j
public class IssueHandler implements RequestHandler<SQSEvent, Void> {

    @Override
    public Void handleRequest(SQSEvent event, Context context) {

        event.getRecords().forEach(msg -> {

            log.info(msg.getBody());
        });
        return null;
    }
}
