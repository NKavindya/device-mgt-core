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
import org.wso2.carbon.ntask.core.TaskInfo;
import org.wso2.carbon.ntask.core.TaskManager;
import org.wso2.carbon.ntask.core.impl.LocalTaskActionListener;

import java.util.ArrayList;
import java.util.List;

public class TestTaskManagerImpl implements TaskManager {
    private List<TaskInfo> registeredTasks;

    public TestTaskManagerImpl() {
        this.registeredTasks = new ArrayList<>();
    }

    @Override
    public void initStartupTasks() throws TaskException {

    }

    @Override
    public void scheduleTask(String taskName) throws TaskException {

    }

    @Override
    public void rescheduleTask(String taskName) throws TaskException {

    }

    @Override
    public boolean deleteTask(String taskName) throws TaskException {
        for (TaskInfo task : this.registeredTasks) {
            if (taskName.equals(task.getName())) {
                this.registeredTasks.remove(task);
                return true;
            }
        }
        return false;
    }

    @Override
    public void pauseTask(String taskName) throws TaskException {

    }

    @Override
    public void resumeTask(String taskName) throws TaskException {

    }

    @Override
    public void registerTask(TaskInfo taskInfo) throws TaskException {
        this.registeredTasks.add(taskInfo);
    }

    @Override
    public TaskState getTaskState(String taskName) throws TaskException {
        return null;
    }

    @Override
    public TaskInfo getTask(String taskName) throws TaskException {
        for (TaskInfo task : this.registeredTasks) {
            if (taskName.contains(task.getName())) {
                return task;
            }
        }
        return null;
    }

    @Override
    public List<TaskInfo> getAllTasks() throws TaskException {
        return this.registeredTasks;
    }

    @Override
    public boolean isTaskScheduled(String taskName) throws TaskException {
        return this.registeredTasks.size() > 0;
    }

    @Override
    public void registerLocalTaskActionListener(LocalTaskActionListener localTaskActionListener, String s) {

    }
}
