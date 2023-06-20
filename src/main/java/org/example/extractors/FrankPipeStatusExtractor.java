package org.example.extractors;

import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.instrumentation.api.instrumenter.SpanStatusBuilder;
import io.opentelemetry.instrumentation.api.instrumenter.SpanStatusExtractor;
import nl.nn.adapterframework.core.PipeRunResult;
import org.example.FrankPipeRequest;

import javax.annotation.Nullable;

public class FrankPipeStatusExtractor implements SpanStatusExtractor<FrankPipeRequest, PipeRunResult> {

    @Override
    public void extract(SpanStatusBuilder spanStatusBuilder, FrankPipeRequest frankPipeRequest, @Nullable PipeRunResult pipeRunResult, @Nullable Throwable throwable) {
        if(!pipeRunResult.isSuccessful()){
            spanStatusBuilder.setStatus(StatusCode.ERROR, throwable.getMessage());
        }
    }
}
