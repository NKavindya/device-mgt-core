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

package io.entgra.device.mgt.core.notification.mgt.core.task;

import io.entgra.device.mgt.core.notification.mgt.core.exception.NotificationArchivalTaskManagerException;
import io.entgra.device.mgt.core.notification.mgt.core.internal.NotificationManagementDataHolder;
import io.entgra.device.mgt.core.notification.mgt.core.util.Constants;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.ntask.common.TaskException;
import org.wso2.carbon.ntask.core.TaskInfo;
import org.wso2.carbon.ntask.core.TaskManager;
import org.wso2.carbon.ntask.core.service.TaskService;

import java.lang.reflect.Field;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

public class NotificationArchivalTaskManagerImplTest {

    @Mock
    private TaskService mockTaskService;

    @Mock
    private TaskManager mockTaskManager;

    private NotificationArchivalTaskManagerImpl taskManager;

    @BeforeMethod
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        taskManager = new NotificationArchivalTaskManagerImpl();
        // Mock TaskService and TaskManager setup
        when(mockTaskService.getTaskManager(Constants.NOTIFICATION_ARCHIVAL_TASK_TYPE)).thenReturn(mockTaskManager);
        when(mockTaskService.isServerInit()).thenReturn(true);
        // Inject mocked TaskService into NotificationManagementDataHolder
        NotificationManagementDataHolder dataHolder = NotificationManagementDataHolder.getInstance();
        Field field = NotificationManagementDataHolder.class.getDeclaredField("taskService");
        field.setAccessible(true);
        field.set(dataHolder, mockTaskService);
        // Setup Carbon context with mock tenant ID
        PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantId(1);
    }

    @Test
    public void testStartTask_SuccessfulScheduling() throws Exception {
        when(mockTaskManager.isTaskScheduled("NotificationArchivalTask_1")).thenReturn(false);
        taskManager.startTask();
        verify(mockTaskService).registerTaskType(Constants.NOTIFICATION_ARCHIVAL_TASK_TYPE);
        verify(mockTaskManager).registerTask(any(TaskInfo.class));
        verify(mockTaskManager).rescheduleTask(eq("NotificationArchivalTask_1"));
    }

    @Test(expectedExceptions = NotificationArchivalTaskManagerException.class)
    public void testStartTask_AlreadyScheduled() throws Exception {
        when(mockTaskManager.isTaskScheduled("NotificationArchivalTask_1")).thenReturn(true);
        taskManager.startTask();
    }

    @Test(expectedExceptions = NotificationArchivalTaskManagerException.class)
    public void testStartTask_TaskExceptionThrown() throws Exception {
        when(mockTaskManager.isTaskScheduled("NotificationArchivalTask_1"))
                .thenThrow(new TaskException("fail", TaskException.Code.UNKNOWN, new Exception()));
        taskManager.startTask();
    }

    @Test
    public void testStopTask_TaskExistsAndStopsSuccessfully() throws Exception {
        when(mockTaskManager.isTaskScheduled("NotificationArchivalTask_1")).thenReturn(true);
        taskManager.stopTask();
        verify(mockTaskManager).deleteTask("NotificationArchivalTask_1");
    }

    @Test
    public void testStopTask_TaskNotScheduled_NoAction() throws Exception {
        when(mockTaskManager.isTaskScheduled("NotificationArchivalTask_1")).thenReturn(false);
        taskManager.stopTask();
        verify(mockTaskManager, never()).deleteTask("NotificationArchivalTask_1");
    }

    @Test(expectedExceptions = NotificationArchivalTaskManagerException.class)
    public void testStopTask_TaskExceptionThrown() throws Exception {
        when(mockTaskManager.isTaskScheduled("NotificationArchivalTask_1"))
                .thenThrow(new TaskException("fail", TaskException.Code.UNKNOWN, new Exception()));
        taskManager.stopTask();
    }
}
