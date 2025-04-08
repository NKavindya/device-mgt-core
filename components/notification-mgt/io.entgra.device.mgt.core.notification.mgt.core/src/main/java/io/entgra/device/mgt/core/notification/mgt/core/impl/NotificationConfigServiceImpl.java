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

package io.entgra.device.mgt.core.notification.mgt.core.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.entgra.device.mgt.core.device.mgt.common.metadata.mgt.Metadata;
import io.entgra.device.mgt.core.device.mgt.common.metadata.mgt.MetadataManagementService;
import io.entgra.device.mgt.core.notification.mgt.core.util.MetadataConstants;
import io.entgra.device.mgt.core.notification.mgt.common.beans.NotificationConfig;
import io.entgra.device.mgt.core.notification.mgt.common.beans.NotificationConfigurationList;
import io.entgra.device.mgt.core.notification.mgt.common.exception.NotificationConfigurationServiceException;
import io.entgra.device.mgt.core.notification.mgt.common.service.NotificationConfigService;
import io.entgra.device.mgt.core.notification.mgt.core.internal.NotificationManagementDataHolder;
import io.entgra.device.mgt.core.device.mgt.common.exceptions.MetadataManagementException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.lang.reflect.Type;
import java.util.NoSuchElementException;

public class NotificationConfigServiceImpl implements NotificationConfigService {
    private static final Log log = LogFactory.getLog(NotificationConfigServiceImpl.class);
    private static final Gson gson = new Gson();
    private final MetadataManagementService metaDataService =
            NotificationManagementDataHolder.getInstance().getMetaDataManagementService();
    
    /**
     * Adds new notification configuration contexts to the metadata storage.
     *
     * If a configuration with the same ID already exists, it will be skipped to avoid duplication.
     * If metadata already exists for notification configurations, it will be updated.
     * otherwise, new metadata will be created.
     * @param newConfigurations A list of new notification configurations to be added.
     * @throws NotificationConfigurationServiceException If the input is invalid or if an error occurs while
     *                                                   accessing or updating metadata.
     */
    public void addNotificationConfigContext(NotificationConfigurationList newConfigurations)
            throws NotificationConfigurationServiceException {
        if (newConfigurations == null || newConfigurations.isEmpty()) {
            throw new NotificationConfigurationServiceException("Cannot add empty configurations");
        }
        try {
            Metadata existingMetadata = metaDataService.retrieveMetadata(MetadataConstants.NOTIFICATION_CONFIG_META_KEY);
            NotificationConfigurationList configurations = new NotificationConfigurationList();
            if (existingMetadata != null) {
                String metaValue = existingMetadata.getMetaValue();
                if (metaValue != null && !metaValue.isEmpty()) {
                    Type listType = new TypeToken<NotificationConfigurationList>() {
                    }.getType();
                    NotificationConfigurationList existingConfigs = gson.fromJson(metaValue, listType);
                    if (existingConfigs != null && existingConfigs.getNotificationConfigurations() != null) {
                        configurations.setNotificationConfigurations(existingConfigs.getNotificationConfigurations());
                    }
                }
            }
            for (NotificationConfig newConfig : newConfigurations.getNotificationConfigurations()) {
                boolean isDuplicate = false;
                for (NotificationConfig existingConfig : configurations.getNotificationConfigurations()) {
                    if (existingConfig.getConfigId() == newConfig.getConfigId()) {
                        isDuplicate = true;
                        break;
                    }
                }
                if (isDuplicate) {
                    log.warn("Configuration with ID " + newConfig.getConfigId() + " already exists, skipping");
                } else {
                    configurations.add(newConfig);
                }
            }
            Metadata configMetadata = new Metadata();
            configMetadata.setMetaKey(MetadataConstants.NOTIFICATION_CONFIG_META_KEY);
            configMetadata.setMetaValue(gson.toJson(configurations));
            if (existingMetadata != null) {
                metaDataService.updateMetadata(configMetadata);
            } else {
                metaDataService.createMetadata(configMetadata);
            }
        } catch (MetadataManagementException e) {
            String msg = "Error creating or updating metadata: " + e.getMessage();
            log.error(msg, e);
            throw new NotificationConfigurationServiceException(msg, e);
        }
    }

    /**
     * Deletes a specific notification configuration from the Metadata context for a given tenant.
     *
     * @param configID The unique identifier (operationCode) of the notification configuration to be deleted.
     * @throws NotificationConfigurationServiceException If no configuration is found with the specified operationCode, or
     * if any error occurs during the database transaction or processing
     * This method retrieves the existing notification configuration context for the given tenant, removes the
     * configuration matching the provided operationCode, and updates the Metadata context with the remaining configurations.
     */
    public void deleteNotificationConfigContext(int configID) throws NotificationConfigurationServiceException {
        try {
            Metadata existingMetadata = metaDataService.retrieveMetadata(MetadataConstants.NOTIFICATION_CONFIG_META_KEY);
            if (existingMetadata == null) {
                String message = "No notification configuration with Meta key found for tenant: ";
                throw new NoSuchElementException(message);
            }
            String metaValue = existingMetadata.getMetaValue();
            Type listType = new TypeToken<NotificationConfigurationList>() {
            }.getType();
            NotificationConfigurationList configurations = gson.fromJson(metaValue, listType);
            boolean isRemoved = configurations.getNotificationConfigurations()
                    .removeIf(config -> config.getConfigId() == configID);
            if (!isRemoved) {
                String message = "No configuration found with config ID: " + configID;
                log.error(message);
                throw new NotificationConfigurationServiceException(message);
            }
            existingMetadata.setMetaValue(gson.toJson(configurations));
            metaDataService.updateMetadata(existingMetadata);
        } catch (NoSuchElementException e) {
            String msg = "No notification configuration context found for tenant: ";
            log.error(msg, e);
            throw new NotificationConfigurationServiceException(msg, e);
        } catch (IllegalArgumentException e) {
            String msg = "Invalid notification configuration context: " + configID;
            log.error(msg, e);
            throw new NotificationConfigurationServiceException(msg, e);
        } catch (MetadataManagementException e) {
            String msg = "Unexpected error occurred while deleting Notification Configurations: " + configID;
            log.error(msg, e);
            throw new NotificationConfigurationServiceException(msg, e);
        }
    }

    /**
     * Updates an existing notification configuration or adds a new configuration to the Metadata context for a given tenant.
     *
     * @param updatedConfig The notification configuration to be updated or added.
     * If a configuration with the same operationCode exists, it will be updated; otherwise, it will be added as a new entry.
     * @throws NotificationConfigurationServiceException If any error occurs during the database transaction or processing.
     * This method retrieves the existing notification configuration context for the given tenant. If a configuration with the same
     * operationCode as the provided configuration exists, it updates that configuration with the new details. Otherwise, it appends
     * the provided configuration as a new entry. The updated configurations are then serialized and saved back to the Metadata context.
     **/
    public void updateNotificationConfigContext(NotificationConfig updatedConfig)
            throws NotificationConfigurationServiceException {
        if (updatedConfig == null) {
            throw new NotificationConfigurationServiceException("Cannot update null configuration");
        }
        if (updatedConfig.getConfigId() <= 0) {
            throw new NotificationConfigurationServiceException("Configuration ID must be positive");
        }
        try {
            Metadata existingMetadata = metaDataService.retrieveMetadata(MetadataConstants.NOTIFICATION_CONFIG_META_KEY);
            if (existingMetadata == null) {
                throw new NotificationConfigurationServiceException(
                        "No notification configurations found to update");
            }
            String metaValue = existingMetadata.getMetaValue();
            if (metaValue == null || metaValue.isEmpty()) {
                throw new NotificationConfigurationServiceException(
                        "Empty metadata value for notification configurations");
            }
            Type listType = new TypeToken<NotificationConfigurationList>() {
            }.getType();
            NotificationConfigurationList configurations = gson.fromJson(metaValue, listType);
            if (configurations == null || configurations.getNotificationConfigurations() == null) {
                throw new NotificationConfigurationServiceException(
                        "Failed to deserialize existing configurations");
            }
            boolean found = false;
            for (int i = 0; i < configurations.size(); i++) {
                if (configurations.get(i).getConfigId() == updatedConfig.getConfigId()) {
                    configurations.set(i, updatedConfig);
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new NotificationConfigurationServiceException(
                        "Configuration with ID " + updatedConfig.getConfigId() + " not found");
            }
            existingMetadata.setMetaValue(gson.toJson(configurations));
            metaDataService.updateMetadata(existingMetadata);
        } catch (MetadataManagementException e) {
            String msg = "Error updating metadata: " + e.getMessage();
            log.error(msg, e);
            throw new NotificationConfigurationServiceException(msg, e);
        }
    }

    /**
     * Deletes all notification configuration metadata for the current tenant.
     *
     * @throws NotificationConfigurationServiceException If metadata is not found or if an error occurs during deletion.
     */
    public void deleteNotificationConfigurations() throws NotificationConfigurationServiceException {
        try {
            metaDataService.deleteMetadata(MetadataConstants.NOTIFICATION_CONFIG_META_KEY);
        } catch (NoSuchElementException e) {
            String msg = "No Meta Data found for Tenant ID";
            log.error(msg);
            throw new NotificationConfigurationServiceException(msg, e);
        } catch (MetadataManagementException e) {
            String message = "Unexpected error occurred while deleting notification configurations for tenant ID.";
            log.error(message, e);
            throw new NotificationConfigurationServiceException(message, e);
        }
    }

    /**
     * Retrieves the list of notification configurations stored in metadata for the current tenant.
     *
     * @return A {@link NotificationConfigurationList} containing all configured notifications.
     * @throws NotificationConfigurationServiceException If metadata retrieval fails, the metadata is missing,
     *                                                   or deserialization fails.
     */
    public NotificationConfigurationList getNotificationConfigurations() throws NotificationConfigurationServiceException {
        NotificationConfigurationList configurations = new NotificationConfigurationList();
        log.info("created default configurations list" + gson.toJson(configurations));
        try {
            if (metaDataService == null) {
                log.error("MetaDataManagementService is null");
                throw new NotificationConfigurationServiceException("MetaDataManagementService is not available");
            }
            Metadata existingMetadata = metaDataService.retrieveMetadata(MetadataConstants.NOTIFICATION_CONFIG_META_KEY);
            if (existingMetadata == null) {
                if (log.isDebugEnabled()) {
                    log.debug("No notification configurations found for tenant");
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("existing meta data" + existingMetadata);
            }
            String metaValue = existingMetadata.getMetaValue();
            log.info("Meta value: " + metaValue);
            Type listType = new TypeToken<NotificationConfigurationList>() {
            }.getType();
            NotificationConfigurationList configList = gson.fromJson(metaValue, listType);
            if (configList == null) {
                if (log.isDebugEnabled()) {
                    log.debug("Meta value could not be deserialized.");
                }
            }
            configurations.setNotificationConfigurations(configList.getNotificationConfigurations());
        } catch (NullPointerException e) {
            String message = "Meta value doesn't exist for meta key.";
            log.error(message, e);
            throw new NotificationConfigurationServiceException(message, e);
        } catch (MetadataManagementException e) {
            if (e.getMessage().contains("not found")) {
                String message = "Notification configurations not found for tenant ID";
                log.warn(message);
                throw new NotificationConfigurationServiceException(message, e);
            } else {
                String message = "Unexpected error occurred while retrieving notification configurations for tenant ID.";
                log.error(message, e);
                throw new NotificationConfigurationServiceException(message, e);
            }
        }
        return configurations;
    }

    /**
     * Retrieves a specific notification configuration by its configuration ID.
     *
     * @param configID The unique identifier of the notification configuration.
     * @return A {@link NotificationConfig} object corresponding to the given ID.
     * @throws NotificationConfigurationServiceException If the configuration is not found or an error occurs during retrieval.
     */
    public NotificationConfig getNotificationConfigByID(int configID) throws NotificationConfigurationServiceException {
        try {
            Metadata metaData = metaDataService.retrieveMetadata(MetadataConstants.NOTIFICATION_CONFIG_META_KEY);
            if (metaData == null) {
                String message = "No notification configurations found for tenant";
                log.error(message);
                throw new NotificationConfigurationServiceException(message);
            }
            String metaValue = metaData.getMetaValue();
            Type listType = new TypeToken<NotificationConfigurationList>() {
            }.getType();
            NotificationConfigurationList configurations = gson.fromJson(metaValue, listType);
            if (configurations != null) {
                for (NotificationConfig config : configurations.getNotificationConfigurations()) {
                    if (config.getConfigId() == configID) {
                        return config;
                    }
                }
            }
            String msg = "Configuration with config ID '" + configID + "' not found for tenant.";
            log.error(msg);
            throw new NotificationConfigurationServiceException(msg);
        } catch (MetadataManagementException e) {
            String message = "Error retrieving notification configuration by configID.";
            log.error(message, e);
            throw new NotificationConfigurationServiceException(message, e);
        }
    }
}
