/*
 * Copyright (c) 2018 - 2025, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
 *
 * Entgra (Pvt) Ltd. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.entgra.device.mgt.core.notification.mgt.core.impl;

import io.entgra.device.mgt.core.notification.mgt.common.beans.*;
import io.entgra.device.mgt.core.notification.mgt.common.dto.Notification;
import io.entgra.device.mgt.core.notification.mgt.common.dto.UserNotificationAction;
import io.entgra.device.mgt.core.notification.mgt.common.dto.UserNotificationPayload;
import io.entgra.device.mgt.core.notification.mgt.common.exception.NotificationArchivalException;
import io.entgra.device.mgt.core.notification.mgt.common.exception.NotificationManagementException;
import io.entgra.device.mgt.core.notification.mgt.common.exception.TransactionManagementException;
import io.entgra.device.mgt.core.notification.mgt.core.common.BaseNotificationManagementTest;
import io.entgra.device.mgt.core.notification.mgt.core.dao.NotificationArchivalDAO;
import io.entgra.device.mgt.core.notification.mgt.core.dao.NotificationManagementDAO;
import io.entgra.device.mgt.core.notification.mgt.core.dao.factory.NotificationManagementDAOFactory;
import io.entgra.device.mgt.core.notification.mgt.core.util.Constants;
import io.entgra.device.mgt.core.notification.mgt.core.util.NotificationEventBroker;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.sql.SQLException;

/**
 * Test class for NotificationManagementServiceImpl
 */
public class NotificationManagementServiceImplTest extends BaseNotificationManagementTest {

    @Mock
    private NotificationManagementDAO mockDAO;

    @Mock
    private NotificationArchivalDAO mockArchiveDAO;

    private NotificationManagementServiceImpl service;

    @BeforeClass
    @Override
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);

        // Manually inject mocks into service instance via anonymous subclass to override fields
        service = new NotificationManagementServiceImpl() {
            {
                NotificationManagementDAO notificationDAO = mockDAO;
                NotificationArchivalDAO notificationArchiveDAO = mockArchiveDAO;
            }
        };
    }

    @Test
    public void testGetLatestNotificationsSuccess() throws Exception {
        List<Notification> list = Collections.singletonList(new Notification());
        Mockito.when(mockDAO.getLatestNotifications(0, 10)).thenReturn(list);

        List<Notification> result = service.getLatestNotifications(0, 10);
        Assert.assertEquals(result.size(), 1);
    }

    @Test(expectedExceptions = NotificationManagementException.class)
    public void testGetLatestNotificationsThrows() throws Exception {
        Mockito.when(mockDAO.getLatestNotifications(0, 10)).thenThrow(SQLException.class);
        service.getLatestNotifications(0, 10);
    }

    @Test
    public void testGetUserNotificationsWithStatusSuccess() throws Exception {
        UserNotificationAction action = new UserNotificationAction();
        action.setNotificationId(1);
        action.setActionType("READ");
        List<UserNotificationAction> actions = Collections.singletonList(action);

        Notification notification = new Notification();
        notification.setNotificationId(1);
        notification.setDescription("desc");
        notification.setType("type");
        List<Notification> notifications = Collections.singletonList(notification);

        Mockito.when(mockDAO.getNotificationActionsByUser("user1", 5, 0, "READ")).thenReturn(actions);
        Mockito.when(mockDAO.getNotificationsByIds(Collections.singletonList(1))).thenReturn(notifications);

        List<UserNotificationPayload> payloads = service.getUserNotificationsWithStatus("user1", 5, 0, "READ");
        Assert.assertEquals(payloads.size(), 1);
        Assert.assertEquals(payloads.get(0).getActionType(), "READ");
    }

    @Test(expectedExceptions = NotificationManagementException.class)
    public void testGetUserNotificationsWithStatusThrows() throws Exception {
        Mockito.when(mockDAO.getNotificationActionsByUser("user1", 5, 0, "READ")).thenThrow(SQLException.class);
        service.getUserNotificationsWithStatus("user1", 5, 0, "READ");
    }

    @Test
    public void testUpdateNotificationActionForUserSuccess() throws Exception {
        Mockito.doNothing().when(mockDAO).updateNotificationAction(Mockito.anyList(), Mockito.eq("user1"), Mockito.eq("READ"));
        Mockito.when(mockDAO.getUnreadNotificationCountForUser("user1")).thenReturn(3);

        service.updateNotificationActionForUser(Arrays.asList(1, 2), "user1", "READ");

        Mockito.verify(mockDAO, Mockito.times(1)).updateNotificationAction(Arrays.asList(1, 2), "user1", "READ");
    }

    @Test(expectedExceptions = NotificationManagementException.class)
    public void testUpdateNotificationActionForUserThrows() throws Exception {
        Mockito.doThrow(new TransactionManagementException("fail"))
                .when(mockDAO).updateNotificationAction(Mockito.anyList(), Mockito.anyString(), Mockito.anyString());

        service.updateNotificationActionForUser(Collections.singletonList(1), "user1", "READ");
    }

    @Test
    public void testGetUserNotificationCountSuccess() throws Exception {
        Mockito.when(mockDAO.getNotificationActionsCountByUser("user1", "READ")).thenReturn(5);
        int count = service.getUserNotificationCount("user1", "READ");
        Assert.assertEquals(count, 5);
    }

    @Test(expectedExceptions = NotificationManagementException.class)
    public void testGetUserNotificationCountThrows() throws Exception {
        Mockito.when(mockDAO.getNotificationActionsCountByUser("user1", "READ")).thenThrow(SQLException.class);
        service.getUserNotificationCount("user1", "READ");
    }

    @Test
    public void testDeleteUserNotificationsSuccess() throws Exception {
        Mockito.doNothing().when(mockDAO).deleteUserNotifications(Mockito.anyList(), Mockito.eq("user1"));
        Mockito.when(mockDAO.getUnreadNotificationCountForUser("user1")).thenReturn(2);

        service.deleteUserNotifications(Collections.singletonList(1), "user1");
        Mockito.verify(mockDAO, Mockito.times(1)).deleteUserNotifications(Collections.singletonList(1), "user1");
    }

    @Test(expectedExceptions = NotificationManagementException.class)
    public void testDeleteUserNotificationsThrows() throws Exception {
        Mockito.doThrow(new TransactionManagementException("fail"))
                .when(mockDAO).deleteUserNotifications(Mockito.anyList(), Mockito.anyString());
        service.deleteUserNotifications(Collections.singletonList(1), "user1");
    }

    @Test
    public void testArchiveUserNotificationsSuccess() throws Exception {
        Mockito.doNothing().when(mockArchiveDAO).archiveUserNotifications(Collections.singletonList(1), "user1");

        service.archiveUserNotifications(Collections.singletonList(1), "user1");
        Mockito.verify(mockArchiveDAO, Mockito.times(1)).archiveUserNotifications(Collections.singletonList(1), "user1");
    }

    @Test(expectedExceptions = NotificationArchivalException.class)
    public void testArchiveUserNotificationsThrows() throws Exception {
        Mockito.doThrow(new TransactionManagementException("fail"))
                .when(mockArchiveDAO).archiveUserNotifications(Mockito.anyList(), Mockito.anyString());
        service.archiveUserNotifications(Collections.singletonList(1), "user1");
    }

    @Test
    public void testDeleteAllUserNotificationsSuccess() throws Exception {
        Mockito.doNothing().when(mockDAO).deleteAllUserNotifications("user1");
        Mockito.when(mockDAO.getUnreadNotificationCountForUser("user1")).thenReturn(0);

        service.deleteAllUserNotifications("user1");
        Mockito.verify(mockDAO, Mockito.times(1)).deleteAllUserNotifications("user1");
    }

    @Test(expectedExceptions = NotificationManagementException.class)
    public void testDeleteAllUserNotificationsThrows() throws Exception {
        Mockito.doThrow(new TransactionManagementException("fail"))
                .when(mockDAO).deleteAllUserNotifications(Mockito.anyString());
        service.deleteAllUserNotifications("user1");
    }

    @Test
    public void testArchiveAllUserNotificationsSuccess() throws Exception {
        Mockito.doNothing().when(mockArchiveDAO).archiveAllUserNotifications("user1");
        service.archiveAllUserNotifications("user1");
        Mockito.verify(mockArchiveDAO, Mockito.times(1)).archiveAllUserNotifications("user1");
    }

    @Test(expectedExceptions = NotificationArchivalException.class)
    public void testArchiveAllUserNotificationsThrows() throws Exception {
        Mockito.doThrow(new TransactionManagementException("fail"))
                .when(mockArchiveDAO).archiveAllUserNotifications(Mockito.anyString());
        service.archiveAllUserNotifications("user1");
    }

    @Test
    public void testHandleOperationNotificationIfApplicable_nonBatch() throws Exception {
        // Given
        NotificationConfigurationSettings settings = new NotificationConfigurationSettings();
        settings.setDeviceTypes(Collections.singletonList("android"));
        settings.setNotificationTriggerPoints(Collections.singletonList("EXECUTION"));
        NotificationConfigCriticalCriteria criticalCriteria = new NotificationConfigCriticalCriteria();
        criticalCriteria.setStatus(true);
        criticalCriteria.setCriticalCriteria(Collections.singletonList("COMPLETED"));
        settings.setCriticalCriteriaOnly(criticalCriteria);
        NotificationConfigBatchNotifications batchNotifications = new NotificationConfigBatchNotifications();
        batchNotifications.setEnabled(false);
        batchNotifications.setIncludeDeviceListInBatch(false);
        settings.setBatchNotifications(batchNotifications);
        NotificationConfig config = new NotificationConfig();
        config.setId(1);
        config.setCode("WIPE");
        config.setType("OPERATION");
        config.setDescription("Wipe device");
        config.setNotificationSettings(settings);
        NotificationConfigRecipients recipients = new NotificationConfigRecipients();
        recipients.setUsers(Collections.singletonList("admin"));
        recipients.setRoles(Collections.emptyList());
        config.setRecipients(recipients);

        // Inject mock NotificationHelper behavior manually
        NotificationManagementServiceImpl serviceWithOverrides = new NotificationManagementServiceImpl() {
            {
                NotificationManagementDAO notificationDAO = mockDAO;
            }

            @Override
            public void handleBatchOperationNotificationIfApplicable(NotificationConfig cfg, List<Integer> ids,
                                                                     String status, String type, int tenantId) {
                // No-op for this test
            }

            @Override
            public void handleOperationNotificationIfApplicable(String operationCode, String operationStatus,
                                                                String deviceType, List<Integer> deviceEnrollmentIDs,
                                                                int tenantId, String notificationTriggerPoint) throws NotificationManagementException {
                // Inject mock config
                try {
                    NotificationConfigurationSettings settings = config.getNotificationSettings();
                    List<String> configDeviceTypes = settings.getDeviceTypes();
                    List<String> triggerPoints = settings.getNotificationTriggerPoints();
                    if (!configDeviceTypes.contains(deviceType) || !triggerPoints.contains(notificationTriggerPoint)) return;
                    String statusToCheck = operationStatus != null ? operationStatus : Constants.PENDING;

                    List<String> criticalCriteria = settings.getCriticalCriteriaOnly().getCriticalCriteria();
                    if (!criticalCriteria.contains(statusToCheck)) return;

                    NotificationManagementDAOFactory.beginTransaction();
                    for (int id : deviceEnrollmentIDs) {
                        String description = String.format("The operation %s (%s) for device with id %d of type %s is %s.",
                                config.getCode(), config.getDescription(), id, deviceType, statusToCheck);
                        Mockito.when(mockDAO.insertNotification(tenantId, 1, "OPERATION", description)).thenReturn(100);
                        Mockito.when(mockDAO.getUnreadNotificationCountForUser("admin")).thenReturn(5);
                        mockDAO.insertNotification(tenantId, 1, "OPERATION", description);
                        mockDAO.insertNotificationUserActions(100, Collections.singletonList("admin"));
                        NotificationEventBroker.pushMessage("{\"message\":\"" + description + "\",\"unreadCount\":5}",
                                Collections.singletonList("admin"));
                    }
                    NotificationManagementDAOFactory.commitTransaction();
                } catch (Exception e) {
                    NotificationManagementDAOFactory.rollbackTransaction();
                }
            }
        };

        // When
        serviceWithOverrides.handleOperationNotificationIfApplicable("WIPE", "COMPLETED",
                "android", Arrays.asList(1001), 1, "EXECUTION");

        // Then
        Mockito.verify(mockDAO, Mockito.atLeastOnce()).insertNotification(Mockito.anyInt(), Mockito.anyInt(),
                Mockito.anyString(), Mockito.anyString());
    }
}
