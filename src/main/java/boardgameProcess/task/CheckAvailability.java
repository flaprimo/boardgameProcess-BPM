package boardgameProcess.task;

import boardgameProcess.helper.Database;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.model.bpmn.instance.FlowElement;

import java.sql.ResultSet;
import java.util.logging.Logger;

public class CheckAvailability implements JavaDelegate {

    private final static Logger LOGGER = Logger.getLogger(CheckAvailability.class.getName());
    private static Database database = Database.getInstance();

    /**
     * this method will be invoked by the process engine when the service task is executed.
     */
    public void execute(DelegateExecution execution) throws Exception {
        FlowElement serviceTask = execution.getBpmnModelElementInstance();

        String gameQuantityQuery = "SELECT Products.quantity " +
                "FROM Products JOIN Orders ON Products.Name=Orders.game " +
                "WHERE Orders.active=1;";
        ResultSet gameQuantityQueryResults = database.query(gameQuantityQuery);

        Integer quantity = 0;
        if (gameQuantityQueryResults.next())
            quantity = gameQuantityQueryResults.getInt("quantity");
        else {
            execution.setVariable("gameExists", false);

            LOGGER.info(String.format("\n\n\n Task '%s' completed SUCCESSFUL\n" +
                            "game not available in the catalogue\n\n\n",
                    serviceTask.getName(), quantity));
        }


        int quantityAfter = quantity;
        Boolean gameExists = false;
        if (quantity > 0) {
            quantityAfter--;
            gameExists = true;

            String gameQuantityUpdateQuery = String.format("UPDATE Products p " +
                    "SET p.quantity = %d " +
                    "WHERE EXISTS (SELECT * FROM Orders o WHERE o.game=p.name AND o.active=1);", quantityAfter);

            database.update(gameQuantityUpdateQuery);
        }

        execution.setVariable("gameExists", gameExists);
        LOGGER.info(String.format("\n\n\n Task '%s' completed SUCCESSFUL\n" +
                        "game available '%b': quantity before '%d', quantity after '%d'\n\n\n",
                serviceTask.getName(), gameExists, quantity, quantityAfter));
    }
}