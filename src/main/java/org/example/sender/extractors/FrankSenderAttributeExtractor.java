package org.example.sender.extractors;

import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.context.Context;
import io.opentelemetry.instrumentation.api.instrumenter.AttributesExtractor;
import nl.nn.adapterframework.stream.Message;
import org.example.sender.FrankSenderRequest;

import javax.annotation.Nullable;

public class FrankSenderAttributeExtractor implements AttributesExtractor<FrankSenderRequest, Message> {

    @Override
    public void onStart(AttributesBuilder attributesBuilder, Context context, FrankSenderRequest frankSenderRequest) {
        attributesBuilder.put("pipe.type", frankSenderRequest.getSenderType());
    }

    @Override
    public void onEnd(AttributesBuilder attributesBuilder, Context context, FrankSenderRequest frankPipeRequest, @Nullable Message pipeRunResult, @Nullable Throwable throwable) {

    }

}
