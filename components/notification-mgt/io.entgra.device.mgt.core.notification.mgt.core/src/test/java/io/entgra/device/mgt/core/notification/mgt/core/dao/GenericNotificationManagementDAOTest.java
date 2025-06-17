/*
 *  Copyright (c) 2018 - 2025, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
 *
 * Entgra (Pvt) Ltd. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package io.entgra.device.mgt.core.notification.mgt.core.dao;

import io.entgra.device.mgt.core.notification.mgt.common.exception.TransactionManagementException;
import io.entgra.device.mgt.core.notification.mgt.core.dao.factory.NotificationManagementDAOFactory;
import io.entgra.device.mgt.core.notification.mgt.common.dto.Notification;
import io.entgra.device.mgt.core.notification.mgt.common.dto.UserNotificationAction;
import io.entgra.device.mgt.core.notification.mgt.common.exception.NotificationManagementException;
import io.entgra.device.mgt.core.notification.mgt.core.common.BaseNotificationManagementTest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

public class GenericNotificationManagementDAOTest extends BaseNotificationManagementTest {

    private static final Log log = LogFactory.getLog(GenericNotificationManagementDAOTest.class);
    private NotificationManagementDAO notificationDAO;
    private int insertedNotificationId = -1;
    private final String testUsername = "user01";
    public static final int SUPER_TENANT_ID = -1234;
    public static final int DUMMY_CONFIG_ID = 10001;

    @BeforeClass
    @Override
    public void init() throws Exception {
        initDataSource();
        notificationDAO = NotificationManagementDAOFactory.getNotificationManagementDAO();
    }

    @Test
    public void testInsertNotification() {
        try {
            NotificationManagementDAOFactory.beginTransaction();
            insertedNotificationId = notificationDAO.insertNotification(
                    SUPER_TENANT_ID,
                    DUMMY_CONFIG_ID,
                    "INFO",
                    "Test Notification Description"
            );
            NotificationManagementDAOFactory.commitTransaction();
            log.info("Inserted notification with ID: " + insertedNotificationId);
            Assert.assertTrue(insertedNotificationId > 0, "Notification ID not returned properly.");
        } catch (NotificationManagementException e) {
            NotificationManagementDAOFactory.rollbackTransaction();
            Assert.fail("Error inserting notification", e);
        } catch (TransactionManagementException e) {
            throw new RuntimeException(e);
        } finally {
            NotificationManagementDAOFactory.closeConnection();
        }
    }

    @Test(dependsOnMethods = "testInsertNotification")
    public void testInsertNotificationUserActions() {
        List<String> users = Arrays.asList(testUsername, "alice", "bob");
        try {
            NotificationManagementDAOFactory.beginTransaction();
            notificationDAO.insertNotificationUserActions(insertedNotificationId, users);
            NotificationManagementDAOFactory.commitTransaction();
            log.info("Inserted user actions for notification ID: " + insertedNotificationId);
        } catch (NotificationManagementException | TransactionManagementException e) {
            NotificationManagementDAOFactory.rollbackTransaction();
            Assert.fail("Error inserting notification user actions", e);
        } finally {
            NotificationManagementDAOFactory.closeConnection();
        }
    }

    @Test(dependsOnMethods = "testInsertNotificationUserActions")
    public void testGetLatestNotifications() {
        try {
            List<Notification> latest = notificationDAO.getLatestNotifications(0, 5);
            Assert.assertFalse(latest.isEmpty(), "No notifications retrieved.");
            log.info("Retrieved " + latest.size() + " latest notifications.");
        } catch (NotificationManagementException e) {
            Assert.fail("Failed to fetch latest notifications", e);
        }
    }

    @Test(dependsOnMethods = "testInsertNotificationUserActions")
    public void testGetNotificationActionsByUser() {
        try {
            List<UserNotificationAction> actions =
                    notificationDAO.getNotificationActionsByUser(testUsername, 10, 0, "UNREAD");
            Assert.assertFalse(actions.isEmpty(), "No actions retrieved for user.");
            log.info("Fetched " + actions.size() + " actions for user " + testUsername);
        } catch (NotificationManagementException e) {
            Assert.fail("Error fetching actions for user", e);
        }
    }

    @Test(dependsOnMethods = "testGetNotificationActionsByUser")
    public void testUpdateNotificationAction() {
        try {
            notificationDAO.updateNotificationAction(
                    List.of(insertedNotificationId),
                    testUsername,
                    "READ"
            );
            List<UserNotificationAction> updated =
                    notificationDAO.getNotificationActionsByUser(testUsername, 10, 0, "READ");
            Assert.assertFalse(updated.isEmpty(), "Update did not reflect.");
            log.info("Successfully updated notification action.");
        } catch (NotificationManagementException e) {
            Assert.fail("Failed to update action", e);
        }
    }

    @Test(dependsOnMethods = "testUpdateNotificationAction")
    public void testGetUnreadNotificationCountForUser() {
        try {
            int count = notificationDAO.getUnreadNotificationCountForUser(testUsername);
            Assert.assertTrue(count >= 0, "Unread count is invalid.");
            log.info("Unread notification count: " + count);
        } catch (NotificationManagementException e) {
            Assert.fail("Failed to get unread count", e);
        }
    }

    @Test(dependsOnMethods = "testUpdateNotificationAction")
    public void testDeleteUserNotification() {
        try {
            notificationDAO.deleteUserNotifications(List.of(insertedNotificationId), testUsername);
            List<UserNotificationAction> actions =
                    notificationDAO.getNotificationActionsByUser(testUsername, 10, 0, null);
            Assert.assertTrue(actions.isEmpty(), "Actions not deleted.");
            log.info("Successfully deleted user notifications.");
        } catch (NotificationManagementException e) {
            Assert.fail("Failed to delete user notifications", e);
        }
    }

    @Test(dependsOnMethods = "testDeleteUserNotification")
    public void testDeleteAllUserNotifications() {
        try {
            notificationDAO.deleteAllUserNotifications("alice");
            List<UserNotificationAction> actions =
                    notificationDAO.getNotificationActionsByUser("alice", 10, 0, null);
            Assert.assertTrue(actions.isEmpty(), "All actions not deleted for user.");
            log.info("Deleted all notifications for user alice.");
        } catch (NotificationManagementException e) {
            Assert.fail("Failed to delete all notifications for user", e);
        }
    }
}
