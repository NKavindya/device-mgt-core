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
package io.entgra.device.mgt.core.device.mgt.extensions.pull.notification.internal;

import io.entgra.device.mgt.core.application.mgt.common.services.ApplicationManager;
import io.entgra.device.mgt.core.device.mgt.core.service.DeviceManagementProviderService;
import io.entgra.device.mgt.core.policy.mgt.core.PolicyManagerService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.*;

@Component(
        name = "io.entgra.device.mgt.core.device.mgt.extensions.pull.notification.internal.PullNotificationServiceComponent",
        immediate = true)
public class PullNotificationServiceComponent {

    private static final Log log = LogFactory.getLog(PullNotificationServiceComponent.class);

    @SuppressWarnings("unused")
    @Activate
    protected void activate(ComponentContext componentContext) {
        try {
            //Do nothing
            if (log.isDebugEnabled()) {
                log.debug("pull notification provider implementation bundle has been successfully " +
                        "initialized");
            }
        } catch (Throwable e) {
            log.error("Error occurred while initializing pull notification provider " +
                    "implementation bundle", e);
        }
    }
    @Deactivate
    protected void deactivate(ComponentContext componentContext) {
        //Do nothing
    }

    @Reference(
            name = "device.mgt.provider.service",
            service = io.entgra.device.mgt.core.device.mgt.core.service.DeviceManagementProviderService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetDeviceManagementProviderService")
    protected void setDeviceManagementProviderService(DeviceManagementProviderService deviceManagementProviderService) {
        PullNotificationDataHolder.getInstance().setDeviceManagementProviderService(deviceManagementProviderService);
    }

    protected void unsetDeviceManagementProviderService(DeviceManagementProviderService deviceManagementProviderService) {
        PullNotificationDataHolder.getInstance().setDeviceManagementProviderService(null);
    }

    @Reference(
            name = "policy.mgr.service",
            service = io.entgra.device.mgt.core.policy.mgt.core.PolicyManagerService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetPolicyManagerService")
    protected void setPolicyManagerService(PolicyManagerService policyManagerService) {
        PullNotificationDataHolder.getInstance().setPolicyManagerService(policyManagerService);
    }

    protected void unsetPolicyManagerService(PolicyManagerService policyManagerService) {
        PullNotificationDataHolder.getInstance().setPolicyManagerService(null);
    }

    @Reference(
            name = "applcation.mgr",
            service = io.entgra.device.mgt.core.application.mgt.common.services.ApplicationManager.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetApplicationManagerService")
    protected void setApplicationManagerService(ApplicationManager applicationManagerService){
        PullNotificationDataHolder.getInstance().setApplicationManager(applicationManagerService);
    }

    protected void unsetApplicationManagerService(ApplicationManager applicationManagerService){
        PullNotificationDataHolder.getInstance().setApplicationManager(null);
    }

}
