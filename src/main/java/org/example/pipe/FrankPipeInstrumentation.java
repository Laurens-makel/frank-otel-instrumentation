package org.example.pipe;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import nl.nn.adapterframework.core.*;
import nl.nn.adapterframework.stream.Message;
import org.example.common.FrankSingletons;

import static net.bytebuddy.matcher.ElementMatchers.*;
import static org.example.common.FrankSingletons.*;

public class FrankPipeInstrumentation implements TypeInstrumentation {

    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return named(FrankClasses.PIPE_PROCESSOR.className());
    }

    @Override
    public void transform(TypeTransformer transformer) {
        transformer.applyAdviceToMethod(
                isMethod()
                        .and(named("processPipe"))
                        .and(not(isAbstract()))
                        .and(takesArguments(4))
                        .and(takesArgument(0, named(FrankClasses.PIPELINE.className())))
                        .and(takesArgument(1, named(FrankClasses.PIPE.className())))
                        .and(takesArgument(2, named(FrankClasses.MESSAGE.className())))
                        .and(takesArgument(3, named(FrankClasses.PIPELINE_SESSION.className())))
                ,this.getClass().getName() + "$PipeExecutionAdvice");
    }

    @SuppressWarnings("unused")
    public static class PipeExecutionAdvice {
        @Advice.OnMethodEnter(suppress = Throwable.class)
        public static void methodEnter(
                @Advice.Argument(3) PipeLineSession session,
                @Advice.Argument(2) Message message,
                @Advice.Argument(1) IPipe pipe,
                @Advice.Argument(0) PipeLine pipeLine,
                @Advice.Local("otelRequest") FrankPipeRequest frankRequest,
                @Advice.Local("otelContext") Context context,
                @Advice.Local("otelScope") Scope scope) {
            frankRequest = new FrankPipeRequest(message, session, pipe);
            Context parentContext = frankRequest.getParentContext();

            if (!instrumenter(FrankSingletons.PIPE_INSTRUMENTATION_NAME).shouldStart(parentContext, frankRequest)) {
                return;
            }

            context = instrumenter(FrankSingletons.PIPE_INSTRUMENTATION_NAME).start(parentContext, frankRequest);
            scope = context.makeCurrent();

            // in certain situations, a pipe should manually propagate the tracing context to its children
            frankRequest.setPropagationSessionKeys(frankRequest.getContextPropagationKey(), context);
        }

        @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
        public static void methodExit(
                @Advice.Argument(3) PipeLineSession session,
                @Advice.Argument(2) Message message,
                @Advice.Argument(1) IPipe pipe,
                @Advice.Argument(0) PipeLine pipeLine,
                @Advice.Return PipeRunResult result,
                @Advice.Thrown Throwable throwable,
                @Advice.Local("otelRequest") FrankPipeRequest frankRequest,
                @Advice.Local("otelContext") Context context,
                @Advice.Local("otelScope") Scope scope) {
            if (scope == null) {
                return;
            }

            if(TAG_FORWARDS){
                PipeForward forward = result.getPipeForward();
                Span current = Span.current();
                current.setAttribute(FRANK_FORWARD_NAME_KEY, forward.getName());
                current.setAttribute(FRANK_FORWARD_PATH_KEY, forward.getPath());
            }

            scope.close();
            if (throwable != null) {
                instrumenter(FrankSingletons.PIPE_INSTRUMENTATION_NAME).end(context, frankRequest, null, throwable);
            } else {
                instrumenter(FrankSingletons.PIPE_INSTRUMENTATION_NAME).end(context, frankRequest, result, null);
            }

            // remove context from session again, since it's not relevant anymore
            if(frankRequest.shouldPropagate()){
                session.remove(frankRequest.getContextPropagationKey());
            }
        }
    }
}
