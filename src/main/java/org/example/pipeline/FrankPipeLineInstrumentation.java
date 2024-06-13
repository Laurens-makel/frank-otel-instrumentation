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
import org.apache.logging.log4j.ThreadContext;
import org.example.common.FrankRequest;
import org.example.common.FrankSingletons;

import java.util.Map;

import static net.bytebuddy.matcher.ElementMatchers.*;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;
import static org.example.common.FrankSingletons.*;

public class FrankPipeLineInstrumentation implements TypeInstrumentation {

    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return named(FrankClasses.PIPELINE_PROCESSOR.className());
    }

    @Override
    public void transform(TypeTransformer transformer) {
        transformer.applyAdviceToMethod(
                isMethod()
                        .and(named("processPipeLine"))
                        .and(not(isAbstract()))
                        .and(takesArguments(5))
                        .and(takesArgument(0, named(FrankClasses.PIPELINE.className())))
                        .and(takesArgument(1, named(FrankClasses.STRING.className())))
                        .and(takesArgument(2, named(FrankClasses.MESSAGE.className())))
                        .and(takesArgument(3, named(FrankClasses.PIPELINE_SESSION.className())))
                        .and(takesArgument(4, named(FrankClasses.STRING.className())))

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

            Span span = Span.current();
            if(TAG_EXITS){
                span.setAttribute(FRANK_EXIT_STATE_KEY, result.getState().name());
                if(result.getExitCode()>0){
                    span.setAttribute(FRANK_EXIT_CODE_KEY, result.getExitCode());
                }
            }

            if(TAG_LOG_CONTEXT){
                for (Map.Entry<String, String> entry : ThreadContext.getContext().entrySet()) {
                    setSpanStringValue(span, entry.getKey(), entry.getValue());
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
