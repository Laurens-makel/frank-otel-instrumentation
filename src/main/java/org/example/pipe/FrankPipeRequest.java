package org.example.pipe;

import nl.nn.adapterframework.core.IPipe;
import nl.nn.adapterframework.core.PipeLineSession;
import nl.nn.adapterframework.stream.Message;

public class FrankPipeRequest {

    private final Message message;
    private final PipeLineSession session;
    private final IPipe pipe;

    public FrankPipeRequest(IPipe pipe, Message message, PipeLineSession session){
        this.pipe = pipe;
        this.message = message;
        this.session = session;
    }

    public String getPipeName(){
        return pipe.getName();
    }
    public String getPipeType(){
        return pipe.getClass().getSimpleName();
    }

    public void setSessionKey(String key, String value){
        session.put(key, value);
    }

}
