package com.immortalcrab.nominator.serverless;

import java.io.InputStream;
import java.io.OutputStream;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

import lombok.extern.log4j.Log4j;

@Log4j
public class DocHandler implements RequestStreamHandler {

    public void handleRequest(InputStream iStream, OutputStream oStream, Context ctx) {

    }
}
