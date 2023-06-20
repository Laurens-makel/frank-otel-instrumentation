package org.example.pipe;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import org.example.pipe.extractors.FrankPipeAttributeExtractor;
import org.example.pipe.extractors.FrankPipeSpanNameExtractor;
import org.example.pipe.extractors.FrankPipeStatusExtractor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;
@AutoService(InstrumentationModule.class)
public class FrankPipeInstrumentationModule extends InstrumentationModule {

    public FrankPipeInstrumentationModule() {
        super("frank-framework-pipes", "frank-framework-pipes-7.7");
    }

    @Override
    public List<TypeInstrumentation> typeInstrumentations() {
        return singletonList(new FrankPipeInstrumentation());
    }

    @Override
    public List<String> getAdditionalHelperClassNames() {
        List<String> helpers = new ArrayList<>();
        helpers.add(FrankPipeSingletons.class.getName());
        helpers.add(FrankPipeRequest.class.getName());
        helpers.add(FrankPipeAttributeExtractor.class.getName());
        helpers.add(FrankPipeSpanNameExtractor.class.getName());
        helpers.add(FrankPipeStatusExtractor.class.getName());
        return helpers;
    }
}
