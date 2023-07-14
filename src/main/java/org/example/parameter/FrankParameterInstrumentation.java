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
import org.example.common.FrankSingletons.FrankClasses;

import static net.bytebuddy.matcher.ElementMatchers.*;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;
import static org.example.common.FrankSingletons.PARAMETER_EVENTS;

public class FrankParameterInstrumentation implements TypeInstrumentation {

    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return named(FrankClasses.PARAMETER.className());
    }

    @Override
    public void transform(TypeTransformer transformer) {
        transformer.applyAdviceToMethod(
                isMethod()
                        .and(named("getValue"))
                        .and(not(isAbstract()))
                        .and(takesArguments(4))
                        .and(takesArgument(0, named(FrankClasses.PARAMETER_VALUE_LIST.className())))
                        .and(takesArgument(1, named(FrankClasses.MESSAGE.className())))
                        .and(takesArgument(2, named(FrankClasses.PIPELINE_SESSION.className())))
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
            if(PARAMETER_EVENTS){
                String name = parameter.getName();
                String value = parameter.isHidden() ? "****" : parameter.getValue();
                Span.current().setAttribute(name, value);
                Span.current().addEvent("Parameter Resolved", Attributes.builder()
                        .put(name, value)
                        .build());
            }

        }
    }
}
