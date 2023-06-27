package org.example.common;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import io.opentelemetry.instrumentation.api.instrumenter.InstrumenterBuilder;
import nl.nn.adapterframework.core.*;
import nl.nn.adapterframework.parameters.Parameter;
import nl.nn.adapterframework.parameters.ParameterValueList;
import nl.nn.adapterframework.processors.CorePipeLineProcessor;
import nl.nn.adapterframework.processors.InputOutputPipeProcessor;
import nl.nn.adapterframework.stream.Message;

import java.util.HashMap;
import java.util.Map;

public class FrankSingletons {

    public enum FrankClasses {
        MESSAGE("nl.nn.adapterframework.stream.Message"),
        PARAMETER("nl.nn.adapterframework.parameters.Parameter"),
        PARAMETER_VALUE_LIST("nl.nn.adapterframework.parameters.ParameterValueList"),
        PIPE("nl.nn.adapterframework.core.IPipe"),
        PIPE_PROCESSOR("nl.nn.adapterframework.processors.InputOutputPipeProcessor"),
        PIPELINE("nl.nn.adapterframework.core.PipeLine"),
        PIPELINE_PROCESSOR("nl.nn.adapterframework.processors.CorePipeLineProcessor"),
        PIPELINE_SESSION("nl.nn.adapterframework.core.PipeLineSession"),
        SENDER("nl.nn.adapterframework.core.ISender"),
        STRING("java.lang.String");

        private String className;

        FrankClasses(String className) {
            this.className = className;
        }

        public String className() {
            return className;
        }
    }

    /* EXITS */
    public static final String FRANK_EXIT_STATE_KEY = "frank.exit.state";
    public static final String FRANK_EXIT_CODE_KEY = "frank.exit.code";
    public static boolean TAG_EXITS = getBooleanProperty("frank.instrumentation.exits", "true");

    /* FORWARDS */
    public static final String FRANK_FORWARD_NAME_KEY = "frank.forward.name";
    public static final String FRANK_FORWARD_PATH_KEY = "frank.forward.path";
    public static boolean TAG_FORWARDS = getBooleanProperty("frank.instrumentation.forwards", "true");

    /* INSTRUMENTATIONS */
    public static final String PIPELINE_INSTRUMENTATION_NAME = "frank-framework-pipeline-instrumentation";
    private static final String PIPELINE_INSTRUMENTATION_PROPERTY = "frank.instrumentation.pipeline";

    public static final String PIPE_INSTRUMENTATION_NAME = "frank-framework-pipes-instrumentation";
    private static final String PIPE_INSTRUMENTATION_PROPERTY = "frank.instrumentation.pipes";

    public static final String PARAMETER_INSTRUMENTATION_NAME = "frank-framework-parameters-instrumentation";
    private static final String PARAMETER_INSTRUMENTATION_PROPERTY = "frank.instrumentation.parameter";

    public static final String SENDER_INSTRUMENTATION_NAME = "frank-framework-sender-instrumentation";
    private static final String SENDER_INSTRUMENTATION_PROPERTY = "frank.instrumentation.senders";

    private static final Map<String, Instrumenter<FrankRequest, INamedObject>> instrumentations = new HashMap<>();
    static {
        instrumentations.put(
            SENDER_INSTRUMENTATION_NAME,
            build(SENDER_INSTRUMENTATION_NAME, getBooleanProperty(SENDER_INSTRUMENTATION_PROPERTY, "true"))
        );
        instrumentations.put(
            PIPELINE_INSTRUMENTATION_NAME,
            buildClientInstrumenter(PIPELINE_INSTRUMENTATION_NAME, getBooleanProperty(PIPELINE_INSTRUMENTATION_PROPERTY, "true"))
        );
        instrumentations.put(
            PIPE_INSTRUMENTATION_NAME,
            buildClientInstrumenter(PIPE_INSTRUMENTATION_NAME, getBooleanProperty(PIPE_INSTRUMENTATION_PROPERTY, "true"))
        );
    }

    public static final Instrumenter instrumenter(String name){
        return instrumentations.get(name);
    }

    private static boolean getBooleanProperty(String key, String defaultValue){
        return Boolean.parseBoolean(System.getProperty(key, defaultValue));
    }
    private static InstrumenterBuilder<FrankRequest, INamedObject> base(String instrumentationName, boolean instrumentationEnabled){
        return Instrumenter.<FrankRequest, INamedObject> builder(
                        GlobalOpenTelemetry.get(),
                        instrumentationName,
                        new FrankExtractor<FrankRequest, INamedObject>()
                )
                .setEnabled(instrumentationEnabled)
                .setSpanStatusExtractor(new FrankExtractor())
                .addAttributesExtractor(new FrankExtractor());
    }
    private static Instrumenter<FrankRequest, INamedObject> build(String instrumentationName, boolean instrumentationEnabled){
        return base(instrumentationName, instrumentationEnabled).buildInstrumenter();
    }
    private static Instrumenter<FrankRequest, INamedObject> buildClientInstrumenter(String instrumentationName, boolean instrumentationEnabled){
        return base(instrumentationName, instrumentationEnabled).buildClientInstrumenter(FrankRequest::setPropagationSessionKeys);
    }
}
