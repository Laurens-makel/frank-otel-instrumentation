package org.example.sender;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import nl.nn.adapterframework.core.PipeRunResult;
import nl.nn.adapterframework.stream.Message;
import org.example.sender.extractors.FrankSenderAttributeExtractor;
import org.example.sender.extractors.FrankSenderSpanNameExtractor;
import org.example.sender.extractors.FrankSenderStatusExtractor;

public class FrankSenderSingletons {
    private static final String INSTRUMENTATION_NAME = "io.opentelemetry.apache-httpclient-4.0";
    private static final Instrumenter<FrankSenderRequest, Message> INSTRUMENTER;

    static {
        INSTRUMENTER = Instrumenter.<FrankSenderRequest, Message> builder(
                                GlobalOpenTelemetry.get(),
                                INSTRUMENTATION_NAME,
                                new FrankSenderSpanNameExtractor()
                        )
                        .setSpanStatusExtractor(new FrankSenderStatusExtractor())
                        .addAttributesExtractor(new FrankSenderAttributeExtractor())
                        .buildClientInstrumenter(FrankSenderRequest::setSessionKey);
    }

    public static Instrumenter<FrankSenderRequest, Message> instrumenter() {
        return INSTRUMENTER;
    }
}
