/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package boardgameProcess.task;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import boardgameProcess.helper.Database;
import boardgameProcess.helper.Email;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.model.bpmn.instance.FlowElement;

/**
 * This is an implementation of the {@link JavaDelegate} interface. Implementations of that class can
 * be directly invoked by the process engine while executing a BPMN process.
 *
 * @author Daniel Meyer
 *
 */
public class YeServiceTask implements JavaDelegate {

  private final static Logger LOGGER = Logger.getLogger(YeServiceTask.class.getName());
  private static Database database = Database.getInstance();
  private static Email email = Email.getInstance();

  /**
   * this method will be invoked by the process engine when the service task is executed.
   */
  public void execute(DelegateExecution execution) throws Exception {

    // use camunda model API to get current BPMN element
    FlowElement serviceTask = execution.getBpmnModelElementInstance();

    // query
    List<Map<String, Object>> results = database.query("SELECT * FROM Products");

    for (Map<String, Object> row:results) {
      System.out.print("\n");
      for (Map.Entry<String, Object> rowEntry : row.entrySet()) {
        System.out.print(rowEntry.getKey() + " = " + rowEntry.getValue() + ", ");
      }
    }

    email.sendEmail("klaus.teuber@catanftw.de", "boardgame@shop.com", "Order cancellation",
            "Hi!\n" +
                    "we are sorry to inform you that the order for your game has been cancelled!\n" +
                    "Sincerely,\nYour favorite boardgame shop!");

    // log status
    LOGGER.info("\n\n\nYEEEE Service Task '"+serviceTask.getName() +"' is eddaje!\n\n\n");
  }
}
