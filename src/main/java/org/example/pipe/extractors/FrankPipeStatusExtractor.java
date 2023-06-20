package org.example.pipe.extractors;

import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.instrumentation.api.instrumenter.SpanStatusBuilder;
import io.opentelemetry.instrumentation.api.instrumenter.SpanStatusExtractor;
import nl.nn.adapterframework.core.PipeRunResult;
import org.example.pipe.FrankPipeRequest;

import javax.annotation.Nullable;

public class FrankPipeStatusExtractor implements SpanStatusExtractor<FrankPipeRequest, PipeRunResult> {

    @Override
    public void extract(SpanStatusBuilder spanStatusBuilder, FrankPipeRequest frankPipeRequest, @Nullable PipeRunResult pipeRunResult, @Nullable Throwable throwable) {
        if(throwable!=null){
            spanStatusBuilder.setStatus(StatusCode.ERROR, throwable.getMessage());
        }
    }
}
