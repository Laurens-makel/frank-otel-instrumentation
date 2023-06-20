package org.example;

import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import nl.nn.adapterframework.core.IPipe;
import nl.nn.adapterframework.core.PipeLine;
import nl.nn.adapterframework.core.PipeLineSession;
import nl.nn.adapterframework.core.PipeRunResult;
import nl.nn.adapterframework.stream.Message;

import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;

import static io.opentelemetry.javaagent.bootstrap.Java8BytecodeBridge.currentContext;
import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.implementsInterface;
import static net.bytebuddy.matcher.ElementMatchers.*;
import static org.example.FrankSingletons.instrumenter;

public class FrankInstrumentation implements TypeInstrumentation {

    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return implementsInterface(named("nl.nn.adapterframework.processors.PipeProcessor"));
    }

    @Override
    public void transform(TypeTransformer transformer) {
        transformer.applyAdviceToMethod(
                isMethod()
                        .and(named("processPipe"))
                        .and(not(isAbstract()))
                        .and(takesArguments(4))
                        .and(takesArgument(0, named("nl.nn.adapterframework.core.PipeLineSession")))
                        .and(takesArgument(1, named("nl.nn.adapterframework.core.IPipe")))
                        .and(takesArgument(2, named("nl.nn.adapterframework.stream.Message")))
                        .and(takesArgument(3, named("nl.nn.adapterframework.core.PipeLine")))

                ,this.getClass().getName() + "$PipeExecutionAdvice");
    }

    @SuppressWarnings("unused")
    public static class PipeExecutionAdvice {
        @Advice.OnMethodEnter(suppress = Throwable.class)
        public static void methodEnter(
                @Advice.Argument(0) PipeLineSession session,
                @Advice.Argument(1) IPipe pipe,
                @Advice.Argument(2) Message message,
                @Advice.Argument(3) PipeLine pipeLine,
                @Advice.Local("otelRequest") FrankPipeRequest otelRequest,
                @Advice.Local("otelContext") Context context,
                @Advice.Local("otelScope") Scope scope) {
            Context parentContext = currentContext();

            otelRequest = new FrankPipeRequest(pipe, message, session);

            if (!instrumenter().shouldStart(parentContext, otelRequest)) {
                return;
            }

            context = instrumenter().start(parentContext, otelRequest);
            scope = context.makeCurrent();
        }

        @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
        public static void methodExit(
                @Advice.Argument(0) PipeLineSession session,
                @Advice.Argument(1) IPipe pipe,
                @Advice.Argument(2) Message message,
                @Advice.Argument(3) PipeLine pipeLine,
                @Advice.Return PipeRunResult result,
                @Advice.Thrown Throwable throwable,
                @Advice.Local("otelRequest") FrankPipeRequest otelRequest,
                @Advice.Local("otelContext") Context context,
                @Advice.Local("otelScope") Scope scope) {
            if (scope == null) {
                return;
            }

            scope.close();
            if (throwable != null) {
                instrumenter().end(context, otelRequest, null, throwable);
            } else {
                instrumenter().end(context, otelRequest, result, null);
            }
        }
    }
}
