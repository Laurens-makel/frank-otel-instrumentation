package org.example.sender;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import nl.nn.adapterframework.core.ISender;
import nl.nn.adapterframework.stream.Message;
import org.example.common.FrankExtractor;
import org.example.common.FrankRequest;

public class FrankSenderSingletons {
    private static final String INSTRUMENTATION_NAME = "frank-framework-sender-instrumentation";
    private static final boolean INSTRUMENTATION_ENABLED = System.getProperty("frank.instrumentation.senders", "true").equals("true");
    private static final Instrumenter<FrankSenderRequest, Message> INSTRUMENTER;

    static {
        INSTRUMENTER = Instrumenter.<FrankSenderRequest, Message> builder(
                                GlobalOpenTelemetry.get(),
                                INSTRUMENTATION_NAME,
                                new FrankExtractor<FrankRequest<ISender>, Message>()
                        )
                        .setEnabled(INSTRUMENTATION_ENABLED)
                        .setSpanStatusExtractor(new FrankExtractor<FrankRequest<ISender>, Message>())
                        .addAttributesExtractor(new FrankExtractor<FrankRequest<ISender>, Message>())
                        .buildInstrumenter();
    }

    public static Instrumenter<FrankSenderRequest, Message> instrumenter() {
        return INSTRUMENTER;
    }
}
