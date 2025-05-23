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


package io.entgra.device.mgt.core.device.mgt.core.device.details.mgt.dao;

import io.entgra.device.mgt.core.device.mgt.common.Device;
import io.entgra.device.mgt.core.device.mgt.common.device.details.DeviceInfo;
import io.entgra.device.mgt.core.device.mgt.common.device.details.DeviceLocation;

import java.util.List;
import java.util.Map;

/**
 * This class will store device details related generic information such as cpu/memory utilization, battery level,
 * plugged in to a power source or operation on battery.
 * In CDM framework, we only keep the snapshot of the device information. So previous details are deleted as soon as new
 * data is arrived.
 */

public interface DeviceDetailsDAO {

    /**
     * This method will add device information to the database.
     * @param deviceInfo - Device information object.
     * @throws DeviceDetailsMgtDAOException
     */
    void addDeviceInformation(int deviceId, int enrolmentId, DeviceInfo deviceInfo) throws DeviceDetailsMgtDAOException;

    /**
     * This method will add the device properties to the database.
     * @param propertyMap - device properties.
     * @throws DeviceDetailsMgtDAOException
     */
    void addDeviceProperties(Map<String, String> propertyMap, int deviceId, int enrolmentId)
            throws DeviceDetailsMgtDAOException;

    void updateDeviceProperties(Map<String, String> propertyMap, int deviceId, int enrolmentId)
            throws DeviceDetailsMgtDAOException;

    /**
     * This method will return the device information when device id is provided.
     * @param deviceId - device Id
     * @return DeviceInfo
     * @throws DeviceDetailsMgtDAOException
     */
    DeviceInfo getDeviceInformation(int deviceId, int enrolmentId) throws DeviceDetailsMgtDAOException;

    /**
     * This method will return the device properties from database.
     * @param deviceId
     * @return - device properties map.
     * @throws DeviceDetailsMgtDAOException
     */
    Map<String, String> getDeviceProperties(int deviceId, int enrolmentId) throws DeviceDetailsMgtDAOException;

    /**
     * This method will delete the device information from the database.
     * @param deviceId - Integer.
     * @throws DeviceDetailsMgtDAOException
     */
    void deleteDeviceInformation(int deviceId, int enrollmentId) throws DeviceDetailsMgtDAOException;

    /**
     * This method will delete the device properties from database.
     * @param deviceId - Integer.
     * @throws DeviceDetailsMgtDAOException
     */
    void deleteDeviceProperties(int deviceId, int enrollmentId) throws DeviceDetailsMgtDAOException;

    /**
     * This method will add device location to database.
     * @param deviceLocation  - Device location with latitude and longitude.
     * @throws DeviceDetailsMgtDAOException
     */
    void addDeviceLocation(DeviceLocation deviceLocation, int enrollmentId) throws DeviceDetailsMgtDAOException;

    /**
     * This method will return the device location object when the device id is provided.
     * @param deviceId - id of the device.
     * @return - Device location object.
     * @throws DeviceDetailsMgtDAOException
     */
    DeviceLocation getDeviceLocation(int deviceId, int enrollmentId) throws DeviceDetailsMgtDAOException;


    /**
     * This method will return the device location exist or not
     * @param deviceId - id of the device.
     * @param enrollmentId - enrolment id of the device.
     * @return - if device location exist
     * @throws DeviceDetailsMgtDAOException if SQL error occurred while processing the query.
     */
    boolean hasLocations(int deviceId, int enrollmentId) throws DeviceDetailsMgtDAOException;


    /**
     * This method will delete the device location from the database.
     * @param deviceId
     * @throws DeviceDetailsMgtDAOException
     */
    void deleteDeviceLocation(int deviceId, int enrollmentId) throws DeviceDetailsMgtDAOException;

    /**
     * Add device location information to the database
     * @param device Device object
     * @param deviceLocation Device Location Object
     * @param tenantId Tenant Id
     * @throws DeviceDetailsMgtDAOException
     */
    void addDeviceLocationInfo(Device device, DeviceLocation deviceLocation, int tenantId)
            throws DeviceDetailsMgtDAOException;

    /**
     * Add device location information to the database
     * @param device Device object
     * @param deviceLocation Device Location Object
     * @param tenantId Tenant Id
     * @throws DeviceDetailsMgtDAOException
     */
    void addDeviceLocationsInfo(Device device, List<DeviceLocation> deviceLocation, int tenantId)
            throws DeviceDetailsMgtDAOException;

    void updateDeviceInformation(int deviceId, int enrollmentId, DeviceInfo newDeviceInfo) throws DeviceDetailsMgtDAOException;

    void updateDeviceLocation(DeviceLocation deviceLocation, int enrollmentId) throws DeviceDetailsMgtDAOException;
}
