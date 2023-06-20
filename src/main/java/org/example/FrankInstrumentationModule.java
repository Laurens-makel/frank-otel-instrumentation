package org.example;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import org.example.extractors.FrankPipeAttributeExtractor;
import org.example.extractors.FrankPipeSpanNameExtractor;
import org.example.extractors.FrankPipeStatusExtractor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonList;
@AutoService(InstrumentationModule.class)
public class FrankInstrumentationModule extends InstrumentationModule {

    public FrankInstrumentationModule() {
        super("frank-framework", "frank-framework-7.7");
    }

    @Override
    public List<TypeInstrumentation> typeInstrumentations() {
        return singletonList(new FrankInstrumentation());
    }

    @Override
    public List<String> getAdditionalHelperClassNames() {
        List<String> helpers = new ArrayList<>();
        helpers.add(FrankSingletons.class.getName());
        helpers.add(FrankPipeRequest.class.getName());
        helpers.add(FrankPipeAttributeExtractor.class.getName());
        helpers.add(FrankPipeSpanNameExtractor.class.getName());
        helpers.add(FrankPipeStatusExtractor.class.getName());
        return helpers;
    }
}
