package org.example.parameter;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import nl.nn.adapterframework.core.*;
import nl.nn.adapterframework.parameters.Parameter;
import nl.nn.adapterframework.parameters.ParameterValueList;
import nl.nn.adapterframework.stream.Message;

import  io.opentelemetry.api.common.Attributes;

import static net.bytebuddy.matcher.ElementMatchers.*;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;

public class FrankParameterInstrumentation implements TypeInstrumentation {

    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return named("nl.nn.adapterframework.parameters.Parameter");
    }

    @Override
    public void transform(TypeTransformer transformer) {
        transformer.applyAdviceToMethod(
                isMethod()
                        .and(named("getValue"))
                        .and(not(isAbstract()))
                        .and(takesArguments(4))
                        .and(takesArgument(0, named("nl.nn.adapterframework.parameters.ParameterValueList")))
                        .and(takesArgument(1, named("nl.nn.adapterframework.stream.Message")))
                        .and(takesArgument(2, named("nl.nn.adapterframework.core.PipeLineSession")))
                        .and(takesArgument(3, named("boolean")))
                ,this.getClass().getName() + "$ParameterResolvedAdvice");
    }

    @SuppressWarnings("unused")
    public static class ParameterResolvedAdvice {

        @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
        public static void methodExit(
                @Advice.Argument(3) boolean namespaceAware,
                @Advice.Argument(2) PipeLineSession session,
                @Advice.Argument(1) Message message,
                @Advice.Argument(0) ParameterValueList pvl,
                @Advice.This Parameter parameter,
                @Advice.Return Object result,
                @Advice.Thrown Throwable throwable,
                @Advice.Local("otelContext") Context context,
                @Advice.Local("otelScope") Scope scope) {
            Span.current().addEvent("Parameter Resolved", Attributes.builder()
                    .put(parameter.getName(), result.toString())
                    .build());
        }
    }
}
