package org.example.sender.extractors;

import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.instrumentation.api.instrumenter.SpanStatusBuilder;
import io.opentelemetry.instrumentation.api.instrumenter.SpanStatusExtractor;
import nl.nn.adapterframework.core.PipeRunResult;
import nl.nn.adapterframework.stream.Message;
import org.example.sender.FrankSenderRequest;

import javax.annotation.Nullable;

public class FrankSenderStatusExtractor implements SpanStatusExtractor<FrankSenderRequest, Message> {

    @Override
    public void extract(SpanStatusBuilder spanStatusBuilder, FrankSenderRequest frankSenderRequest, @Nullable Message message, @Nullable Throwable throwable) {
        if(throwable!=null){
            spanStatusBuilder.setStatus(StatusCode.ERROR, throwable.getMessage());
        }
    }
}
