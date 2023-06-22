package org.example.pipe;

import nl.nn.adapterframework.core.IPipe;
import nl.nn.adapterframework.core.PipeLineSession;
import nl.nn.adapterframework.pipes.IteratingPipe;
import nl.nn.adapterframework.stream.Message;
import org.example.common.FrankRequest;

public class FrankPipeRequest extends FrankRequest<IPipe> {

    private final String contextPropagationKey;
    private final boolean isParallelContextPropagation = Boolean.parseBoolean(System.getProperty("frank.instrumentation.parallel.iterator.propagation", "true"));

    public FrankPipeRequest(Message message, PipeLineSession session, IPipe frankComponent) {
        super(message, session, frankComponent);

        contextPropagationKey = detectContextPropagationKey();
    }

    public boolean shouldPropagate(){
        return contextPropagationKey != null;
    }

    public String getContextPropagationKey(){
        return contextPropagationKey;
    }

    private String detectContextPropagationKey(){
        // IteratingPipes with 'parallel' set to 'true' spawn child threads
        if(isParallelContextPropagation && frankComponent instanceof IteratingPipe){
            IteratingPipe iteratingPipe = ((IteratingPipe) frankComponent);
            if(iteratingPipe.isParallel()){
                return FrankRequest.SPAN_CONTEXT_SESSION_KEY+iteratingPipe.getSender().getName();
            }
        }
        return null;
    }

}
