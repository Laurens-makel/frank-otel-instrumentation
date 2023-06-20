package org.example.pipe;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import nl.nn.adapterframework.core.IPipe;
import nl.nn.adapterframework.core.PipeRunResult;
import org.example.common.FrankExtractor;
import org.example.common.FrankRequest;

public class FrankPipeSingletons {
    private static final String INSTRUMENTATION_NAME = "frank-framework-pipes-instrumentation";
    private static final Instrumenter<FrankPipeRequest, PipeRunResult> INSTRUMENTER;

    static {
        INSTRUMENTER = Instrumenter.<FrankPipeRequest, PipeRunResult> builder(
                                GlobalOpenTelemetry.get(),
                                INSTRUMENTATION_NAME,
                                new FrankExtractor<FrankRequest<IPipe>, PipeRunResult>()
                        )
                        .setSpanStatusExtractor(new FrankExtractor<FrankRequest<IPipe>, PipeRunResult>())
                        .addAttributesExtractor(new FrankExtractor<FrankRequest<IPipe>, PipeRunResult>())
                        .buildClientInstrumenter(FrankPipeRequest::setSessionKey);
    }

    public static Instrumenter<FrankPipeRequest, PipeRunResult> instrumenter() {
        return INSTRUMENTER;
    }
}
