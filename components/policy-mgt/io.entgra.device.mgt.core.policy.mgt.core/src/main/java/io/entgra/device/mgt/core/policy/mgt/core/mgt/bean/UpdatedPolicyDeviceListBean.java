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
package io.entgra.device.mgt.core.policy.mgt.core.mgt.bean;

import io.entgra.device.mgt.core.device.mgt.common.policy.mgt.Policy;

import java.util.List;

/**
 * This class stores list of updated policies and list of changed devices for Policy Manager
 */
public class UpdatedPolicyDeviceListBean {

    private List<Policy> updatedPolicies;
    private List<Integer> updatedPolicyIds;
    private List<String> changedDeviceTypes;

    public UpdatedPolicyDeviceListBean(List<Policy> updatedPolicies, List<Integer> updatedPolicyIds, List<String>
            deviceTypes) {
        this.updatedPolicies = updatedPolicies;
        this.updatedPolicyIds = updatedPolicyIds;
        this.changedDeviceTypes = deviceTypes;
    }

    public List<Policy> getUpdatedPolicies() {
        return updatedPolicies;
    }

    public void setUpdatedPolicies(List<Policy> updatedPolicies) {
        this.updatedPolicies = updatedPolicies;
    }

    public List<Integer> getUpdatedPolicyIds() {
        return updatedPolicyIds;
    }

    public void setUpdatedPolicyIds(List<Integer> updatedPolicyIds) {
        this.updatedPolicyIds = updatedPolicyIds;
    }

    public List<String> getChangedDeviceTypes() {
        return changedDeviceTypes;
    }

    public void setChangedDeviceTypes(List<String> changedDeviceTypes) {
        this.changedDeviceTypes = changedDeviceTypes;
    }
}
