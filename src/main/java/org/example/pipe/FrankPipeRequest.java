package org.example.pipe;

import nl.nn.adapterframework.core.IPipe;
import nl.nn.adapterframework.core.PipeLineSession;
import nl.nn.adapterframework.stream.Message;
import org.example.common.FrankRequest;

public class FrankPipeRequest extends FrankRequest<IPipe> {

    public FrankPipeRequest(Message message, PipeLineSession session, IPipe frankComponent) {
        super(message, session, frankComponent);
    }
}
