package com.immortalcrab.nominator.serverless;

import java.nio.charset.StandardCharsets;
import java.util.Map;

abstract class DocMaker {

	static final String PDF_MIME_TYPE = "application/pdf";

	private Map<String, String> setupHeaders(final String fName) {

		return Map.of(
			"Content-disposition", String.format("attachment; filename=%s", fName),
			"Content-Type", PDF_MIME_TYPE,
			"Accept", PDF_MIME_TYPE
		);
	}

	private Map<String, Object> setupResponse(Map<String, String> headers, final byte[] docEncB64) {

		final String body = new String(docEncB64, StandardCharsets.UTF_8);

		return Map.of(
			"statusCode", 200,
			"isBase64Encoded", true,
			"body", body,
			"headers", headers
		);
	}

}
