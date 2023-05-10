/*
 * Copyright (c) 2023, Entgra Pvt Ltd. (http://www.wso2.org) All Rights Reserved.
 *
 * Entgra Pvt Ltd. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.entgra.device.mgt.subtype.mgt.dao;

import io.entgra.device.mgt.subtype.mgt.dto.DeviceSubType;
import io.entgra.device.mgt.subtype.mgt.exception.SubTypeMgtDAOException;

import java.util.List;

public interface DeviceSubTypeDAO {
    boolean addDeviceSubType(DeviceSubType deviceSubType) throws SubTypeMgtDAOException;

    boolean updateDeviceSubType(String subTypeId, int tenantId, DeviceSubType.DeviceType deviceType, String subTypeName,
                                String typeDefinition) throws SubTypeMgtDAOException;

    DeviceSubType getDeviceSubType(String subTypeId, int tenantId, DeviceSubType.DeviceType deviceType)
            throws SubTypeMgtDAOException;

    List<DeviceSubType> getAllDeviceSubTypes(int tenantId, DeviceSubType.DeviceType deviceType)
            throws SubTypeMgtDAOException;

    int getDeviceSubTypeCount(DeviceSubType.DeviceType deviceType) throws SubTypeMgtDAOException;

    boolean checkDeviceSubTypeExist(String subTypeId, int tenantId, DeviceSubType.DeviceType deviceType)
            throws SubTypeMgtDAOException;

    DeviceSubType getDeviceSubTypeByProvider(String subTypeName, int tenantId, DeviceSubType.DeviceType deviceType)
            throws SubTypeMgtDAOException;
}