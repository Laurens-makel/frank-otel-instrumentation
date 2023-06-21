package org.example.common;

import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.context.Context;
import io.opentelemetry.instrumentation.api.instrumenter.AttributesExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.SpanNameExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.SpanStatusBuilder;
import io.opentelemetry.instrumentation.api.instrumenter.SpanStatusExtractor;

import javax.annotation.Nullable;

public class FrankExtractor<F extends FrankRequest, R> implements AttributesExtractor<F, R>, SpanNameExtractor<F>, SpanStatusExtractor<F, R> {

    public final static String FRANK_ELEMENT_TYPE_KEY = "frank.element.type";

    @Override
    public void onStart(AttributesBuilder attributesBuilder, Context context, F frankRequest) {
        attributesBuilder.put(FRANK_ELEMENT_TYPE_KEY, frankRequest.getFrankElementType());
    }

    @Override
    public void onEnd(AttributesBuilder attributesBuilder, Context context, F t, @Nullable R r, @Nullable Throwable throwable) {

    }

    @Override
    public String extract(F frankRequest) {
        return frankRequest.getFrankElementType()+" ["+frankRequest.getName()+"]";
    }

    @Override
    public void extract(SpanStatusBuilder spanStatusBuilder, F f, @Nullable R r, @Nullable Throwable throwable) {
        if(throwable!=null){
            spanStatusBuilder.setStatus(StatusCode.ERROR, throwable.getMessage());
        }
    }
}
