package org.example.samplers;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.trace.data.LinkData;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import io.opentelemetry.sdk.trace.samplers.SamplingResult;
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes;

import java.util.ArrayList;
import java.util.List;

public class FrankApiSampler implements Sampler {

    private static final String DEFAULT_HTTP_TARGET_PATTERNS = "/iaf/api,/iaf/gui,/testtool";
    private static final List<String> httpTargetPatterns = new ArrayList<>();
    static {
        String[] configuredHttpPatterns = System.getProperty("frank.sampler.ignore.http", DEFAULT_HTTP_TARGET_PATTERNS).split(",");
        for(String httpPattern : configuredHttpPatterns){
            httpTargetPatterns.add(httpPattern.trim());
        }
    }

    @Override
    public SamplingResult shouldSample(Context context, String traceId, String name, SpanKind spanKind, Attributes attributes, List<LinkData> list) {
        String httpTarget = attributes.get(SemanticAttributes.HTTP_TARGET);
        if(httpTarget!=null){
            for(String pattern : httpTargetPatterns){
                if(httpTarget.contains(pattern)){
                    return SamplingResult.drop();
                }
            }
        }

        return SamplingResult.recordAndSample();
    }

    @Override
    public String getDescription() {
        return "Sampler that could be configured to drop all spans related o Frank Framework API traffic.";
    }
}
