package org.example.sender;

import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import nl.nn.adapterframework.core.*;
import nl.nn.adapterframework.stream.Message;
import org.example.common.FrankRequest;
import org.example.common.FrankSingletons;


import static io.opentelemetry.javaagent.bootstrap.Java8BytecodeBridge.currentContext;
import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.implementsInterface;
import static net.bytebuddy.matcher.ElementMatchers.*;
import static org.example.common.FrankSingletons.instrumenter;

public class FrankSenderInstrumentation implements TypeInstrumentation {

    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return implementsInterface(named("nl.nn.adapterframework.core.ISender"));
    }

    @Override
    public void transform(TypeTransformer transformer) {
        transformer.applyAdviceToMethod(
                isMethod()
                        .and(named("sendMessage"))
                        .and(takesArguments(2))
                        .and(takesArgument(0, named("nl.nn.adapterframework.stream.Message")))
                        .and(takesArgument(1, named("nl.nn.adapterframework.core.PipeLineSession")))
                ,this.getClass().getName() + "$SenderExecutionAdvice");
    }

    @SuppressWarnings("unused")
    public static class SenderExecutionAdvice {
        @Advice.OnMethodEnter(suppress = Throwable.class)
        public static void methodEnter(
                @Advice.Argument(1) PipeLineSession session,
                @Advice.Argument(0) Message message,
                @Advice.This ISender sender,
                @Advice.Local("otelRequest") FrankRequest<ISender> otelRequest,
                @Advice.Local("otelContext") Context context,
                @Advice.Local("otelScope") Scope scope) {
            boolean useSessionContext = session.containsKey(FrankRequest.SPAN_CONTEXT_SESSION_KEY+sender.getName());
            System.out.println("Sender methodEnter(), found parentContext in session ["+useSessionContext+"] ");
            Context parentContext = useSessionContext
                    ? (Context) session.get(FrankRequest.SPAN_CONTEXT_SESSION_KEY+sender.getName())
                    : currentContext();


            System.out.println("SENDER EXECUTION ADVICE!");
            otelRequest = new FrankRequest<>(message, session, sender);

            if (!instrumenter(FrankSingletons.SENDER_INSTRUMENTATION_NAME).shouldStart(parentContext, otelRequest)) {
                return;
            }

            context = instrumenter(FrankSingletons.SENDER_INSTRUMENTATION_NAME).start(parentContext, otelRequest);
            scope = context.makeCurrent();
        }

        @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
        public static void methodExit(
                @Advice.Argument(1) PipeLineSession session,
                @Advice.Argument(0) Message message,
                @Advice.Return Message result,
                @Advice.Thrown Throwable throwable,
                @Advice.Local("otelRequest") FrankRequest<ISender> otelRequest,
                @Advice.Local("otelContext") Context context,
                @Advice.Local("otelScope") Scope scope) {
            if (scope == null) {
                return;
            }

            scope.close();
            if (throwable != null) {
                instrumenter(FrankSingletons.SENDER_INSTRUMENTATION_NAME).end(context, otelRequest, null, throwable);
            } else {
                instrumenter(FrankSingletons.SENDER_INSTRUMENTATION_NAME).end(context, otelRequest, result, null);
            }
        }
    }
}
