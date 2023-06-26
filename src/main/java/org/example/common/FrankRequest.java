package org.example.common;

import io.opentelemetry.context.Context;

import nl.nn.adapterframework.core.INamedObject;
import nl.nn.adapterframework.core.PipeLineSession;
import nl.nn.adapterframework.stream.Message;
import org.apache.commons.lang3.StringUtils;

import static io.opentelemetry.javaagent.bootstrap.Java8BytecodeBridge.currentContext;

public class FrankRequest<T extends INamedObject> {

    public final static String SPAN_CONTEXT_SESSION_KEY = "FrankInstrumentation.CurrentContext";
    protected final Message message;
    protected final PipeLineSession session;
    protected final T frankComponent;
    protected final String contextPropagationKey;
    protected final Context parentContext;

    public FrankRequest(Message message, PipeLineSession session, T frankComponent) {
        this.message = message;
        this.session = session;
        this.frankComponent = frankComponent;
        this.contextPropagationKey = detectContextPropagationKey();
        this.parentContext = detectParentContext();
    }

    public String getName(){
        return frankComponent.getName();
    }
    public String getFrankElementType(){
        return StringUtils.substringBefore(frankComponent.getClass().getSimpleName(), "$$EnhancerBySpring");
    }

    public void setPropagationSessionKeys(String key, Object value){
        if(!shouldPropagate()) return;
        session.put(key, value);
    }
    public boolean shouldPropagate(){
        return contextPropagationKey != null;
    }
    // should be overridden in a child class to detect 'special' situations where context should be delegated manually to child threads
    protected String detectContextPropagationKey() {
        return null;
    }
    public String getContextPropagationKey(){
        return contextPropagationKey;
    }

    protected Context detectParentContext(){
        String sessionContextKey = FrankRequest.SPAN_CONTEXT_SESSION_KEY+frankComponent.getName();
        return session.containsKey(sessionContextKey)
                ? (Context) session.get(sessionContextKey)
                : currentContext();
    }
    public Context getParentContext(){
        return parentContext;
    }

}
