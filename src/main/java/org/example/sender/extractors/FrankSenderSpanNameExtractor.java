package org.example.sender.extractors;

import io.opentelemetry.instrumentation.api.instrumenter.SpanNameExtractor;
import org.example.sender.FrankSenderRequest;

public class FrankSenderSpanNameExtractor implements SpanNameExtractor<FrankSenderRequest> {

    @Override
    public String extract(FrankSenderRequest frankSenderRequest) {
        return "Sender ["+frankSenderRequest.getSenderName()+"]";
    }

}
