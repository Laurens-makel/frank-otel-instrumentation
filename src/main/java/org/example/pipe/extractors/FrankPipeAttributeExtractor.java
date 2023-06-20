package org.example.pipe.extractors;

import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.context.Context;
import io.opentelemetry.instrumentation.api.instrumenter.AttributesExtractor;
import nl.nn.adapterframework.core.PipeRunResult;
import org.example.pipe.FrankPipeRequest;

import javax.annotation.Nullable;

public class FrankPipeAttributeExtractor implements AttributesExtractor<FrankPipeRequest, PipeRunResult> {

    @Override
    public void onStart(AttributesBuilder attributesBuilder, Context context, FrankPipeRequest frankPipeRequest) {
        attributesBuilder.put("pipe.type", frankPipeRequest.getPipeType());
    }

    @Override
    public void onEnd(AttributesBuilder attributesBuilder, Context context, FrankPipeRequest frankPipeRequest, @Nullable PipeRunResult pipeRunResult, @Nullable Throwable throwable) {

    }

}
