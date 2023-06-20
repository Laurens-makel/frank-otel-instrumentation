package org.example.extractors;

import io.opentelemetry.instrumentation.api.instrumenter.SpanNameExtractor;
import org.example.FrankPipeRequest;

public class FrankPipeSpanNameExtractor implements SpanNameExtractor<FrankPipeRequest> {

    @Override
    public String extract(FrankPipeRequest frankPipeRequest) {
        return "Pipe ["+frankPipeRequest.getPipeName()+"]";
    }

}
