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

import io.entgra.device.mgt.core.device.mgt.common.metadata.mgt.MetadataManagementService;
import io.entgra.device.mgt.core.notification.mgt.common.beans.NotificationConfig;
import io.entgra.device.mgt.core.notification.mgt.common.beans.NotificationConfigurationList;
import io.entgra.device.mgt.core.notification.mgt.common.beans.NotificationConfigurationSettings;
import io.entgra.device.mgt.core.notification.mgt.common.exception.NotificationArchivalException;
import io.entgra.device.mgt.core.notification.mgt.core.dao.NotificationArchivalDAO;
import io.entgra.device.mgt.core.notification.mgt.core.internal.NotificationManagementDataHolder;
import io.entgra.device.mgt.core.notification.mgt.core.util.NotificationHelper;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anySet;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for NotificationArchivalServiceImpl
 */
public class NotificationArchivalServiceImplTest {

    @Mock
    private NotificationArchivalDAO archivalDAOMock;

    @Mock
    private NotificationArchivalDAO deleteDAOMock;

    @Mock
    private MetadataManagementService metadataServiceMock;

    private NotificationArchivalServiceImpl service;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        NotificationManagementDataHolder.getInstance().setMetaDataManagementService(metadataServiceMock);

        service = new NotificationArchivalServiceImpl() {
//            @Override
            protected NotificationConfigurationList getNotificationConfigurationList(int tenantId)
                    throws NotificationArchivalException {
                NotificationConfigurationList configList = new NotificationConfigurationList();
                configList.setDefaultArchiveAfter("6 days");
                configList.setDefaultArchiveType("default");

                NotificationConfig config = new NotificationConfig();
                config.setId(1);
                NotificationConfigurationSettings settings = new NotificationConfigurationSettings();
                settings.setArchiveType("default");
                settings.setArchiveAfter("7 days");
                config.setNotificationSettings(settings);
                configList.setNotificationConfigurations(Collections.singletonList(config));

                NotificationHelper.setDefaultArchivalValuesIfAbsent(configList);
                return configList;
            }

//            @Override
            protected NotificationArchivalDAO getNotificationArchivalDAO() {
                return archivalDAOMock;
            }

//            @Override
            protected NotificationArchivalDAO getDeleteArchivalDAO() {
                return deleteDAOMock;
            }
        };
    }

    @Test
    public void testArchiveOldNotifications_success() throws Exception {
        int tenantId = 1;
        Timestamp expectedCutoff = NotificationHelper.resolveCutoffTimestamp("7 days");

        when(archivalDAOMock.moveNotificationsToArchiveByConfig(expectedCutoff, tenantId, 1))
                .thenReturn(Arrays.asList(100, 200));
        doNothing().when(archivalDAOMock).moveUserActionsToArchive(anyList());
        doNothing().when(archivalDAOMock).deleteOldNotificationsByConfig(expectedCutoff, tenantId, 1);
        when(archivalDAOMock.moveNotificationsToArchiveExcludingConfigs(any(), eq(tenantId), anySet()))
                .thenReturn(Collections.singletonList(300));

        service.archiveOldNotifications(tenantId);

        verify(archivalDAOMock).moveNotificationsToArchiveByConfig(expectedCutoff, tenantId, 1);
        verify(archivalDAOMock).moveUserActionsToArchive(anyList());
        verify(archivalDAOMock).deleteOldNotificationsByConfig(expectedCutoff, tenantId, 1);
    }

    @Test
    public void testDeleteExpiredArchivedNotifications_success() throws Exception {
        int tenantId = 1;
        Timestamp cutoff = NotificationHelper.resolveCutoffTimestamp("5 years");

        doNothing().when(deleteDAOMock).deleteExpiredArchivedNotifications(any(Timestamp.class), eq(tenantId));

        service.deleteExpiredArchivedNotifications(tenantId);

        verify(deleteDAOMock).deleteExpiredArchivedNotifications(any(Timestamp.class), eq(tenantId));
    }

    @Test(expectedExceptions = NotificationArchivalException.class)
    public void testArchiveOldNotifications_error() throws Exception {
        NotificationArchivalServiceImpl faultyService = new NotificationArchivalServiceImpl() {
            @Override
            public void archiveOldNotifications(int tenantId) throws NotificationArchivalException {
                throw new NotificationArchivalException("Simulated failure");
            }
        };
        faultyService.archiveOldNotifications(1);
    }

    @Test(expectedExceptions = NotificationArchivalException.class)
    public void testDeleteExpiredArchivedNotifications_error() throws Exception {
        NotificationArchivalServiceImpl faultyService = new NotificationArchivalServiceImpl() {
            @Override
            public void deleteExpiredArchivedNotifications(int tenantId) throws NotificationArchivalException {
                throw new NotificationArchivalException("Simulated delete failure");
            }
        };
        faultyService.deleteExpiredArchivedNotifications(1);
    }
}
