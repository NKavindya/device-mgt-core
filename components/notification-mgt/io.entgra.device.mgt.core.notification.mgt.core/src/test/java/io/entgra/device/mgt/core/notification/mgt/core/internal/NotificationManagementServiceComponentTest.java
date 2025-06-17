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

package io.entgra.device.mgt.core.notification.mgt.core.internal;

import io.entgra.device.mgt.core.notification.mgt.common.exception.NotificationConfigurationServiceException;

import io.entgra.device.mgt.core.notification.mgt.core.config.NotificationConfigurationManager;
import io.entgra.device.mgt.core.notification.mgt.core.config.archive.NotificationArchiveConfigManager;
import io.entgra.device.mgt.core.notification.mgt.core.config.archive.datasource.NotificationArchiveRepository;
import io.entgra.device.mgt.core.notification.mgt.core.config.datasource.NotificationDatasourceConfiguration;
import io.entgra.device.mgt.core.notification.mgt.core.config.datasource.NotificationManagementRepository;
import io.entgra.device.mgt.core.notification.mgt.core.exception.NotificationArchivalTaskManagerException;
import io.entgra.device.mgt.core.notification.mgt.core.impl.NotificationConfigServiceImpl;
import io.entgra.device.mgt.core.notification.mgt.core.task.NotificationArchivalTaskManagerImpl;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.powermock.api.mockito.PowerMockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NotificationManagementServiceComponentTest {

    private NotificationManagementServiceComponent component;

    @Mock
    private ComponentContext componentContext;

    @Mock
    private BundleContext bundleContext;

    @Mock
    private NotificationConfigurationManager configManager;

    @Mock
    private NotificationArchiveConfigManager archiveConfigManager;

    @Mock
    private NotificationManagementRepository notificationRepo;

    @Mock
    private NotificationArchiveRepository archiveRepo;

    @Mock
    private NotificationDatasourceConfiguration dataSource;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        component = new NotificationManagementServiceComponent();
        when(componentContext.getBundleContext()).thenReturn(bundleContext);
        when(notificationRepo.getDataSourceConfig()).thenReturn(dataSource);
        when(archiveRepo.getDataSourceConfig()).thenReturn(dataSource);
        PowerMockito.mockStatic(NotificationConfigurationManager.class);
        PowerMockito.when(NotificationConfigurationManager.getInstance()).thenReturn(configManager);
        when(configManager.getNotificationManagementRepository()).thenReturn(notificationRepo);
        PowerMockito.mockStatic(NotificationArchiveConfigManager.class);
        PowerMockito.when(NotificationArchiveConfigManager.getInstance()).thenReturn(archiveConfigManager);
        when(archiveConfigManager.getNotificationArchiveRepository()).thenReturn(archiveRepo);
    }

    @Test
    public void testActivate_success() throws Exception {
        component.activate(componentContext);
        verify(bundleContext, atLeastOnce()).registerService(anyString(), any(), isNull());
    }

    @Test
    public void testActivate_metadataSettingFails() throws Exception {
        PowerMockito.spy(NotificationConfigServiceImpl.class);
        NotificationConfigServiceImpl mockConfigService = PowerMockito.mock(NotificationConfigServiceImpl.class);
        doThrow(new NotificationConfigurationServiceException("Simulated failure"))
                .when(mockConfigService).setDefaultNotificationArchiveMetadata(anyString(), anyString());
        component.activate(componentContext);
    }

    @Test
    public void testActivate_taskAlreadyScheduled() throws Exception {
        PowerMockito.spy(NotificationArchivalTaskManagerImpl.class);
        NotificationArchivalTaskManagerImpl mockTaskMgr = PowerMockito.mock(NotificationArchivalTaskManagerImpl.class);
        doThrow(new NotificationArchivalTaskManagerException("Notification archival task is already active for tenant"))
                .when(mockTaskMgr).startTask();
        component.activate(componentContext);
    }

    @Test
    public void testDeactivate() {
        component.deactivate(componentContext);
    }
}
