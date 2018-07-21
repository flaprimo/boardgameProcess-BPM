package boardgameProcess.helper;

import static org.camunda.bpm.engine.authorization.Authorization.ANY;
import static org.camunda.bpm.engine.authorization.Authorization.AUTH_TYPE_GRANT;
import static org.camunda.bpm.engine.authorization.Permissions.ACCESS;
import static org.camunda.bpm.engine.authorization.Permissions.ALL;
import static org.camunda.bpm.engine.authorization.Resources.APPLICATION;

import java.util.logging.Logger;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.authorization.Authorization;
import org.camunda.bpm.engine.authorization.Groups;
import org.camunda.bpm.engine.authorization.Resource;
import org.camunda.bpm.engine.authorization.Resources;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.impl.persistence.entity.AuthorizationEntity;

//from: https://github.com/camunda-consulting/camunda-util-demo-support/blob/master/src/main/java/com/camunda/consulting/util/UserGenerator.java

public class UserGenerator {

    private final static Logger LOGGER = Logger.getLogger(UserGenerator.class.getName());

    public static void createDefaultUsers(ProcessEngine engine) {
        LOGGER.info("Generating default users for showroom");

        // /////////////////////////////////////
        // create user as otherwise the invoice exmaple will re-create users
        // Give demo user Admin Rights as well
        addUser(engine, "demo", "demo", "Demo", "Demo");

        // Admin USer - is allowed to do anything
        addUser(engine, "admin", "admin", "Camunda", "BPM");
        if (addGroup(engine, Groups.CAMUNDA_ADMIN, "Camunda BPM Admin", "admin", "demo")) {
            // create ADMIN authorizations on all built-in resources
            for (Resource resource : Resources.values()) {
                if (engine.getAuthorizationService().createAuthorizationQuery().groupIdIn(Groups.CAMUNDA_ADMIN).resourceType(resource).resourceId(ANY).count() == 0) {
                    AuthorizationEntity userAdminAuth = new AuthorizationEntity(AUTH_TYPE_GRANT);
                    userAdminAuth.setGroupId(Groups.CAMUNDA_ADMIN);
                    userAdminAuth.setResource(resource);
                    userAdminAuth.setResourceId(ANY);
                    userAdminAuth.addPermission(ALL);
                    engine.getAuthorizationService().saveAuthorization(userAdminAuth);
                }
            }
        }
    }

    private static boolean addGroup(ProcessEngine engine, String groupId, String groupName, String... members) {
        if (engine.getIdentityService().isReadOnly()) {
            LOGGER.severe("Identity service provider is Read Only, not creating any demo users.");
            return false;
        }
        if (engine.getIdentityService().createGroupQuery().groupId(groupId).count() > 0) {
            addMembership(engine, groupId, members);
            return false;
        }

        Group salesGroup = engine.getIdentityService().newGroup(groupId);
        salesGroup.setName(groupName);
        salesGroup.setType("WORKFLOW");
        engine.getIdentityService().saveGroup(salesGroup);

        // authorize groups for tasklist only:
        Authorization auth = engine.getAuthorizationService().createNewAuthorization(AUTH_TYPE_GRANT);
        auth.setGroupId(groupId);
        auth.addPermission(ACCESS);
        auth.setResourceId("tasklist");
        auth.setResource(APPLICATION);
        engine.getAuthorizationService().saveAuthorization(auth);

        addMembership(engine, groupId, members);
        return true;
    }

    private static void addMembership(ProcessEngine engine, String groupId, String... userIds) {
        for (String userId : userIds) {
            engine.getIdentityService().deleteMembership(userId, groupId);
            try {
                engine.getIdentityService().createMembership(userId, groupId);
            } catch (Exception ex) {
                // memebership already there - ignore
            }
        }
    }

    private static boolean addUser(ProcessEngine engine, String userName, String password, String firstname, String lastname) {
        if (engine.getIdentityService().isReadOnly()) {
            LOGGER.severe("Identity service provider is Read Only, not creating any demo users.");
            return false;
        }
        if (engine.getIdentityService().createUserQuery().userId(userName).count() > 0) {
            return false;
        }
        User user = engine.getIdentityService().newUser(userName);
        user.setFirstName(firstname);
        user.setLastName(lastname);
        user.setPassword(password);
        user.setEmail("demo@camunda.org");
        engine.getIdentityService().saveUser(user);
        return true;
    }
}