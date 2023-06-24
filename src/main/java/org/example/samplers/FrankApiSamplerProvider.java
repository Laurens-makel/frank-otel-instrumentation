package org.example.samplers;

import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import io.opentelemetry.sdk.autoconfigure.spi.traces.ConfigurableSamplerProvider;
import io.opentelemetry.sdk.trace.samplers.Sampler;

public class FrankApiSamplerProvider implements ConfigurableSamplerProvider {

    @Override
    public Sampler createSampler(ConfigProperties configProperties) {
        return new FrankApiSampler();
    }

    @Override
    public String getName() {
        return "frank_framework_api_sampler";
    }

}
