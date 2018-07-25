package boardgameProcess.task;

import boardgameProcess.helper.Database;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.model.bpmn.instance.FlowElement;

import java.util.logging.Logger;

public class CloseOrder implements JavaDelegate {

    private final static Logger LOGGER = Logger.getLogger(CloseOrder.class.getName());
    private static Database database = Database.getInstance();

    /**
     * this method will be invoked by the process engine when the service task is executed.
     */
    public void execute(DelegateExecution execution) throws Exception {
        FlowElement serviceTask = execution.getBpmnModelElementInstance();

        String orderActiveUpdateQuery = "UPDATE Orders " +
                "SET active=0 " +
                "WHERE active=1;";
        database.update(orderActiveUpdateQuery);

        LOGGER.info(String.format("\n\n\n Task '%s' completed SUCCESSFUL\n" +
                        "active order set as non active\n\n\n",
                serviceTask.getName()));
    }
}