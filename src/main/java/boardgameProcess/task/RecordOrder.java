package boardgameProcess.task;

import boardgameProcess.helper.Database;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.model.bpmn.instance.FlowElement;

import java.util.logging.Logger;

public class RecordOrder implements JavaDelegate {

    private final static Logger LOGGER = Logger.getLogger(RecordOrder.class.getName());
    private static Database database = Database.getInstance();

    /**
     * this method will be invoked by the process engine when the service task is executed.
     */
    public void execute(DelegateExecution execution) throws Exception {
        FlowElement serviceTask = execution.getBpmnModelElementInstance();

        // get order values from incoming message
        String name = (String) execution.getVariable("name");
        String email = (String) execution.getVariable("email");
        String game = (String) execution.getVariable("game");
        String address = (String) execution.getVariable("address");

        // insert new order
        String orderInsert = String.format("INSERT INTO Orders VALUES" +
                "(default, '%s', '%s', '%s', '%s', true);", name, email, game, address);
        database.update(orderInsert);

        LOGGER.info(String.format("\n\n\n Task '%s' completed SUCCESSFUL\n" +
                "New order added: (email='%s', game='%s', address='%s')\n\n\n",
                serviceTask.getName(), email, game, address));
    }
}