package boardgameProcess.task;

import boardgameProcess.helper.Database;
import boardgameProcess.helper.Email;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.model.bpmn.instance.FlowElement;

import java.sql.ResultSet;
import java.util.logging.Logger;

public class NotifyShipment implements JavaDelegate {

    private final static Logger LOGGER = Logger.getLogger(NotifyShipment.class.getName());
    private static Email email = Email.getInstance();
    private static Database database = Database.getInstance();

    /**
     * this method will be invoked by the process engine when the service task is executed.
     */
    public void execute(DelegateExecution execution) throws Exception {
        FlowElement serviceTask = execution.getBpmnModelElementInstance();

        String orderQuery = "SELECT name, email, game, address FROM Orders " +
                "WHERE Orders.active=1;";
        ResultSet orderQueryResults = database.query(orderQuery);

        String customerName;
        String customerEmail;
        String game;
        String address;

        if (orderQueryResults.next()) {
            customerName = orderQueryResults.getString("name");
            customerEmail = orderQueryResults.getString("email");
            game = orderQueryResults.getString("game");
            address = orderQueryResults.getString("address");
        }
        else {
            throw new Exception("Active order not found!");
        }

        email.sendEmail(customerEmail, "boardgame@shop.com", "Game has been shipped",
                String.format("Hi %s!\n" +
                        "we are happy to inform you that your order for the game '%s' has been shipped!\n" +
                        "it will be delivered to the following address: %s\n\n" +
                        "Sincerly,\nYour favorite boardgame shop!", customerName, game, address));

        LOGGER.info(String.format("\n\n\n Task '%s' completed SUCCESSFUL\n" +
                        "user (name='%s', customerEmail='%s', address='%s') notified of order (game='%s') shipment\n\n\n",
                serviceTask.getName(), customerName, customerEmail, game, address));
    }
}