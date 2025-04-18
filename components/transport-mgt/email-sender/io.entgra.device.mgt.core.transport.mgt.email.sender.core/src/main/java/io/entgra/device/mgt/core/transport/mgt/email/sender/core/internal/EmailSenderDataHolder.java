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

package io.entgra.device.mgt.core.transport.mgt.email.sender.core.internal;

import io.entgra.device.mgt.core.transport.mgt.email.sender.core.service.EmailSenderService;
import org.wso2.carbon.utils.ConfigurationContextService;

public class EmailSenderDataHolder {

//    private RegistryService registryService;
    private ConfigurationContextService configurationContextService;
    private EmailSenderService emailServiceProvider;

    private static EmailSenderDataHolder thisInstance = new EmailSenderDataHolder();

    private EmailSenderDataHolder() {}

    public static EmailSenderDataHolder getInstance() {
        return thisInstance;
    }

//    public RegistryService getRegistryService() {
//        if (registryService == null) {
//            throw new IllegalStateException("Registry service is not initialized properly");
//        }
//        return registryService;
//    }
//
//    public void setRegistryService(RegistryService registryService) {
//        this.registryService = registryService;
//    }

    public ConfigurationContextService getConfigurationContextService() {
        if (configurationContextService == null) {
            throw new IllegalStateException("ConfigurationContext service is not initialized properly");
        }
        return configurationContextService;
    }

    public void setConfigurationContextService(ConfigurationContextService configurationContextService) {
        this.configurationContextService = configurationContextService;
    }

    public EmailSenderService getEmailServiceProvider() {
        return emailServiceProvider;
    }

    public void setEmailServiceProvider(EmailSenderService emailServiceProvider) {
        this.emailServiceProvider = emailServiceProvider;
    }

}
