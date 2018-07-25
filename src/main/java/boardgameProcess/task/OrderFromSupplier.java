package boardgameProcess.task;

import boardgameProcess.helper.Rest;
import boardgameProcess.helper.Database;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.model.bpmn.instance.FlowElement;

import java.sql.ResultSet;
import java.util.logging.Logger;

public class OrderFromSupplier implements JavaDelegate {

    private final static Logger LOGGER = Logger.getLogger(OrderFromSupplier.class.getName());
    private static Rest rest = Rest.getInstance();
    private static Database database = Database.getInstance();

    /**
     * this method will be invoked by the process engine when the service task is executed.
     */
    public void execute(DelegateExecution execution) throws Exception {
        FlowElement serviceTask = execution.getBpmnModelElementInstance();

        String gameNameQuery = "SELECT game FROM Orders WHERE active=1;";
        ResultSet gameNameQueryResults = database.query(gameNameQuery);

        String gameName = null;
        if (gameNameQueryResults.next())
            gameName = gameNameQueryResults.getString("game");

        if (gameName == null) {
            throw new Exception("active order game name not found!");
        }

        try {
            rest.post("supplier/game/" + gameName, null);
        } catch(Exception e) {
            LOGGER.severe("\n\n\n" + Rest.class.getName() + " - post request FAILURE\n");
            e.printStackTrace();
        }

        LOGGER.info(String.format("\n\n\n Task '%s' completed SUCCESSFUL\n" +
                        "game '%s' ordered from supplier\n\n\n",
                serviceTask.getName(), gameName));
    }
}