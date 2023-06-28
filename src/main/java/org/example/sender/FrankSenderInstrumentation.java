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
import org.example.common.FrankSingletons.FrankClasses;

import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.implementsInterface;
import static net.bytebuddy.matcher.ElementMatchers.*;
import static org.example.common.FrankSingletons.instrumenter;

public class FrankSenderInstrumentation implements TypeInstrumentation {

    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return implementsInterface(named(FrankClasses.SENDER.className()));
    }

    @Override
    public void transform(TypeTransformer transformer) {
        transformer.applyAdviceToMethod(
                isMethod()
                        .and(named("sendMessage"))
                        .and(takesArguments(2))
                        .and(takesArgument(0, named(FrankClasses.MESSAGE.className())))
                        .and(takesArgument(1, named(FrankClasses.PIPELINE_SESSION.className())))
                ,this.getClass().getName() + "$SenderExecutionAdvice");
    }

    @SuppressWarnings("unused")
    public static class SenderExecutionAdvice {
        @Advice.OnMethodEnter(suppress = Throwable.class)
        public static void methodEnter(
                @Advice.Argument(1) PipeLineSession session,
                @Advice.Argument(0) Message message,
                @Advice.This ISender sender,
                @Advice.Local("otelRequest") FrankRequest<ISender> frankRequest,
                @Advice.Local("otelContext") Context context,
                @Advice.Local("otelScope") Scope scope) {
            frankRequest = new FrankRequest<>(message, session, sender);
            Context parentContext = frankRequest.getParentContext();

            if (!instrumenter(FrankSingletons.SENDER_INSTRUMENTATION_NAME).shouldStart(parentContext, frankRequest)) {
                return;
            }

            context = instrumenter(FrankSingletons.SENDER_INSTRUMENTATION_NAME).start(parentContext, frankRequest);
            scope = context.makeCurrent();
        }

        @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
        public static void methodExit(
                @Advice.Argument(1) PipeLineSession session,
                @Advice.Argument(0) Message message,
                @Advice.Return Message result,
                @Advice.Thrown Throwable throwable,
                @Advice.Local("otelRequest") FrankRequest<ISender> frankRequest,
                @Advice.Local("otelContext") Context context,
                @Advice.Local("otelScope") Scope scope) {
            if (scope == null) {
                return;
            }

            scope.close();
            if (throwable != null) {
                instrumenter(FrankSingletons.SENDER_INSTRUMENTATION_NAME).end(context, frankRequest, null, throwable);
            } else {
                instrumenter(FrankSingletons.SENDER_INSTRUMENTATION_NAME).end(context, frankRequest, result, null);
            }
        }
    }
}
