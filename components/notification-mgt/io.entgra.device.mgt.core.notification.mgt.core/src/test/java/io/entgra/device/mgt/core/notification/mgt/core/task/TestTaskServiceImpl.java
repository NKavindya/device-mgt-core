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

package io.entgra.device.mgt.core.notification.mgt.core.task;

import org.wso2.carbon.ntask.common.TaskException;
import org.wso2.carbon.ntask.core.TaskManager;
import org.wso2.carbon.ntask.core.service.TaskService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestTaskServiceImpl implements TaskService {
    private Set<String> registeredTaskTypes;
    private TaskManager taskManager;

    public TestTaskServiceImpl() {

        this.registeredTaskTypes = new HashSet<>();
        this.taskManager = new TestTaskManagerImpl();
    }

    @Override
    public TaskManager getTaskManager(String s) throws TaskException {
        return this.taskManager;
    }

    @Override
    public List<TaskManager> getAllTenantTaskManagersForType(String s) throws TaskException {
        return null;
    }

    @Override
    public void registerTaskType(String s) throws TaskException {
        this.registeredTaskTypes.add(s);
    }

    @Override
    public Set<String> getRegisteredTaskTypes() {
        return this.registeredTaskTypes;
    }

    @Override
    public void serverInitialized() {

    }

    @Override
    public boolean isServerInit() {
        return true;
    }

    @Override
    public TaskServiceConfiguration getServerConfiguration() {
        return null;
    }

    @Override
    public void runAfterRegistrationActions() throws TaskException {

    }

    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }
}
