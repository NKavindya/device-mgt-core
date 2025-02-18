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

package io.entgra.device.mgt.core.policy.mgt.core.util;

public final class PolicyManagementConstants {

    public static final String DEVICE_CONFIG_XML_NAME = "cdm-config.xml";
    public static final String ANY = "ANY";
    public static final String POLICY_BUNDLE = "POLICY_BUNDLE";
    public static final String MONITOR_POLICY_BUNDLE = "MONITOR_POLICY_BUNDLE";

    public static final String TENANT_ID = "TENANT_ID";

    // public static final String MONITOR = "MONITOR";
    public static final String ENFORCE = "ENFORCE";
    public static final String WARN = "WARN";
    public static final String BLOCK = "BLOCK";

    public static final String ENROLLMENT_APP_INSTALL_FEATURE_CODE = "ENROLLMENT_APP_INSTALL";

    public static final String MONITORING_TASK_TYPE = "MONITORING_TASK";
    public static final String MONITORING_TASK_NAME = "MONITORING";
    public static final String MONITORING_TASK_CLAZZ = "io.entgra.device.mgt.core.policy.mgt.core.task.MonitoringTask";

    public static final String DM_CACHE_MANAGER = "DM_CACHE_MANAGER";
    // public static final String DM_CACHE = "DM_CACHE";
    public static final String DM_CACHE_LIST = "DM_CACHE_LIST";

    public static final String DELEGATION_TASK_TYPE = "DELEGATION__TASK";
    public static final String DELEGATION_TASK_NAME = "DELEGATION";
    public static final String DELEGATION_TASK_CLAZZ = "io.entgra.device.mgt.core.policy.mgt.core.enforcement.DelegationTask";

    public static final String GENERAL_POLICY_TYPE = "GENERAL";
    public static final String CORRECTIVE_POLICY_TYPE = "CORRECTIVE";
    public static final String POLICY_CORRECTIVE_ACTION_TYPE = "POLICY";
    public static final String POLICY_FEATURE_CODE = "POLICY_ACTION";
    public static final String POLICY_ACTIONS = "POLICY_ACTIONS";
    public static final String CORRECTIVE_POLICY_FEATURE_CODE = "CORRECTIVE_POLICY";
    public static final String EMAIL_CORRECTIVE_ACTION_TYPE = "MAIL";
    public static final String EMAIL_FEATURE_CODE = "EMAIL_ACTION";
    public static final Integer EMAIL_ACTION_ID = 450;

    /**
     Caller would reference the constants using PolicyManagementConstants.DEVICE_CONFIG_XML_NAME,
     and so on. Any caller should be prevented from constructing objects of
     this class, thus declaring this private constructor.
     */
    private PolicyManagementConstants() {
        throw new AssertionError();
    }

}
