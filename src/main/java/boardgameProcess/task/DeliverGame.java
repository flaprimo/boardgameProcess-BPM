package boardgameProcess.task;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.model.bpmn.instance.FlowElement;

import java.util.logging.Logger;

public class DeliverGame implements JavaDelegate {

    private final static Logger LOGGER = Logger.getLogger(DeliverGame.class.getName());
    private static Boolean receiverPresent = true;

    /**
     * this method will be invoked by the process engine when the service task is executed.
     */
    public void execute(DelegateExecution execution) throws Exception {
        FlowElement serviceTask = execution.getBpmnModelElementInstance();

        if (!receiverPresent)
            throw new BpmnError("ReceiverPresent");

        LOGGER.info(String.format("\n\n\n Task '%s' completed SUCCESSFUL\n" +
                        "receiver presence at given address: '%b'\n\n\n",
                serviceTask.getName(), receiverPresent));
    }
}