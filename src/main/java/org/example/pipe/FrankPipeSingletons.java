package org.example.pipe;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import nl.nn.adapterframework.core.PipeRunResult;
import org.example.pipe.extractors.FrankPipeAttributeExtractor;
import org.example.pipe.extractors.FrankPipeSpanNameExtractor;
import org.example.pipe.extractors.FrankPipeStatusExtractor;

public class FrankPipeSingletons {
    private static final String INSTRUMENTATION_NAME = "frank-framework-pipes-instrumentation";
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
