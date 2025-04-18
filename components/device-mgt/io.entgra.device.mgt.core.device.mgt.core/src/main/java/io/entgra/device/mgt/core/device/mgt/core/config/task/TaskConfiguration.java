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


/**
 * This class will read the configurations related to task. This task will be responsible for adding the operations.
 */
package io.entgra.device.mgt.core.device.mgt.core.config.task;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "TaskConfiguration")
public class TaskConfiguration {


    private boolean enabled;
    private int frequency;
    private String taskClazz;
    private List<Operation> operations;

    @XmlElement(name = "Enable", required = true)
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @XmlElement(name = "Frequency", required = true)
    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    @XmlElement(name = "TaskClass", required = true)
    public String getTaskClazz() {
        return taskClazz;
    }

    public void setTaskClazz(String taskClazz) {
        this.taskClazz = taskClazz;
    }

    @XmlElementWrapper(name="Operations")
    @XmlElement(name = "Operation", required = true)
    public List<Operation> getOperations() {
        return operations;
    }

    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }

    @XmlRootElement(name = "Operation")
    public static class Operation {


        private String operationName;
        private int recurrency;
        private List<String> platforms;


        @XmlElement(name = "Name", required = true)
        public String getOperationName() {
            return operationName;
        }

        public void setOperationName(String operationName) {
            this.operationName = operationName;
        }

        @XmlElement(name = "RecurrentTimes", required = true)
        public int getRecurrency() {
            return recurrency;
        }

        public void setRecurrency(int recurrency) {
            this.recurrency = recurrency;
        }

        @XmlElementWrapper(name = "Platforms")
        @XmlElement(name = "platform", required = true)
        public List<String> getPlatforms() {
            return platforms;
        }

        public void setPlatforms(List<String> platforms) {
            this.platforms = platforms;
        }

    }
}
