package org.example.parameter;

import nl.nn.adapterframework.core.PipeLineSession;
import nl.nn.adapterframework.parameters.Parameter;
import nl.nn.adapterframework.stream.Message;
import org.example.common.FrankRequest;

public class FrankParameterRequest extends FrankRequest<Parameter> {

    public FrankParameterRequest(Message message, PipeLineSession session, Parameter frankComponent) {
        super(message, session, frankComponent);
    }

}
