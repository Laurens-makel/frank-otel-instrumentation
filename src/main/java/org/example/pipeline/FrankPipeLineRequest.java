package org.example.pipeline;

import nl.nn.adapterframework.core.IAdapter;
import nl.nn.adapterframework.core.PipeLineSession;
import nl.nn.adapterframework.stream.Message;
import org.example.common.FrankRequest;

public class FrankPipeLineRequest extends FrankRequest<IAdapter> {

    public FrankPipeLineRequest(Message message, PipeLineSession session, IAdapter frankComponent) {
        super(message, session, frankComponent);
    }

}
