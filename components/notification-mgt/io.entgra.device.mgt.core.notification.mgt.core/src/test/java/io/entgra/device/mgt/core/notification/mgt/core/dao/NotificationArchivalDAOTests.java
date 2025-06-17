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

import io.entgra.device.mgt.core.notification.mgt.common.dto.UserNotificationAction;
import io.entgra.device.mgt.core.notification.mgt.core.dao.factory.NotificationManagementDAOFactory;
import io.entgra.device.mgt.core.notification.mgt.core.common.BaseNotificationManagementTest;
import io.entgra.device.mgt.core.notification.mgt.core.dao.factory.archive.NotificationArchivalDestDAOFactory;
import io.entgra.device.mgt.core.notification.mgt.core.dao.impl.archive.GenericNotificationArchivalDAOImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Set;
import java.util.Collections;
import java.util.List;

public class NotificationArchivalDAOTests extends BaseNotificationManagementTest {
    private static final Log log = LogFactory.getLog(NotificationArchivalDAOTests.class);

    public static final int SUPER_TENANT_ID = -1234;
    private final int tenantId = SUPER_TENANT_ID;
    private int notificationId;
    private final int configId = 101;
    private final String testUsername = "test-user";

    private GenericNotificationArchivalDAOImpl archivalDAO;

    @BeforeClass
    @Override
    public void init() throws Exception {
        initDataSource();
        archivalDAO = new GenericNotificationArchivalDAOImpl();
    }

    @Test
    public void testMoveUserActionsToArchive() throws Exception {
        insertDummyNotificationAndActions();
        NotificationManagementDAOFactory.beginTransaction();
        archivalDAO.moveUserActionsToArchive(Collections.singletonList(notificationId));
        NotificationManagementDAOFactory.commitTransaction();
        List<UserNotificationAction> archivedActions = fetchArchivedUserActions(notificationId);
        if (!isMock()) {
            Assert.assertEquals(archivedActions.size(), 1, "User actions were not archived");
            Assert.assertEquals(archivedActions.get(0).getNotificationId(), notificationId);
        }
    }

    @Test(dependsOnMethods = "testMoveUserActionsToArchive")
    public void testMoveNotificationsToArchiveByConfig() throws Exception {
        insertDummyNotification();
        Timestamp cutoff = new Timestamp(System.currentTimeMillis() + 1000);
        NotificationManagementDAOFactory.beginTransaction();
        List<Integer> archivedIds = archivalDAO.moveNotificationsToArchiveByConfig(cutoff, tenantId, configId);
        NotificationManagementDAOFactory.commitTransaction();
        if (!isMock()) {
            Assert.assertTrue(archivedIds.contains(notificationId),
                    "Notification was not archived by config");
        }
    }

    @Test(dependsOnMethods = "testMoveNotificationsToArchiveByConfig")
    public void testMoveNotificationsToArchiveExcludingConfigs() throws Exception {
        insertDummyNotification();
        Timestamp cutoff = new Timestamp(System.currentTimeMillis() + 1000);
        NotificationManagementDAOFactory.beginTransaction();
        List<Integer> archivedIds =
                archivalDAO.moveNotificationsToArchiveExcludingConfigs(cutoff, tenantId, Set.of(999));
        NotificationManagementDAOFactory.commitTransaction();
        if (!isMock()) {
            Assert.assertTrue(archivedIds.contains(notificationId),
                    "Notification was not archived excluding configs");
        }
    }

    @Test
    public void testDeleteOldNotificationsByConfig() throws Exception {
        insertDummyNotification();
        Timestamp cutoff = new Timestamp(System.currentTimeMillis() + 1000);
        NotificationManagementDAOFactory.beginTransaction();
        int deleted = archivalDAO.deleteOldNotificationsByConfig(cutoff, tenantId, configId);
        NotificationManagementDAOFactory.commitTransaction();
        if (!isMock()) {
            Assert.assertEquals(deleted, 1, "Notification was not deleted");
        }
    }

    @Test
    public void testArchiveUserNotifications() throws Exception {
        insertDummyNotificationAndActions();
        NotificationManagementDAOFactory.beginTransaction();
        archivalDAO.archiveUserNotifications(Collections.singletonList(notificationId), testUsername);
        NotificationManagementDAOFactory.commitTransaction();
        List<UserNotificationAction> archived = fetchArchivedUserActions(notificationId);
        if (!isMock()) {
            Assert.assertEquals(archived.size(), 1, "User notification was not archived");
        }
    }

    @Test
    public void testArchiveAllUserNotifications() throws Exception {
        insertDummyNotificationAndActions();
        NotificationManagementDAOFactory.beginTransaction();
        archivalDAO.archiveAllUserNotifications(testUsername);
        NotificationManagementDAOFactory.commitTransaction();
        List<UserNotificationAction> archived = fetchArchivedUserActions(notificationId);
        if (!isMock()) {
            Assert.assertEquals(archived.size(), 1, "All user notifications were not archived");
        }
    }

    @Test
    public void testDeleteExpiredArchivedNotifications() throws Exception {
        insertDummyArchivedNotificationAndActions();
        Timestamp past = new Timestamp(System.currentTimeMillis() + 1000);
        NotificationManagementDAOFactory.beginTransaction();
        archivalDAO.deleteExpiredArchivedNotifications(past, tenantId);
        NotificationManagementDAOFactory.commitTransaction();
        List<UserNotificationAction> archived = fetchArchivedUserActions(notificationId);
        if (!isMock()) {
            Assert.assertTrue(archived.isEmpty(), "Expired archived notifications were not deleted");
        }
    }

    // utility methods =
    private void insertDummyNotification() throws Exception {
        NotificationManagementDAOFactory.beginTransaction();
        notificationId = NotificationManagementDAOFactory.getNotificationManagementDAO()
                .insertNotification(tenantId, configId, "INFO", "test-desc");
        NotificationManagementDAOFactory.commitTransaction();
    }

    private void insertDummyNotificationAndActions() throws Exception {
        insertDummyNotification();
        NotificationManagementDAOFactory.beginTransaction();
        NotificationManagementDAOFactory.getNotificationManagementDAO()
                .insertNotificationUserActions(notificationId, List.of(testUsername));
        NotificationManagementDAOFactory.commitTransaction();
    }

    private void insertDummyArchivedNotificationAndActions() throws Exception {
        insertDummyNotificationAndActions();
        archivalDAO.archiveUserNotifications(List.of(notificationId), testUsername);
        archivalDAO.moveNotificationsToArchiveByConfig(new Timestamp(System.currentTimeMillis()
                + 1000), tenantId, configId);
    }

    private List<UserNotificationAction> fetchArchivedUserActions(int id) throws Exception {
        String sql = "SELECT * FROM DM_NOTIFICATION_USER_ACTION_ARCH WHERE NOTIFICATION_ID = ?";
        try (Connection conn = NotificationArchivalDestDAOFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                List<UserNotificationAction> actions = new ArrayList<>();
                while (rs.next()) {
                    UserNotificationAction action = new UserNotificationAction();
                    action.setNotificationId(rs.getInt("NOTIFICATION_ID"));
                    action.setUsername(rs.getString("USERNAME"));
                    action.setActionType(rs.getString("ACTION_TYPE"));
                    action.setActionTimestamp(rs.getTimestamp("ACTION_TIMESTAMP"));
                    actions.add(action);
                }
                return actions;
            }
        }
    }
}
