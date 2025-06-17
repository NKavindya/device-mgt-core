/*
 * Copyright (c) 2018 - 2023, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
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

import io.entgra.device.mgt.core.notification.mgt.core.common.BaseNotificationManagementTest;
import io.entgra.device.mgt.core.notification.mgt.core.dto.Notification;
import io.entgra.device.mgt.core.notification.mgt.core.exception.NotificationManagementException;
import io.entgra.device.mgt.core.notification.mgt.core.service.NotificationArchivalService;
import io.entgra.device.mgt.core.notification.mgt.core.service.NotificationManagementService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Test class for NotificationArchivalServiceImpl
 */
public class NotificationArchivalServiceImplTest extends BaseNotificationManagementTest {

    private static final Log log = LogFactory.getLog(NotificationArchivalServiceImplTest.class);
    private NotificationArchivalService notificationArchivalService;
    private NotificationManagementService notificationManagementService;
    private static final String TEST_DEVICE_ID = "test-device-001";
    private static final String TEST_DEVICE_TYPE = "test-device-type";
    private static final String TEST_NOTIFICATION_TYPE = "ALERT";
    private static final String TEST_NOTIFICATION_MESSAGE = "Test notification message";

    @BeforeClass
    public void init() throws Exception {
        super.init();
        notificationArchivalService = new NotificationArchivalServiceImpl();
        notificationManagementService = new NotificationManagementServiceImpl();
    }

    @Test(description = "Test archiving old notifications")
    public void testArchiveOldNotifications() throws NotificationManagementException {
        // First add a notification
        Notification notification = new Notification();
        notification.setDeviceId(TEST_DEVICE_ID);
        notification.setDeviceType(TEST_DEVICE_TYPE);
        notification.setType(TEST_NOTIFICATION_TYPE);
        notification.setMessage(TEST_NOTIFICATION_MESSAGE);
        notificationManagementService.addNotification(notification);

        // Archive old notifications
        int archivedCount = notificationArchivalService.archiveOldNotifications();
        Assert.assertTrue(archivedCount >= 0, "Archived count should be non-negative");
    }

    @Test(description = "Test getting archived notifications", dependsOnMethods = "testArchiveOldNotifications")
    public void testGetArchivedNotifications() throws NotificationManagementException {
        List<Notification> archivedNotifications = notificationArchivalService.getArchivedNotifications(TEST_DEVICE_ID, TEST_DEVICE_TYPE);
        Assert.assertNotNull(archivedNotifications, "Archived notifications list should not be null");
    }

    @Test(description = "Test getting archived notifications with pagination", dependsOnMethods = "testGetArchivedNotifications")
    public void testGetArchivedNotificationsWithPagination() throws NotificationManagementException {
        List<Notification> archivedNotifications = notificationArchivalService.getArchivedNotifications(TEST_DEVICE_ID, TEST_DEVICE_TYPE, 0, 10);
        Assert.assertNotNull(archivedNotifications, "Archived notifications list should not be null");
    }

    @Test(description = "Test getting archived notifications by date range", dependsOnMethods = "testGetArchivedNotificationsWithPagination")
    public void testGetArchivedNotificationsByDateRange() throws NotificationManagementException {
        long startTime = System.currentTimeMillis() - 86400000; // 24 hours ago
        long endTime = System.currentTimeMillis();
        
        List<Notification> archivedNotifications = notificationArchivalService.getArchivedNotificationsByDateRange(
                TEST_DEVICE_ID, TEST_DEVICE_TYPE, startTime, endTime);
        Assert.assertNotNull(archivedNotifications, "Archived notifications list should not be null");
    }

    @Test(description = "Test restoring archived notification", dependsOnMethods = "testGetArchivedNotificationsByDateRange")
    public void testRestoreArchivedNotification() throws NotificationManagementException {
        List<Notification> archivedNotifications = notificationArchivalService.getArchivedNotifications(TEST_DEVICE_ID, TEST_DEVICE_TYPE);
        if (!archivedNotifications.isEmpty()) {
            int notificationId = archivedNotifications.get(0).getId();
            boolean result = notificationArchivalService.restoreArchivedNotification(notificationId);
            Assert.assertTrue(result, "Should successfully restore archived notification");
        }
    }

    @Test(description = "Test deleting archived notification", dependsOnMethods = "testRestoreArchivedNotification")
    public void testDeleteArchivedNotification() throws NotificationManagementException {
        List<Notification> archivedNotifications = notificationArchivalService.getArchivedNotifications(TEST_DEVICE_ID, TEST_DEVICE_TYPE);
        if (!archivedNotifications.isEmpty()) {
            int notificationId = archivedNotifications.get(0).getId();
            boolean result = notificationArchivalService.deleteArchivedNotification(notificationId);
            Assert.assertTrue(result, "Should successfully delete archived notification");
        }
    }

    @Test(description = "Test getting archived notification count", dependsOnMethods = "testDeleteArchivedNotification")
    public void testGetArchivedNotificationCount() throws NotificationManagementException {
        int count = notificationArchivalService.getArchivedNotificationCount(TEST_DEVICE_ID, TEST_DEVICE_TYPE);
        Assert.assertTrue(count >= 0, "Archived notification count should be non-negative");
    }
} 