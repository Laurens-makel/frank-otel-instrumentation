package org.example.samplers;


import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.trace.data.LinkData;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import io.opentelemetry.sdk.trace.samplers.SamplingResult;
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes;

import java.util.List;

public class FrankApiSampler implements Sampler {

    @Override
    public SamplingResult shouldSample(Context context, String traceId, String name, SpanKind spanKind, Attributes attributes, List<LinkData> list) {
        String httpTarget = attributes.get(SemanticAttributes.HTTP_TARGET);
        if(httpTarget!=null && httpTarget.contains("/iaf/api/")){
            return SamplingResult.drop();
        }
        return SamplingResult.recordAndSample();
    }

    @Override
    public String getDescription() {
        return "Sampler that could be configured to drop all spans related o Frank Framework API traffic.";
    }
}
