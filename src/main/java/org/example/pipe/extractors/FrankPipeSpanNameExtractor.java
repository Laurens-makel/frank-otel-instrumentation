package org.example.pipe.extractors;

import io.opentelemetry.instrumentation.api.instrumenter.SpanNameExtractor;
import org.example.pipe.FrankPipeRequest;

public class FrankPipeSpanNameExtractor implements SpanNameExtractor<FrankPipeRequest> {

    @Override
    public String extract(FrankPipeRequest frankPipeRequest) {
        return "Pipe ["+frankPipeRequest.getPipeName()+"]";
    }

}
