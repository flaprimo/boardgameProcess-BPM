package boardgameProcess.task;

import boardgameProcess.helper.Database;
import boardgameProcess.helper.Email;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.model.bpmn.instance.FlowElement;

import java.sql.ResultSet;
import java.util.logging.Logger;

public class AskSatisfaction implements JavaDelegate {

    private final static Logger LOGGER = Logger.getLogger(AskSatisfaction.class.getName());
    private static Email email = Email.getInstance();
    private static Database database = Database.getInstance();

    /**
     * this method will be invoked by the process engine when the service task is executed.
     */
    public void execute(DelegateExecution execution) throws Exception {
        FlowElement serviceTask = execution.getBpmnModelElementInstance();

        String orderQuery = "SELECT name, email, game FROM Orders " +
                "WHERE Orders.active=1;";
        ResultSet orderQueryResults = database.query(orderQuery);

        String customerName;
        String customerEmail;
        String game;

        if (orderQueryResults.next()) {
            customerName = orderQueryResults.getString("name");
            customerEmail = orderQueryResults.getString("email");
            game = orderQueryResults.getString("game");
        }
        else {
            throw new Exception("Active order not found!");
        }

        email.sendEmail(customerEmail, "boardgame@shop.com", "What do you think about us?",
                String.format("Hi %s!\n" +
                        "we would like to ask you some feedback about your last order for the game '%s'.\n\n" +
                        "Sincerly,\nYour favorite boardgame shop!", customerName, game));

        LOGGER.info(String.format("\n\n\n Task '%s' completed SUCCESSFUL\n" +
                        "ask user (name='%s', customerEmail='%s') for the order (game='%s') satisfaction\n\n\n",
                serviceTask.getName(), customerName, customerEmail, game));
    }
}