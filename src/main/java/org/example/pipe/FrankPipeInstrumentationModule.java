package org.example.pipe;

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
        helpers.add(FrankSingletons.class.getName());
        helpers.add(FrankPipeRequest.class.getName());
        helpers.add(FrankExtractor.class.getName());
        helpers.add(FrankRequest.class.getName());
        return helpers;
    }
}
