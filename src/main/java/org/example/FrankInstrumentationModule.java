package org.example;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;

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
}
