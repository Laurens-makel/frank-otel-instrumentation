package org.example.common;

import nl.nn.adapterframework.core.INamedObject;
import nl.nn.adapterframework.core.PipeLineSession;
import nl.nn.adapterframework.stream.Message;

public abstract class FrankRequest<T extends INamedObject> {
    protected final Message message;
    protected final PipeLineSession session;
    protected final T frankComponent;

    public FrankRequest(Message message, PipeLineSession session, T frankComponent) {
        this.message = message;
        this.session = session;
        this.frankComponent = frankComponent;
    }

    public String getName(){
        return frankComponent.getName();
    }
    public String getFrankElementType(){
        return frankComponent.getClass().getSimpleName();
    }

    public void setSessionKey(String key, String value){
        session.put(key, value);
    }

}
