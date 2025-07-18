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
import io.entgra.device.mgt.core.notification.mgt.common.exception.NotificationManagementException;
import io.entgra.device.mgt.core.notification.mgt.core.dao.NotificationArchivalDAO;
import io.entgra.device.mgt.core.notification.mgt.core.dao.NotificationManagementDAO;
import io.entgra.device.mgt.core.notification.mgt.core.dao.factory.NotificationManagementDAOFactory;
import io.entgra.device.mgt.core.notification.mgt.core.dao.factory.archive.NotificationArchivalDestDAOFactory;
import io.entgra.device.mgt.core.notification.mgt.core.dao.factory.archive.NotificationArchivalSourceDAOFactory;
import io.entgra.device.mgt.core.notification.mgt.core.internal.NotificationManagementDataHolder;
import io.entgra.device.mgt.core.notification.mgt.core.util.NotificationEventBroker;
import io.entgra.device.mgt.core.notification.mgt.core.util.NotificationHelper;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.contains;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.testng.AssertJUnit.assertEquals;

/**
 * Test class for NotificationManagementServiceImpl
 */
@PrepareForTest({
        NotificationManagementDAOFactory.class,
        NotificationArchivalDestDAOFactory.class,
        NotificationArchivalSourceDAOFactory.class,
        NotificationEventBroker.class,
        NotificationHelper.class,
        NotificationManagementDataHolder.class
})
public class NotificationManagementServiceImplTest extends PowerMockTestCase {

    @Mock
    private NotificationManagementDAO notificationDAOMock;

    @Mock
    private NotificationArchivalDAO notificationArchivalDAOMock;

    @Mock
    private NotificationManagementServiceImpl service;

    @BeforeMethod
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockStatic(NotificationManagementDAOFactory.class);
        mockStatic(NotificationArchivalDestDAOFactory.class);
        mockStatic(NotificationArchivalSourceDAOFactory.class);
        mockStatic(NotificationEventBroker.class);
        mockStatic(NotificationHelper.class);
        when(NotificationManagementDAOFactory
                .getNotificationManagementDAO()).thenReturn(notificationDAOMock);
        when(NotificationArchivalDestDAOFactory
                .getNotificationArchivalDAO()).thenReturn(notificationArchivalDAOMock);
        service = new NotificationManagementServiceImpl(notificationDAOMock, notificationArchivalDAOMock);
    }

    @Test
    public void testGetLatestNotifications_success() throws Exception {
        List<Notification> mockList = Collections.singletonList(new Notification());
        when(notificationDAOMock.getLatestNotifications(0, 10)).thenReturn(mockList);
        List<Notification> result = service.getLatestNotifications(0, 10);
        assertEquals(result, mockList);
    }

    @Test(expectedExceptions = NotificationManagementException.class)
    public void testGetLatestNotifications_exception() throws Exception {
        when(notificationDAOMock.getLatestNotifications(anyInt(), anyInt()))
                .thenThrow(new NotificationManagementException());
        service.getLatestNotifications(0, 5);
    }

    @Test
    public void testGetUserNotificationsWithStatus_success() throws Exception {
        UserNotificationAction action = new UserNotificationAction();
        action.setNotificationId(1);
        action.setRead(false);
        Notification notification = new Notification();
        notification.setNotificationId(1);
        notification.setDescription("desc");
        notification.setType("type");
        notification.setCreatedTimestamp(new Timestamp(System.currentTimeMillis()));
        when(notificationDAOMock.getNotificationActionsByUser("user", 5, 0, false))
                .thenReturn(Collections.singletonList(action));
        when(notificationDAOMock.getNotificationsByIds(Collections.singletonList(1)))
                .thenReturn(Collections.singletonList(notification));
        List<UserNotificationPayload> result =
                service.getUserNotificationsWithStatus("user", 5, 0, false);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getActionType(), "UNREAD");
    }

    @Test
    public void testUpdateNotificationActionForUser_success() throws Exception {
        doNothing().when(notificationDAOMock)
                .updateNotificationAction(anyList(), eq("user"), eq("READ"));
        when(notificationDAOMock.getUnreadNotificationCountForUser("user")).thenReturn(5);
        PowerMockito.doNothing().when(NotificationEventBroker.class);
        NotificationEventBroker.pushMessage(anyString(), anyList());
        service.updateNotificationActionForUser(Arrays.asList(1, 2), "user", "READ");
        verify(notificationDAOMock).updateNotificationAction(anyList(), eq("user"), eq("READ"));
    }

    @Test
    public void testDeleteUserNotifications_success() throws Exception {
        doNothing().when(notificationDAOMock).deleteUserNotifications(anyList(), eq("user"));
        when(notificationDAOMock.getUnreadNotificationCountForUser("user")).thenReturn(3);
        PowerMockito.doNothing().when(NotificationEventBroker.class);
        NotificationEventBroker.pushMessage(anyString(), anyList());
        service.deleteUserNotifications(Arrays.asList(10), "user");
        verify(notificationDAOMock).deleteUserNotifications(anyList(), eq("user"));
    }

    @Test
    public void testArchiveUserNotifications_success() throws Exception {
        doNothing().when(notificationArchivalDAOMock).archiveUserNotifications(anyList(), eq("user"));
        service.archiveUserNotifications(Arrays.asList(1, 2, 3), "user");
        verify(notificationArchivalDAOMock).archiveUserNotifications(anyList(), eq("user"));
    }

    @Test
    public void testDeleteAllUserNotifications_success() throws Exception {
        doNothing().when(notificationDAOMock).deleteAllUserNotifications("user");
        when(notificationDAOMock.getUnreadNotificationCountForUser("user")).thenReturn(0);
        PowerMockito.doNothing().when(NotificationEventBroker.class);
        NotificationEventBroker.pushMessage(anyString(), anyList());
        service.deleteAllUserNotifications("user");
        verify(notificationDAOMock).deleteAllUserNotifications("user");
    }

    @Test
    public void testArchiveAllUserNotifications_success() throws Exception {
        doNothing().when(notificationArchivalDAOMock).archiveAllUserNotifications("user");
        service.archiveAllUserNotifications("user");
        verify(notificationArchivalDAOMock).archiveAllUserNotifications("user");
    }

    @Test
    public void testHandleOperationNotificationIfApplicable_success() throws Exception {
        NotificationConfig config = mock(NotificationConfig.class);
        NotificationConfigurationSettings settings = mock(NotificationConfigurationSettings.class);
        NotificationConfigCriticalCriteria critical = mock(NotificationConfigCriticalCriteria.class);
        NotificationConfigBatchNotifications batch = mock(NotificationConfigBatchNotifications.class);
        when(config.getNotificationSettings()).thenReturn(settings);
        when(config.getCode()).thenReturn("OP123");
        when(config.getDescription()).thenReturn("Sample Operation");
        when(config.getId()).thenReturn(1);
        when(config.getType()).thenReturn("operation");
        when(settings.getDeviceTypes()).thenReturn(Arrays.asList("android"));
        when(settings.getNotificationTriggerPoints()).thenReturn(Arrays.asList("OP_SUCCESS"));
        when(settings.getCriticalCriteriaOnly()).thenReturn(critical);
        when(settings.getBatchNotifications()).thenReturn(batch);
        when(critical.isStatus()).thenReturn(true);
        when(critical.getCriticalCriteria()).thenReturn(Arrays.asList("COMPLETED"));
        when(batch.isEnabled()).thenReturn(false);
        when(NotificationHelper.getNotificationConfigurationByCode("OP123")).thenReturn(config);
//        when(NotificationHelper.extractUsernamesFromRecipients(
//                any(NotificationConfigRecipients.class), anyInt()))
//                .thenReturn(Arrays.asList("admin"));
        mockStatic(NotificationManagementDAOFactory.class);
        when(NotificationManagementDAOFactory.getNotificationManagementDAO()).thenReturn(notificationDAOMock);
        PowerMockito.doNothing().when(NotificationManagementDAOFactory.class, "beginTransaction");
        PowerMockito.doNothing().when(NotificationManagementDAOFactory.class, "commitTransaction");
        PowerMockito.doNothing().when(NotificationManagementDAOFactory.class, "rollbackTransaction");
        PowerMockito.doNothing().when(NotificationManagementDAOFactory.class, "closeConnection");
        when(notificationDAOMock.insertNotification(anyInt(), anyInt(), anyString(),
                anyString())).thenReturn(1);
        when(notificationDAOMock.getUnreadNotificationCountForUser("admin")).thenReturn(2);
        doNothing().when(notificationDAOMock).insertNotificationUserActions(anyInt(), anyList());
//        doNothing().when(NotificationEventBroker.class);
//        NotificationEventBroker.pushMessage(anyString(), anyList());
        service.handleOperationNotificationIfApplicable("OP123", "COMPLETED", "android",
                Arrays.asList(101), 1, "OP_SUCCESS");
        verify(notificationDAOMock, times(1)).insertNotification(anyInt(),
                anyInt(), anyString(), anyString());
    }

}
