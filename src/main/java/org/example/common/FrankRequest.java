package org.example.common;

import nl.nn.adapterframework.core.INamedObject;
import nl.nn.adapterframework.core.PipeLineSession;
import nl.nn.adapterframework.stream.Message;
import org.apache.commons.lang3.StringUtils;

public class FrankRequest<T extends INamedObject> {

    public final static String SPAN_CONTEXT_SESSION_KEY = "FrankInstrumentation.CurrentContext";
    protected final Message message;
    protected final PipeLineSession session;
    protected final T frankComponent;
    protected final String contextPropagationKey;

    public FrankRequest(Message message, PipeLineSession session, T frankComponent) {
        this.message = message;
        this.session = session;
        this.frankComponent = frankComponent;
        this.contextPropagationKey = detectContextPropagationKey();
    }

    public String getName(){
        return frankComponent.getName();
    }
    public String getFrankElementType(){
        return StringUtils.substringBefore(frankComponent.getClass().getSimpleName(), "$$EnhancerBySpring");
    }

    public void setPropagationSessionKeys(String key, String value){
        if(!shouldPropagate()) return;
        session.put(key, value);
    }
    protected String detectContextPropagationKey() {
        return null;
    }
    public boolean shouldPropagate(){
        return contextPropagationKey != null;
    }
    public String getContextPropagationKey(){
        return contextPropagationKey;
    }

}
