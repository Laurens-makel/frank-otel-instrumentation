package org.example.pipeline;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import nl.nn.adapterframework.core.IAdapter;
import nl.nn.adapterframework.core.PipeLine;
import nl.nn.adapterframework.core.PipeLineResult;
import nl.nn.adapterframework.core.PipeLineSession;
import nl.nn.adapterframework.stream.Message;
import org.example.common.FrankRequest;
import org.example.common.FrankSingletons;

import static io.opentelemetry.javaagent.bootstrap.Java8BytecodeBridge.currentContext;
import static net.bytebuddy.matcher.ElementMatchers.*;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;
import static org.example.common.FrankSingletons.*;

public class FrankPipeLineInstrumentation implements TypeInstrumentation {

    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return named("nl.nn.adapterframework.processors.CorePipeLineProcessor");
    }

    @Override
    public void transform(TypeTransformer transformer) {
        transformer.applyAdviceToMethod(
                isMethod()
                        .and(named("processPipeLine"))
                        .and(not(isAbstract()))
                        .and(takesArguments(5))
                        .and(takesArgument(0, named("nl.nn.adapterframework.core.PipeLine")))
                        .and(takesArgument(1, named("java.lang.String")))
                        .and(takesArgument(2, named("nl.nn.adapterframework.stream.Message")))
                        .and(takesArgument(3, named("nl.nn.adapterframework.core.PipeLineSession")))
                        .and(takesArgument(4, named("java.lang.String")))

                ,this.getClass().getName() + "$PipeLineExecutionAdvice");
    }

    @SuppressWarnings("unused")
    public static class PipeLineExecutionAdvice {

        @Advice.OnMethodEnter(suppress = Throwable.class)
        public static void methodEnter(
                @Advice.Argument(4) String firstPipe,
                @Advice.Argument(3) PipeLineSession session,
                @Advice.Argument(2) Message message,
                @Advice.Argument(1) String messageId,
                @Advice.Argument(0) PipeLine pipeLine,
                @Advice.Local("otelRequest") FrankRequest<IAdapter> frankRequest,
                @Advice.Local("otelContext") Context context,
                @Advice.Local("otelScope") Scope scope) {
            frankRequest = new FrankRequest(message, session, pipeLine.getAdapter());
            Context parentContext = frankRequest.getParentContext();

            if (!instrumenter(FrankSingletons.PIPELINE_INSTRUMENTATION_NAME).shouldStart(parentContext, frankRequest)) {
                return;
            }

            context = instrumenter(FrankSingletons.PIPELINE_INSTRUMENTATION_NAME).start(parentContext, frankRequest);
            scope = context.makeCurrent();
        }

        @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
        public static void methodExit(
                @Advice.Argument(4) String firstPipe,
                @Advice.Argument(3) PipeLineSession session,
                @Advice.Argument(2) Message message,
                @Advice.Argument(1) String messageId,
                @Advice.Argument(0) PipeLine pipeLine,
                @Advice.Return PipeLineResult result,
                @Advice.Thrown Throwable throwable,
                @Advice.Local("otelRequest") FrankRequest<IAdapter> frankRequest,
                @Advice.Local("otelContext") Context context,
                @Advice.Local("otelScope") Scope scope) {
            if (scope == null) {
                return;
            }
            System.out.println("EXIT ADVICE!");

            if(TAG_EXITS){
                Span current = Span.current();
                current.setAttribute(FRANK_EXIT_STATE_KEY, result.getState().name());
                if(result.getExitCode()>0){
                    current.setAttribute(FRANK_EXIT_CODE_KEY, result.getExitCode());
                }
            }

            scope.close();
            if (throwable != null) {
                instrumenter(FrankSingletons.PIPELINE_INSTRUMENTATION_NAME).end(context, frankRequest, null, throwable);
            } else {
                instrumenter(FrankSingletons.PIPELINE_INSTRUMENTATION_NAME).end(context, frankRequest, result, null);
            }
        }
    }

}
