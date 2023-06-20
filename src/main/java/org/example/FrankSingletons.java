package org.example;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import nl.nn.adapterframework.core.PipeRunResult;
import org.example.extractors.FrankPipeAttributeExtractor;
import org.example.extractors.FrankPipeSpanNameExtractor;
import org.example.extractors.FrankPipeStatusExtractor;

public class FrankSingletons {
    private static final String INSTRUMENTATION_NAME = "io.opentelemetry.apache-httpclient-4.0";
    private static final Instrumenter<FrankPipeRequest, PipeRunResult> INSTRUMENTER;

    static {
        INSTRUMENTER = Instrumenter.<FrankPipeRequest, PipeRunResult> builder(
                                GlobalOpenTelemetry.get(),
                                INSTRUMENTATION_NAME,
                                new FrankPipeSpanNameExtractor()
                        )
                        .setSpanStatusExtractor(new FrankPipeStatusExtractor())
                        .addAttributesExtractor(new FrankPipeAttributeExtractor())
                        .buildClientInstrumenter(FrankPipeRequest::setSessionKey);
    }

    public static Instrumenter<FrankPipeRequest, PipeRunResult> instrumenter() {
        return INSTRUMENTER;
    }
}
