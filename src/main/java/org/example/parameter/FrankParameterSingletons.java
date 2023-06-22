package org.example.parameter;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import nl.nn.adapterframework.parameters.Parameter;
import nl.nn.adapterframework.stream.Message;
import org.example.common.FrankExtractor;
import org.example.common.FrankRequest;

public class FrankParameterSingletons {
    private static final String INSTRUMENTATION_NAME = "frank-framework-parameters-7.7.6";
    private static final Instrumenter<FrankRequest<Parameter>, Message> INSTRUMENTER;

    static {
        INSTRUMENTER = Instrumenter.<FrankRequest<Parameter>, Message> builder(
                        GlobalOpenTelemetry.get(),
                        INSTRUMENTATION_NAME,
                        new FrankExtractor<FrankRequest<Parameter>, Message>()
                )
                .setSpanStatusExtractor(new FrankExtractor<FrankRequest<Parameter>, Message>())
                .addAttributesExtractor(new FrankExtractor<FrankRequest<Parameter>, Message>())
                .buildInstrumenter();
    }

    public static Instrumenter<FrankRequest<Parameter>, Message> instrumenter() {
        return INSTRUMENTER;
    }
}
