package org.example.pipeline;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import org.example.common.FrankExtractor;
import org.example.common.FrankRequest;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;

@AutoService(InstrumentationModule.class)
public class FrankPipeLineInstrumentationModule extends InstrumentationModule {

    public FrankPipeLineInstrumentationModule() {
        super("frank-framework-pipeline", "frank-framework-pipeline-7.7");
    }

    @Override
    public List<TypeInstrumentation> typeInstrumentations() {
        return singletonList(new FrankPipeLineInstrumentation());
    }

    @Override
    public List<String> getAdditionalHelperClassNames() {
        List<String> helpers = new ArrayList<>();
        helpers.add(FrankPipeLineSingletons.class.getName());
        helpers.add(FrankPipeLineRequest.class.getName());
        helpers.add(FrankExtractor.class.getName());
        helpers.add(FrankRequest.class.getName());
        return helpers;
    }
}
