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

package io.entgra.device.mgt.core.operation.template.spi;

import io.entgra.device.mgt.core.operation.template.dto.OperationTemplate;
import io.entgra.device.mgt.core.operation.template.exception.OperationTemplateMgtPluginException;

import java.util.List;
import java.util.Set;

/**
 * Operation Template service interface.
 */
public interface OperationTemplateService {

    void addOperationTemplate(OperationTemplate operationTemplate) throws OperationTemplateMgtPluginException;

    OperationTemplate updateOperationTemplate(OperationTemplate operationTemplate) throws OperationTemplateMgtPluginException;

    OperationTemplate getOperationTemplate(String deviceType, String subTypeId, String operationCode) throws OperationTemplateMgtPluginException;

    List<OperationTemplate> getAllOperationTemplates(String deviceType) throws OperationTemplateMgtPluginException;

    void deleteOperationTemplate(String deviceType, String subTypeId, String operationCode) throws OperationTemplateMgtPluginException;

    Set<String> getOperationTemplateCodes(String deviceType, String subTypeId) throws OperationTemplateMgtPluginException;
}
