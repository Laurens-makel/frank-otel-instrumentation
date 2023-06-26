package org.example.common;

import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.context.Context;
import io.opentelemetry.instrumentation.api.instrumenter.AttributesExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.SpanNameExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.SpanStatusBuilder;
import io.opentelemetry.instrumentation.api.instrumenter.SpanStatusExtractor;

import javax.annotation.Nullable;

public class FrankExtractor<FRANK_ELEMENT_REQUEST extends FrankRequest, RESPONSE> implements SpanNameExtractor<FRANK_ELEMENT_REQUEST>,
            SpanStatusExtractor<FRANK_ELEMENT_REQUEST, RESPONSE>, AttributesExtractor<FRANK_ELEMENT_REQUEST, RESPONSE> {

    public final static String FRANK_ELEMENT_REQUEST_TYPE_KEY = "frank.element.type";

    @Override
    public void onStart(AttributesBuilder attributesBuilder, Context context, FRANK_ELEMENT_REQUEST frankRequest) {
        attributesBuilder.put(FRANK_ELEMENT_REQUEST_TYPE_KEY, frankRequest.getFrankElementType());
    }

    @Override
    public void onEnd(AttributesBuilder attributesBuilder, Context context, FRANK_ELEMENT_REQUEST t, @Nullable RESPONSE r, @Nullable Throwable throwable) {

    }

    @Override
    public String extract(FRANK_ELEMENT_REQUEST frankRequest) {
        return frankRequest.getFrankElementType()+" ["+frankRequest.getName()+"]";
    }

    @Override
    public void extract(SpanStatusBuilder spanStatusBuilder, FRANK_ELEMENT_REQUEST f, @Nullable RESPONSE r, @Nullable Throwable throwable) {
        if(throwable!=null){
            spanStatusBuilder.setStatus(StatusCode.ERROR, throwable.getMessage());
        }
    }
}
