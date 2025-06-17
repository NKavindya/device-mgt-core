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
import io.entgra.device.mgt.core.notification.mgt.core.dto.NotificationConfig;
import io.entgra.device.mgt.core.notification.mgt.core.exception.NotificationManagementException;
import io.entgra.device.mgt.core.notification.mgt.core.service.NotificationConfigService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Test class for NotificationConfigServiceImpl
 */
public class NotificationConfigServiceImplTest extends BaseNotificationManagementTest {

    private static final Log log = LogFactory.getLog(NotificationConfigServiceImplTest.class);
    private NotificationConfigService notificationConfigService;
    private static final String TEST_DEVICE_TYPE = "test-device-type";
    private static final String TEST_NOTIFICATION_TYPE = "ALERT";
    private static final boolean TEST_ENABLED = true;
    private static final int TEST_RETENTION_DAYS = 30;

    @BeforeClass
    public void init() throws Exception {
        super.init();
        notificationConfigService = new NotificationConfigServiceImpl();
    }

    @Test(description = "Test adding notification configuration")
    public void testAddNotificationConfig() throws NotificationManagementException {
        NotificationConfig config = new NotificationConfig();
        config.setDeviceType(TEST_DEVICE_TYPE);
        config.setNotificationType(TEST_NOTIFICATION_TYPE);
        config.setEnabled(TEST_ENABLED);
        config.setRetentionDays(TEST_RETENTION_DAYS);
        
        boolean result = notificationConfigService.addNotificationConfig(config);
        Assert.assertTrue(result, "Should successfully add notification configuration");
    }

    @Test(description = "Test getting notification configuration", dependsOnMethods = "testAddNotificationConfig")
    public void testGetNotificationConfig() throws NotificationManagementException {
        NotificationConfig config = notificationConfigService.getNotificationConfig(TEST_DEVICE_TYPE, TEST_NOTIFICATION_TYPE);
        Assert.assertNotNull(config, "Notification configuration should not be null");
        Assert.assertEquals(config.getDeviceType(), TEST_DEVICE_TYPE);
        Assert.assertEquals(config.getNotificationType(), TEST_NOTIFICATION_TYPE);
        Assert.assertEquals(config.isEnabled(), TEST_ENABLED);
        Assert.assertEquals(config.getRetentionDays(), TEST_RETENTION_DAYS);
    }

    @Test(description = "Test updating notification configuration", dependsOnMethods = "testGetNotificationConfig")
    public void testUpdateNotificationConfig() throws NotificationManagementException {
        NotificationConfig config = new NotificationConfig();
        config.setDeviceType(TEST_DEVICE_TYPE);
        config.setNotificationType(TEST_NOTIFICATION_TYPE);
        config.setEnabled(false);
        config.setRetentionDays(60);
        
        boolean result = notificationConfigService.updateNotificationConfig(config);
        Assert.assertTrue(result, "Should successfully update notification configuration");
        
        config = notificationConfigService.getNotificationConfig(TEST_DEVICE_TYPE, TEST_NOTIFICATION_TYPE);
        Assert.assertFalse(config.isEnabled(), "Notification should be disabled");
        Assert.assertEquals(config.getRetentionDays(), 60, "Retention days should be updated");
    }

    @Test(description = "Test getting all notification configurations", dependsOnMethods = "testUpdateNotificationConfig")
    public void testGetAllNotificationConfigs() throws NotificationManagementException {
        List<NotificationConfig> configs = notificationConfigService.getAllNotificationConfigs(TEST_DEVICE_TYPE);
        Assert.assertNotNull(configs, "Notification configurations list should not be null");
        Assert.assertFalse(configs.isEmpty(), "Notification configurations list should not be empty");
    }

    @Test(description = "Test deleting notification configuration", dependsOnMethods = "testGetAllNotificationConfigs")
    public void testDeleteNotificationConfig() throws NotificationManagementException {
        boolean result = notificationConfigService.deleteNotificationConfig(TEST_DEVICE_TYPE, TEST_NOTIFICATION_TYPE);
        Assert.assertTrue(result, "Should successfully delete notification configuration");
        
        List<NotificationConfig> configs = notificationConfigService.getAllNotificationConfigs(TEST_DEVICE_TYPE);
        Assert.assertTrue(configs.isEmpty(), "Notification configurations list should be empty after deletion");
    }

    @Test(description = "Test getting notification configuration for non-existent device type")
    public void testGetNotificationConfigForNonExistentDeviceType() throws NotificationManagementException {
        NotificationConfig config = notificationConfigService.getNotificationConfig("non-existent-type", TEST_NOTIFICATION_TYPE);
        Assert.assertNull(config, "Notification configuration should be null for non-existent device type");
    }
} 