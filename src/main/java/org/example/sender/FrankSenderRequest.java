package org.example.sender;

import nl.nn.adapterframework.core.IPipe;
import nl.nn.adapterframework.core.ISender;
import nl.nn.adapterframework.core.PipeLineSession;
import nl.nn.adapterframework.stream.Message;
import org.example.common.FrankRequest;

public class FrankSenderRequest extends FrankRequest<ISender> {

    public FrankSenderRequest(Message message, PipeLineSession session, ISender frankComponent) {
        super(message, session, frankComponent);
    }
}
