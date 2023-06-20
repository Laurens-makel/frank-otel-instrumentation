package org.example.sender;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import org.example.sender.extractors.FrankSenderAttributeExtractor;
import org.example.sender.extractors.FrankSenderSpanNameExtractor;
import org.example.sender.extractors.FrankSenderStatusExtractor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;
@AutoService(InstrumentationModule.class)
public class FrankSenderInstrumentationModule extends InstrumentationModule {

    public FrankSenderInstrumentationModule() {
        super("frank-framework-senders", "frank-framework-senders-7.7");
    }

    @Override
    public List<TypeInstrumentation> typeInstrumentations() {
        return singletonList(new FrankSenderInstrumentation());
    }

    @Override
    public List<String> getAdditionalHelperClassNames() {
        List<String> helpers = new ArrayList<>();
        helpers.add(FrankSenderSingletons.class.getName());
        helpers.add(FrankSenderRequest.class.getName());
        helpers.add(FrankSenderAttributeExtractor.class.getName());
        helpers.add(FrankSenderSpanNameExtractor.class.getName());
        helpers.add(FrankSenderStatusExtractor.class.getName());
        return helpers;
    }
}
