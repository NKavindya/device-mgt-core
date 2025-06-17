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
import io.entgra.device.mgt.core.notification.mgt.core.service.NotificationManagementService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for NotificationManagementServiceImpl
 */
public class NotificationManagementServiceImplTest extends BaseNotificationManagementTest {

    private static final Log log = LogFactory.getLog(NotificationManagementServiceImplTest.class);
    private NotificationManagementService notificationManagementService;
    private static final String TEST_DEVICE_ID = "test-device-001";
    private static final String TEST_DEVICE_TYPE = "test-device-type";
    private static final String TEST_NOTIFICATION_TYPE = "ALERT";
    private static final String TEST_NOTIFICATION_MESSAGE = "Test notification message";

    @BeforeClass
    public void init() throws Exception {
        super.init();
        notificationManagementService = new NotificationManagementServiceImpl();
    }

    @Test(description = "Test adding a notification")
    public void testAddNotification() throws NotificationManagementException {
        Notification notification = new Notification();
        notification.setDeviceId(TEST_DEVICE_ID);
        notification.setDeviceType(TEST_DEVICE_TYPE);
        notification.setType(TEST_NOTIFICATION_TYPE);
        notification.setMessage(TEST_NOTIFICATION_MESSAGE);
        
        int notificationId = notificationManagementService.addNotification(notification);
        Assert.assertTrue(notificationId > 0, "Notification ID should be greater than 0");
    }

    @Test(description = "Test getting notifications for a device", dependsOnMethods = "testAddNotification")
    public void testGetNotifications() throws NotificationManagementException {
        List<Notification> notifications = notificationManagementService.getNotifications(TEST_DEVICE_ID, TEST_DEVICE_TYPE);
        Assert.assertNotNull(notifications, "Notifications list should not be null");
        Assert.assertFalse(notifications.isEmpty(), "Notifications list should not be empty");
        
        Notification notification = notifications.get(0);
        Assert.assertEquals(notification.getDeviceId(), TEST_DEVICE_ID);
        Assert.assertEquals(notification.getDeviceType(), TEST_DEVICE_TYPE);
        Assert.assertEquals(notification.getType(), TEST_NOTIFICATION_TYPE);
        Assert.assertEquals(notification.getMessage(), TEST_NOTIFICATION_MESSAGE);
    }

    @Test(description = "Test marking notification as read", dependsOnMethods = "testGetNotifications")
    public void testMarkNotificationAsRead() throws NotificationManagementException {
        List<Notification> notifications = notificationManagementService.getNotifications(TEST_DEVICE_ID, TEST_DEVICE_TYPE);
        int notificationId = notifications.get(0).getId();
        
        boolean result = notificationManagementService.markNotificationAsRead(notificationId);
        Assert.assertTrue(result, "Should successfully mark notification as read");
        
        notifications = notificationManagementService.getNotifications(TEST_DEVICE_ID, TEST_DEVICE_TYPE);
        Assert.assertTrue(notifications.get(0).isRead(), "Notification should be marked as read");
    }

    @Test(description = "Test getting unread notification count", dependsOnMethods = "testMarkNotificationAsRead")
    public void testGetUnreadNotificationCount() throws NotificationManagementException {
        int count = notificationManagementService.getUnreadNotificationCount(TEST_DEVICE_ID, TEST_DEVICE_TYPE);
        Assert.assertEquals(count, 0, "Unread notification count should be 0");
    }

    @Test(description = "Test getting notifications with pagination", dependsOnMethods = "testGetUnreadNotificationCount")
    public void testGetNotificationsWithPagination() throws NotificationManagementException {
        List<Notification> notifications = notificationManagementService.getNotifications(TEST_DEVICE_ID, TEST_DEVICE_TYPE, 0, 10);
        Assert.assertNotNull(notifications, "Notifications list should not be null");
        Assert.assertFalse(notifications.isEmpty(), "Notifications list should not be empty");
    }

    @Test(description = "Test getting notifications by type", dependsOnMethods = "testGetNotificationsWithPagination")
    public void testGetNotificationsByType() throws NotificationManagementException {
        List<Notification> notifications = notificationManagementService.getNotificationsByType(TEST_DEVICE_ID, TEST_DEVICE_TYPE, TEST_NOTIFICATION_TYPE);
        Assert.assertNotNull(notifications, "Notifications list should not be null");
        Assert.assertFalse(notifications.isEmpty(), "Notifications list should not be empty");
        Assert.assertEquals(notifications.get(0).getType(), TEST_NOTIFICATION_TYPE);
    }

    @Test(description = "Test getting notifications by date range", dependsOnMethods = "testGetNotificationsByType")
    public void testGetNotificationsByDateRange() throws NotificationManagementException {
        long startTime = System.currentTimeMillis() - 86400000; // 24 hours ago
        long endTime = System.currentTimeMillis();
        
        List<Notification> notifications = notificationManagementService.getNotificationsByDateRange(TEST_DEVICE_ID, TEST_DEVICE_TYPE, startTime, endTime);
        Assert.assertNotNull(notifications, "Notifications list should not be null");
        Assert.assertFalse(notifications.isEmpty(), "Notifications list should not be empty");
    }

    @Test(description = "Test deleting a notification", dependsOnMethods = "testGetNotificationsByDateRange")
    public void testDeleteNotification() throws NotificationManagementException {
        List<Notification> notifications = notificationManagementService.getNotifications(TEST_DEVICE_ID, TEST_DEVICE_TYPE);
        int notificationId = notifications.get(0).getId();
        
        boolean result = notificationManagementService.deleteNotification(notificationId);
        Assert.assertTrue(result, "Should successfully delete notification");
        
        notifications = notificationManagementService.getNotifications(TEST_DEVICE_ID, TEST_DEVICE_TYPE);
        Assert.assertTrue(notifications.isEmpty(), "Notifications list should be empty after deletion");
    }
} 