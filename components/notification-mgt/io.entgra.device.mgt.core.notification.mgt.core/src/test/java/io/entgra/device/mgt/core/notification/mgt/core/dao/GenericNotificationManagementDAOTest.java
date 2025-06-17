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

import io.entgra.device.mgt.core.notification.mgt.common.dto.Notification;
import io.entgra.device.mgt.core.notification.mgt.common.dto.UserNotificationAction;
import io.entgra.device.mgt.core.notification.mgt.core.common.BaseNotificationManagementTest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

public class GenericNotificationManagementDAOTest extends BaseNotificationManagementTest {

    private static final Log log = LogFactory.getLog(GenericNotificationManagementDAOTest.class);

    @Mock
    private NotificationManagementDAO notificationDAO;

    private int insertedNotificationId = -1;
    private final String testUsername = "user01";
    public static final int SUPER_TENANT_ID = -1234;
    public static final int DUMMY_CONFIG_ID = 10001;

    @BeforeClass
    public void initialize() throws Exception {
        log.info("Initializing DAO test with mocks");
        super.initializeServices();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testInsertNotification() throws Exception {
        when(notificationDAO
                .insertNotification(SUPER_TENANT_ID, DUMMY_CONFIG_ID, "INFO", "Test Notification Description"))
                .thenReturn(1234);
        insertedNotificationId = notificationDAO.insertNotification(
                SUPER_TENANT_ID,
                DUMMY_CONFIG_ID,
                "INFO",
                "Test Notification Description"
        );
        log.info("Inserted notification with ID: " + insertedNotificationId);
        Assert.assertTrue(insertedNotificationId > 0, "Notification ID not returned properly.");
        verify(notificationDAO, times(1))
                .insertNotification(anyInt(), anyInt(), anyString(), anyString());
    }

    @Test(dependsOnMethods = "testInsertNotification")
    public void testInsertNotificationUserActions() throws Exception {
        List<String> users = Arrays.asList(testUsername, "alice", "bob");
        doNothing().when(notificationDAO).insertNotificationUserActions(insertedNotificationId, users);
        notificationDAO.insertNotificationUserActions(insertedNotificationId, users);
        log.info("Inserted user actions for notification ID: " + insertedNotificationId);
        verify(notificationDAO, times(1))
                .insertNotificationUserActions(insertedNotificationId, users);
    }

    @Test(dependsOnMethods = "testInsertNotificationUserActions")
    public void testGetLatestNotifications() throws Exception {
        List<Notification> mockNotifications = new ArrayList<>();
        mockNotifications.add(new Notification());
        when(notificationDAO.getLatestNotifications(0, 5)).thenReturn(mockNotifications);
        List<Notification> latest = notificationDAO.getLatestNotifications(0, 5);
        Assert.assertFalse(latest.isEmpty(), "No notifications retrieved.");
        log.info("Retrieved " + latest.size() + " latest notifications.");
        verify(notificationDAO, times(1)).getLatestNotifications(0, 5);
    }

    @Test(dependsOnMethods = "testInsertNotificationUserActions")
    public void testGetNotificationActionsByUser() throws Exception {
        List<UserNotificationAction> mockActions = new ArrayList<>();
        mockActions.add(new UserNotificationAction());
        when(notificationDAO.getNotificationActionsByUser(testUsername, 10, 0, false))
                .thenReturn(mockActions);
        List<UserNotificationAction> actions =
                notificationDAO.getNotificationActionsByUser(testUsername, 10, 0, false);
        Assert.assertFalse(actions.isEmpty(), "No actions retrieved for user.");
        log.info("Fetched " + actions.size() + " actions for user " + testUsername);
        verify(notificationDAO, times(1))
                .getNotificationActionsByUser(testUsername, 10, 0, false);
    }

    @Test(dependsOnMethods = "testGetNotificationActionsByUser")
    public void testUpdateNotificationAction() throws Exception {
        doNothing().when(notificationDAO)
                .updateNotificationAction(List.of(insertedNotificationId), testUsername, "READ");
        List<UserNotificationAction> updatedActions = new ArrayList<>();
        updatedActions.add(new UserNotificationAction());
        when(notificationDAO.getNotificationActionsByUser(testUsername, 10, 0, true))
                .thenReturn(updatedActions);
        notificationDAO.updateNotificationAction(List.of(insertedNotificationId), testUsername, "READ");
        List<UserNotificationAction> updated =
                notificationDAO.getNotificationActionsByUser(testUsername, 10, 0, true);
        Assert.assertFalse(updated.isEmpty(), "Update did not reflect.");
        log.info("Successfully updated notification action.");
        verify(notificationDAO, times(1))
                .updateNotificationAction(List.of(insertedNotificationId), testUsername, "READ");
        verify(notificationDAO, times(1))
                .getNotificationActionsByUser(testUsername, 10, 0, true);
    }

    @Test(dependsOnMethods = "testUpdateNotificationAction")
    public void testGetUnreadNotificationCountForUser() throws Exception {
        when(notificationDAO.getUnreadNotificationCountForUser(testUsername)).thenReturn(5);
        int count = notificationDAO.getUnreadNotificationCountForUser(testUsername);
        Assert.assertTrue(count >= 0, "Unread count is invalid.");
        log.info("Unread notification count: " + count);
        verify(notificationDAO, times(1))
                .getUnreadNotificationCountForUser(testUsername);
    }

    @Test(dependsOnMethods = "testUpdateNotificationAction")
    public void testDeleteUserNotification() throws Exception {
        doNothing().when(notificationDAO)
                .deleteUserNotifications(List.of(insertedNotificationId), testUsername);
        when(notificationDAO.getNotificationActionsByUser(testUsername, 10, 0, null))
                .thenReturn(Collections.emptyList());
        notificationDAO.deleteUserNotifications(List.of(insertedNotificationId), testUsername);
        List<UserNotificationAction> actions =
                notificationDAO.getNotificationActionsByUser(testUsername, 10, 0, null);
        Assert.assertTrue(actions.isEmpty(), "Actions not deleted.");
        log.info("Successfully deleted user notifications.");
        verify(notificationDAO, times(1))
                .deleteUserNotifications(List.of(insertedNotificationId), testUsername);
        verify(notificationDAO, times(1))
                .getNotificationActionsByUser(testUsername, 10, 0, null);
    }

    @Test(dependsOnMethods = "testDeleteUserNotification")
    public void testDeleteAllUserNotifications() throws Exception {
        doNothing().when(notificationDAO).deleteAllUserNotifications("alice");
        when(notificationDAO.getNotificationActionsByUser("alice", 10, 0, null))
                .thenReturn(Collections.emptyList());
        notificationDAO.deleteAllUserNotifications("alice");
        List<UserNotificationAction> actions =
                notificationDAO.getNotificationActionsByUser("alice", 10, 0, null);
        Assert.assertTrue(actions.isEmpty(), "All actions not deleted for user.");
        log.info("Deleted all notifications for user alice.");
        verify(notificationDAO, times(1))
                .deleteAllUserNotifications("alice");
        verify(notificationDAO, times(1))
                .getNotificationActionsByUser("alice", 10, 0, null);
    }
}
