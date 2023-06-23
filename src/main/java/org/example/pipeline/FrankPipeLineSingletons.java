package org.example.pipeline;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import nl.nn.adapterframework.core.PipeLineResult;
import org.example.common.FrankExtractor;

public class FrankPipeLineSingletons {
    private static final String INSTRUMENTATION_NAME = "frank-framework-pipes-instrumentation";
    private static final Instrumenter<FrankPipeLineRequest, PipeLineResult> INSTRUMENTER;

    public static final String FRANK_EXIT_STATE_KEY = "frank.exit.state";
    public static final String FRANK_EXIT_CODE_KEY = "frank.exit.code";
    public static boolean TAG_EXITS = Boolean.parseBoolean(System.getProperty("frank.instrumentation.exits", "true"));

    static {
        INSTRUMENTER = Instrumenter.<FrankPipeLineRequest, PipeLineResult> builder(
                                GlobalOpenTelemetry.get(),
                                INSTRUMENTATION_NAME,
                                new FrankExtractor<FrankPipeLineRequest, PipeLineResult>()
                        )
                        .setSpanStatusExtractor(new FrankExtractor<FrankPipeLineRequest, PipeLineResult>())
                        .addAttributesExtractor(new FrankExtractor<FrankPipeLineRequest, PipeLineResult>())
                        .buildClientInstrumenter(FrankPipeLineRequest::setSessionKey);
    }

    public static Instrumenter<FrankPipeLineRequest, PipeLineResult> instrumenter() {
        return INSTRUMENTER;
    }
}
