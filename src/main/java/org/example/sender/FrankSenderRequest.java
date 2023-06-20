package org.example.sender;

import nl.nn.adapterframework.core.IPipe;
import nl.nn.adapterframework.core.ISender;
import nl.nn.adapterframework.core.PipeLineSession;
import nl.nn.adapterframework.stream.Message;

public class FrankSenderRequest {

    private final Message message;
    private final PipeLineSession session;
    private final ISender sender;

    public FrankSenderRequest(ISender sender, Message message, PipeLineSession session){
        this.sender = sender;
        this.message = message;
        this.session = session;
    }

    public String getSenderName(){
        return "Dummy";
    }
    public String getSenderType(){
        return "fummmy";
    }

    public void setSessionKey(String key, String value){
        session.put(key, value);
    }

}
