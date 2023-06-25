package org.example.parameter;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import org.example.common.FrankExtractor;
import org.example.common.FrankRequest;
import org.example.common.FrankSingletons;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;

@AutoService(InstrumentationModule.class)
public class FrankParameterInstrumentationModule extends InstrumentationModule {

    public FrankParameterInstrumentationModule() {
        super("frank-framework-parameters", "frank-framework-parameters-7.7");
    }

    @Override
    public List<TypeInstrumentation> typeInstrumentations() {
        return singletonList(new FrankParameterInstrumentation());
    }

    @Override
    public List<String> getAdditionalHelperClassNames() {
        List<String> helpers = new ArrayList<>();
        helpers.add(FrankSingletons.class.getName());
        helpers.add(FrankExtractor.class.getName());
        helpers.add(FrankRequest.class.getName());
        return helpers;
    }
}
