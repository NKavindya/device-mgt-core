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

package io.entgra.device.mgt.core.device.mgt.core.notification.mgt;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.entgra.device.mgt.core.device.mgt.common.Device;
import io.entgra.device.mgt.core.device.mgt.common.DeviceIdentifier;
import io.entgra.device.mgt.core.device.mgt.common.PaginationRequest;
import io.entgra.device.mgt.core.device.mgt.common.PaginationResult;
import io.entgra.device.mgt.core.device.mgt.common.exceptions.DeviceManagementException;
import io.entgra.device.mgt.core.device.mgt.common.exceptions.EntityDoesNotExistException;
import io.entgra.device.mgt.core.device.mgt.common.exceptions.MetadataManagementException;
import io.entgra.device.mgt.core.device.mgt.common.exceptions.TransactionManagementException;
import io.entgra.device.mgt.core.device.mgt.common.metadata.mgt.Metadata;
import io.entgra.device.mgt.core.device.mgt.common.metadata.mgt.MetadataManagementService;
import io.entgra.device.mgt.core.device.mgt.common.notification.mgt.Notification;
import io.entgra.device.mgt.core.device.mgt.common.notification.mgt.NotificationManagementException;
import io.entgra.device.mgt.core.device.mgt.common.notification.mgt.NotificationManagementService;
import io.entgra.device.mgt.core.device.mgt.common.operation.mgt.Operation;
import io.entgra.device.mgt.core.device.mgt.core.internal.DeviceManagementDataHolder;
import io.entgra.device.mgt.core.device.mgt.core.notification.mgt.beans.NotificationConfig;
import io.entgra.device.mgt.core.device.mgt.core.notification.mgt.beans.NotificationConfigurationList;
import io.entgra.device.mgt.core.device.mgt.core.notification.mgt.beans.NotificationConfigRecipients;
import io.entgra.device.mgt.core.device.mgt.core.notification.mgt.dao.NotificationDAO;
import io.entgra.device.mgt.core.device.mgt.core.notification.mgt.dao.NotificationManagementDAOFactory;
import io.entgra.device.mgt.core.device.mgt.core.notification.mgt.dao.util.NotificationDAOUtil;
import io.entgra.device.mgt.core.device.mgt.core.util.DeviceManagerUtil;
import io.entgra.device.mgt.core.device.mgt.extensions.logger.spi.EntgraLogger;
import io.entgra.device.mgt.core.notification.logger.DeviceLogContext;
import io.entgra.device.mgt.core.notification.logger.impl.EntgraDeviceLoggerImpl;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.api.UserStoreManager;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * This class implements the NotificationManagementService.
 */
public class NotificationManagementServiceImpl implements NotificationManagementService {

    private static final EntgraLogger log = new EntgraDeviceLoggerImpl(NotificationManagementServiceImpl.class);
    DeviceLogContext.Builder deviceLogContexBuilder = new DeviceLogContext.Builder();
    public static final String NOTIFICATION_CONFIG_META_KEY = "notification-config" ;
    private NotificationDAO notificationDAO;
    private static final Gson gson = new Gson();

    public NotificationManagementServiceImpl() {
        this.notificationDAO = NotificationManagementDAOFactory.getNotificationDAO();
    }

    @Override
    @Deprecated
    public boolean addNotification(DeviceIdentifier deviceId,
                                   Notification notification) throws NotificationManagementException {
        Device device;
        try {
            device = DeviceManagementDataHolder.getInstance().getDeviceManagementProvider()
                    .getDevice(deviceId, false);
        } catch (DeviceManagementException e) {
            throw new NotificationManagementException("Error occurred while retrieving device data for " +
                    " adding notification", e);
        }
        if (device == null) {
            throw new EntityDoesNotExistException("No device is found with type '" + deviceId.getType() +
                    "' and id '" + deviceId.getId() + "'");
        }
        return addNotification(device, notification);
    }

    @Override
    public boolean addNotification(Device device,
                                   Notification notification) throws NotificationManagementException {
        if (log.isDebugEnabled()) {
            log.debug("Adding a Notification : [" + notification.toString() + "]");
        }
        int notificationId;
        int tenantId = NotificationDAOUtil.getTenantId();

//        try {
//            NotificationManagementDAOFactory.beginTransaction();
//            notificationId = notificationDAO.addNotification(device.getId(), tenantId, notification);
//            NotificationManagementDAOFactory.commitTransaction();
//        } catch (TransactionManagementException e) {
//            NotificationManagementDAOFactory.rollbackTransaction();
//            throw new NotificationManagementException("Error occurred while adding notification", e);
//        } finally {
//            NotificationManagementDAOFactory.closeConnection();
//        }
//        if (log.isDebugEnabled()) {
//            log.debug("Notification id : " + notificationId + " was added to the table.");
//        }
        return true;
    }

    @Override
    public boolean updateNotification(Notification notification) throws NotificationManagementException {
        if (log.isDebugEnabled()) {
            log.debug("Updating Notification : [" + notification.toString() + "]");
        }
//        try {
//            NotificationManagementDAOFactory.beginTransaction();
//            notificationDAO.updateNotification(notification);
//            NotificationManagementDAOFactory.commitTransaction();
//        } catch (TransactionManagementException e) {
//            NotificationManagementDAOFactory.rollbackTransaction();
//            throw new NotificationManagementException("Error occurred while updating notification ", e);
//        } finally {
//            NotificationManagementDAOFactory.closeConnection();
//        }
        if (log.isDebugEnabled()) {
            log.debug("Notification id : " + notification.getNotificationId() +
                    " has updated successfully.");
        }
        return true;
    }

    @Override
    public boolean updateNotificationStatus(int notificationId, Notification.Status status)
            throws NotificationManagementException {
        if (log.isDebugEnabled()) {
            log.debug("Updating Notification id : " + notificationId);
        }
//        try {
//            NotificationManagementDAOFactory.beginTransaction();
//            notificationDAO.updateNotificationStatus(notificationId, status);
//            NotificationManagementDAOFactory.commitTransaction();
//        } catch (TransactionManagementException e) {
//            NotificationManagementDAOFactory.rollbackTransaction();
//            throw new NotificationManagementException("Error occurred while updating notification", e);
//        } finally {
//            NotificationManagementDAOFactory.closeConnection();
//        }
        if (log.isDebugEnabled()) {
            log.debug("Notification id : " + notificationId + " has updated successfully.");
        }
        return true;
    }

    @Override
    public boolean updateAllNotifications(Notification.Status status, int tenantID) throws
            NotificationManagementException {
        if (log.isDebugEnabled()) {
            log.debug("Attempting to clear all notifications");
        }
//        try {
//            NotificationManagementDAOFactory.beginTransaction();
//            notificationDAO.updateAllNotifications(status, tenantID);
//            NotificationManagementDAOFactory.commitTransaction();
//        } catch (TransactionManagementException e) {
//            NotificationManagementDAOFactory.rollbackTransaction();
//            throw new NotificationManagementException("Error occurred while updating notification", e);
//        } finally {
//            NotificationManagementDAOFactory.closeConnection();
//        }
        if (log.isDebugEnabled()) {
            log.debug("All notifications updated successfully.");
        }
        return true;
    }

    @Override
    public List<Notification> getAllNotifications() throws NotificationManagementException {
        try {
            NotificationManagementDAOFactory.openConnection();
            return notificationDAO.getAllNotifications(NotificationDAOUtil.getTenantId());
        } catch (SQLException e) {
            throw new NotificationManagementException("Error occurred while opening a connection to" +
                    " the data source", e);
        } finally {
            NotificationManagementDAOFactory.closeConnection();
        }
    }

    @Override
    public Notification getNotification(int notificationId) throws NotificationManagementException {
        try {
            NotificationManagementDAOFactory.openConnection();
            return notificationDAO.getNotification(NotificationDAOUtil.getTenantId(), notificationId);
        } catch (SQLException e) {
            throw new NotificationManagementException("Error occurred while opening a connection to" +
                    " the data source", e);
        } finally {
            NotificationManagementDAOFactory.closeConnection();
        }
    }

    @Override
    public PaginationResult getAllNotifications(PaginationRequest request) throws NotificationManagementException {
        PaginationResult paginationResult = new PaginationResult();
        List<Notification> notifications = new ArrayList<>();
        request = DeviceManagerUtil.validateNotificationListPageSize(request);
        int count =0;
        try {
            NotificationManagementDAOFactory.openConnection();
            notifications = notificationDAO.getAllNotifications(request, NotificationDAOUtil.getTenantId());
            count = notificationDAO.getNotificationCount(NotificationDAOUtil.getTenantId());
            paginationResult.setData(notifications);
            paginationResult.setRecordsFiltered(count);
            paginationResult.setRecordsTotal(count);
            return paginationResult;
        } catch (SQLException e) {
            throw new NotificationManagementException("Error occurred while opening a connection to" +
                    " the data source", e);
        } finally {
            NotificationManagementDAOFactory.closeConnection();
        }
    }

    @Override
    public PaginationResult getNotificationsByStatus(Notification.Status status,
                                                     PaginationRequest request) throws NotificationManagementException{
        PaginationResult paginationResult = new PaginationResult();
        List<Notification> notifications = new ArrayList<>();
        request = DeviceManagerUtil.validateNotificationListPageSize(request);
        int count =0;
        try {
            NotificationManagementDAOFactory.openConnection();
            notifications = notificationDAO.getNotificationsByStatus(request, status, NotificationDAOUtil.getTenantId());
            count = notificationDAO.getNotificationCountByStatus(status, NotificationDAOUtil.getTenantId());
            paginationResult.setData(notifications);
            paginationResult.setRecordsFiltered(count);
            paginationResult.setRecordsTotal(count);
            return paginationResult;
        } catch (SQLException e) {
            throw new NotificationManagementException("Error occurred while opening a connection " +
                    "to the data source", e);
        } finally {
            NotificationManagementDAOFactory.closeConnection();
        }
    }

    @Override
    public List<Notification> getNotificationsByStatus(Notification.Status status)
            throws NotificationManagementException {
        try {
            NotificationManagementDAOFactory.openConnection();
            return notificationDAO.getNotificationsByStatus(status, NotificationDAOUtil.getTenantId());
        } catch (SQLException e) {
            throw new NotificationManagementException("Error occurred while opening a connection " +
                    "to the data source", e);
        } finally {
            NotificationManagementDAOFactory.closeConnection();
        }
    }

    @Override
    public void handleOperationNotificationIfApplicable(Operation operation, Map<Integer, Device> enrolments, int tenantId)
            throws NotificationManagementException {
        try {
            NotificationConfig config = getNotificationConfigurationByCode(operation.getCode());
            if (config != null) {
                String status = (operation.getStatus() != null) ?
                        operation.getStatus().toString() :
                        Operation.Status.PENDING.toString();
                String description = String.format("The operation %s (%s) for device %s of type %s is %s.",
                        operation.getCode(), config.getConfigId(),
                        enrolments.values().iterator().next().getId(),
                        enrolments.values().iterator().next().getType(),
                        status);
                NotificationManagementDAOFactory.beginTransaction();
                int notificationId = notificationDAO.insertNotification(
                        tenantId, config.getConfigId(), config.getType(), description);
                List<String> usernames = extractUsernamesFromRecipients(config.getRecipients(), tenantId);
                if (!usernames.isEmpty()) {
                    notificationDAO.insertNotificationUserActions(notificationId, usernames);
                }
                NotificationManagementDAOFactory.commitTransaction();
            }
        } catch (NotificationManagementException e) {
            log.error("Failed to handle notification for operation " + operation.getCode(), e);
        } catch (UserStoreException e) {
            throw new RuntimeException("Error retrieving users for role-based notification handling", e);
        } catch (TransactionManagementException e) {
            NotificationManagementDAOFactory.rollbackTransaction();
            throw new NotificationManagementException("Error occurred while adding notification", e);
        } finally {
            NotificationManagementDAOFactory.closeConnection();
        }
    }

    @Override
    public void handleTaskNotificationIfApplicable(String taskCode, int tenantId, String message)
            throws NotificationManagementException {
        try {
            NotificationConfig config = getNotificationConfigurationByCode(taskCode);
            if (config != null) {
                String description = String.format(message);
                NotificationManagementDAOFactory.beginTransaction();
                int notificationId = notificationDAO.insertNotification(
                        tenantId, config.getConfigId(), config.getType(), description);
                List<String> usernames = extractUsernamesFromRecipients(config.getRecipients(), tenantId);
                if (!usernames.isEmpty()) {
                    notificationDAO.insertNotificationUserActions(notificationId, usernames);
                }
                NotificationManagementDAOFactory.commitTransaction();
            }
        } catch (NotificationManagementException e) {
            log.error("Failed to handle task notification for task " + taskCode, e);
            throw e;
        } catch (UserStoreException e) {
            throw new RuntimeException("Error retrieving users for role-based task notification handling", e);
        } catch (TransactionManagementException e) {
            NotificationManagementDAOFactory.rollbackTransaction();
            throw new NotificationManagementException("Error occurred while adding notification", e);
        } finally {
            NotificationManagementDAOFactory.closeConnection();
        }
    }

    private List<String> extractUsernamesFromRecipients(NotificationConfigRecipients recipients, int tenantId)
            throws UserStoreException {
        List<String> usernames = new ArrayList<>();
        if (recipients == null) {
            return usernames;
        }
        UserStoreManager userStoreManager = DeviceManagementDataHolder.getInstance()
                .getRealmService().getTenantUserRealm(tenantId).getUserStoreManager();
        List<String> users = recipients.getUsers();
        if (users != null) {
            usernames.addAll(users);
        }
        List<String> roles = recipients.getRoles();
        if (roles != null) {
            for (String role : roles) {
                String[] usersWithRole = userStoreManager.getUserListOfRole(role);
                usernames.addAll(Arrays.asList(usersWithRole));
            }
        }
        return usernames;
    }

    public NotificationConfig getNotificationConfigurationByCode(String code) throws NotificationManagementException {
        log.info("Fetching notification configuration for code: " + code);
        MetadataManagementService metaDataService = DeviceManagementDataHolder
                .getInstance().getMetadataManagementService();
        try {
            if (metaDataService == null) {
                log.error("MetaDataManagementService is null");
                throw new NotificationManagementException("MetaDataManagementService is not available");
            }
            Metadata existingMetadata = metaDataService.retrieveMetadata(NOTIFICATION_CONFIG_META_KEY);
            if (existingMetadata == null) {
                if (log.isDebugEnabled()) {
                    log.debug("No notification configurations found for tenant");
                }
                return null;
            }
            if (log.isDebugEnabled()) {
                log.debug("Existing metadata: " + existingMetadata);
            }
            String metaValue = existingMetadata.getMetaValue();
            log.info("Meta value: " + metaValue);
            Type listType = new TypeToken<NotificationConfigurationList>() {}.getType();
            NotificationConfigurationList configList = gson.fromJson(metaValue, listType);
            if (configList == null || configList.getNotificationConfigurations() == null) {
                if (log.isDebugEnabled()) {
                    log.debug("Meta value could not be deserialized or config list is empty.");
                }
                return null;
            }
            for (NotificationConfig config : configList.getNotificationConfigurations()) {
                if (config.getCode().equalsIgnoreCase(code)) {
                    return config;
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("No configuration found matching code: " + code);
            }
            return null;
        } catch (NullPointerException e) {
            String message = "Meta value doesn't exist for meta key.";
            log.error(message, e);
            throw new NotificationManagementException(message, e);
        } catch (MetadataManagementException e) {
            if (e.getMessage().contains("not found")) {
                String message = "Notification configurations not found for tenant ID";
                log.warn(message);
                throw new NotificationManagementException(message, e);
            } else {
                String message = "Unexpected error occurred while retrieving notification configurations for tenant ID.";
                log.error(message, e);
                throw new NotificationManagementException(message, e);
            }
        }
    }
}
