/*
 *  Copyright (c) 2018 - 2025, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
 *
 * Entgra (Pvt) Ltd. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package io.entgra.device.mgt.core.notification.mgt.core.impl;

import io.entgra.device.mgt.core.notification.mgt.common.beans.NotificationConfigBatchNotifications;
import io.entgra.device.mgt.core.notification.mgt.common.beans.NotificationConfigCriticalCriteria;
import io.entgra.device.mgt.core.notification.mgt.common.beans.NotificationConfigurationSettings;
import io.entgra.device.mgt.core.notification.mgt.common.exception.TransactionManagementException;
import io.entgra.device.mgt.core.notification.mgt.core.util.NotificationEventBroker;
import io.entgra.device.mgt.core.notification.mgt.core.util.NotificationHelper;
import io.entgra.device.mgt.core.notification.mgt.common.beans.NotificationConfig;
import io.entgra.device.mgt.core.notification.mgt.common.dto.UserNotificationAction;
import io.entgra.device.mgt.core.notification.mgt.common.dto.UserNotificationPayload;
import io.entgra.device.mgt.core.notification.mgt.common.exception.NotificationManagementException;
import io.entgra.device.mgt.core.notification.mgt.common.dto.Notification;
import io.entgra.device.mgt.core.notification.mgt.common.service.NotificationManagementService;
import io.entgra.device.mgt.core.notification.mgt.core.dao.NotificationManagementDAO;
import io.entgra.device.mgt.core.notification.mgt.core.dao.factory.NotificationManagementDAOFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.user.api.UserStoreException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NotificationManagementServiceImpl implements NotificationManagementService {
    private static final Log log = LogFactory.getLog(NotificationManagementServiceImpl.class);
    private final NotificationManagementDAO notificationDAO;

    public NotificationManagementServiceImpl() {
        notificationDAO = NotificationManagementDAOFactory.getNotificationManagementDAO();
    }

    @Override
    public List<Notification> getLatestNotifications(int offset, int limit) throws NotificationManagementException {
        try {
            NotificationManagementDAOFactory.openConnection();
            return notificationDAO.getLatestNotifications(offset, limit);
        } catch (SQLException e) {
            String msg = "Error occurred while initiating transaction";
            log.error(msg, e);
            throw new NotificationManagementException(msg, e);
        } finally {
            NotificationManagementDAOFactory.closeConnection();
        }
    }

    @Override
    public List<UserNotificationPayload> getUserNotificationsWithStatus(
            String username, int limit, int offset, String status) throws NotificationManagementException {
        List<UserNotificationPayload> result = new ArrayList<>();
        try {
            NotificationManagementDAOFactory.openConnection();
            List<UserNotificationAction> userActions =
                    notificationDAO.getNotificationActionsByUser(username, limit, offset, status);
            if (userActions.isEmpty()) {
                return result;
            }
            // extract notification IDs
            List<Integer> notificationIds = userActions.stream()
                    .map(UserNotificationAction::getNotificationId)
                    .collect(Collectors.toList());
            List<Notification> notifications =
                    notificationDAO.getNotificationsByIds(notificationIds);
            // map actions to notifications
            Map<Integer, String> actionTypeMap = userActions.stream()
                    .collect(Collectors.toMap(UserNotificationAction::getNotificationId,
                            UserNotificationAction::getActionType,
                            (existing, replacement) -> existing
                    ));
            for (Notification notification : notifications) {
                String actionType = actionTypeMap.get(notification.getNotificationId());
                result.add(new UserNotificationPayload(
                        notification.getNotificationId(),
                        notification.getDescription(),
                        notification.getType(),
                        actionType,
                        username,
                        notification.getCreatedTimestamp()
                ));
            }
        } catch (SQLException e) {
            String msg = "Error occurred while retrieving user notifications with status";
            log.error(msg, e);
            throw new NotificationManagementException(msg, e);
        } finally {
            NotificationManagementDAOFactory.closeConnection();
        }
        return result;
    }

    @Override
    public void markNotificationAsReadForUser(int notificationId, String username)
            throws NotificationManagementException {
        try {
            NotificationManagementDAOFactory.beginTransaction();
            notificationDAO.markNotificationAsRead(notificationId, username);
            NotificationManagementDAOFactory.commitTransaction();
            int unreadCount = notificationDAO.getUnreadNotificationCountForUser(username);
            String payload = String.format("{\"unreadCount\":%d}", unreadCount);
            NotificationEventBroker.pushMessage(payload, Collections.singletonList(username));
        } catch (TransactionManagementException e) {
            String msg = "Error occurred while marking notification as read for user: " + username;
            log.error(msg, e);
            throw new NotificationManagementException(msg, e);
        } finally {
            NotificationManagementDAOFactory.closeConnection();
        }
    }

    @Override
    public int getUserNotificationCount(String username, String status) throws NotificationManagementException {
        try {
            NotificationManagementDAOFactory.openConnection();
            return notificationDAO.getNotificationActionsCountByUser(username, status);
        } catch (SQLException e) {
            String msg = "Error occurred while counting user notifications for user: " + username;
            log.error(msg, e);
            throw new NotificationManagementException(msg, e);
        } finally {
            NotificationManagementDAOFactory.closeConnection();
        }
    }

    @Override
    public void deleteUserNotifications(List<Integer> notificationIds, String username)
            throws NotificationManagementException {
        try {
            NotificationManagementDAOFactory.beginTransaction();
            notificationDAO.deleteUserNotifications(notificationIds, username);
            NotificationManagementDAOFactory.commitTransaction();
            int unreadCount = notificationDAO.getUnreadNotificationCountForUser(username);
            String payload = String.format("{\"unreadCount\":%d}", unreadCount);
            NotificationEventBroker.pushMessage(payload, Collections.singletonList(username));
        } catch (TransactionManagementException e) {
            String msg = "Error occurred while deleting notifications for user: " + username;
            log.error(msg, e);
            throw new NotificationManagementException(msg, e);
        } finally {
            NotificationManagementDAOFactory.closeConnection();
        }
    }

    @Override
    public void archiveUserNotifications(List<Integer> notificationIds, String username)
            throws NotificationManagementException {
        try {
            NotificationManagementDAOFactory.beginTransaction();
            notificationDAO.archiveUserNotifications(notificationIds, username);
            NotificationManagementDAOFactory.commitTransaction();
        } catch (TransactionManagementException e) {
            String msg = "Error occurred while archiving notifications for user: " + username;
            log.error(msg, e);
            throw new NotificationManagementException(msg, e);
        } finally {
            NotificationManagementDAOFactory.closeConnection();
        }
    }

    @Override
    public void deleteAllUserNotifications(String username) throws NotificationManagementException {
        try {
            NotificationManagementDAOFactory.beginTransaction();
            notificationDAO.deleteAllUserNotifications(username);
            NotificationManagementDAOFactory.commitTransaction();

            int unreadCount = notificationDAO.getUnreadNotificationCountForUser(username);
            String payload = String.format("{\"unreadCount\":%d}", unreadCount);
            NotificationEventBroker.pushMessage(payload, Collections.singletonList(username));
        } catch (TransactionManagementException e) {
            String msg = "Error occurred while deleting all notifications for user: " + username;
            log.error(msg, e);
            throw new NotificationManagementException(msg, e);
        } finally {
            NotificationManagementDAOFactory.closeConnection();
        }
    }

    @Override
    public void archiveAllUserNotifications(String username) throws NotificationManagementException {
        try {
            NotificationManagementDAOFactory.beginTransaction();
            notificationDAO.archiveAllUserNotifications(username);
            NotificationManagementDAOFactory.commitTransaction();
        } catch (TransactionManagementException e) {
            String msg = "Error occurred while archiving all notifications for user: " + username;
            log.error(msg, e);
            throw new NotificationManagementException(msg, e);
        } finally {
            NotificationManagementDAOFactory.closeConnection();
        }
    }

    @Override
    public void handleOperationNotificationIfApplicable(String operationCode, String operationStatus,
                                                        String deviceType, List<Integer> deviceEnrollmentIDs,
                                                        int tenantId, String notificationTriggerPoint)
            throws NotificationManagementException {
        try {
            NotificationConfig config = NotificationHelper.getNotificationConfigurationByCode(operationCode);
            if (config == null) return;
            NotificationConfigurationSettings settings = config.getNotificationSettings();
            if (settings == null) return;
            List<String> configDeviceTypes = settings.getDeviceTypes();
            List<String> triggerPoints = settings.getNotificationTriggerPoints();
            if (configDeviceTypes == null || triggerPoints == null ||
                    !configDeviceTypes.contains(deviceType) ||
                    !triggerPoints.contains(notificationTriggerPoint)) {
                return;
            }
            String statusToCheck = (operationStatus != null) ? operationStatus : "PENDING";
            NotificationConfigCriticalCriteria criticalCriteriaConfig = settings.getCriticalCriteriaOnly();
            if (criticalCriteriaConfig != null && criticalCriteriaConfig.isStatus()) {
                List<String> criticalCriteria = criticalCriteriaConfig.getCriticalCriteria();
                if (criticalCriteria == null || !criticalCriteria.contains(statusToCheck)) {
                    return;
                }
            }
            NotificationConfigBatchNotifications batchConfig = settings.getBatchNotifications();
            boolean isBatch = batchConfig != null && batchConfig.isEnabled();
            if (isBatch) {
                handleBatchOperationNotificationIfApplicable(config, deviceEnrollmentIDs,
                        operationStatus, deviceType, tenantId);
            } else {
                NotificationManagementDAOFactory.beginTransaction();
                try {
                    for (int deviceEnrollmentID : deviceEnrollmentIDs) {
                        String description = String.format("The operation %s (%s) for device with id %d of type %s is %s.",
                                config.getCode(), config.getDescription(), deviceEnrollmentID, deviceType, statusToCheck);
                        int notificationId = notificationDAO.insertNotification(
                                tenantId, config.getId(), config.getType(), description);
                        List<String> usernames = NotificationHelper.extractUsernamesFromRecipients(
                                config.getRecipients(), tenantId);
                        if (!usernames.isEmpty()) {
                            notificationDAO.insertNotificationUserActions(notificationId, usernames);
                            for (String username : usernames) {
                                int count = notificationDAO.getUnreadNotificationCountForUser(username);
                                String payload = String.format(
                                        "{\"message\":\"%s\",\"unreadCount\":%d}", description, count);
                                NotificationEventBroker.pushMessage(payload, Collections.singletonList(username));
                            }
                        }
                    }
                    NotificationManagementDAOFactory.commitTransaction();
                } catch (NotificationManagementException e) {
                    NotificationManagementDAOFactory.rollbackTransaction();
                    throw new NotificationManagementException("Error occurred while adding notification", e);
                } catch (UserStoreException e) {
                    throw new NotificationManagementException("Error occurred while adding user actions", e);
                }
            }
        } catch (NotificationManagementException e) {
            log.error("Failed to handle notification for operation " + operationCode, e);
        } catch (UserStoreException e) {
            throw new RuntimeException("Error retrieving users for role-based notification handling", e);
        } catch (TransactionManagementException e) {
            NotificationManagementDAOFactory.rollbackTransaction();
            throw new NotificationManagementException("Error occurred while adding notification", e);
        }
    }

    @Override
    public void handleBatchOperationNotificationIfApplicable(NotificationConfig config,
                                                             List<Integer> deviceIds,
                                                             String operationStatus,
                                                             String deviceType,
                                                             int tenantId)
            throws NotificationManagementException, TransactionManagementException, UserStoreException {
        String status = (operationStatus != null) ? operationStatus : "PENDING";
        NotificationConfigBatchNotifications batchConfig = config.getNotificationSettings().getBatchNotifications();
        boolean includeDeviceList = batchConfig.isIncludeDeviceListInBatch();
        String description;
        if (includeDeviceList) {
            description = String.format("The operation %s (%s) for device with ids %s of type %s is %s.",
                    config.getCode(), config.getDescription(), deviceIds.toString(), deviceType, status);
        } else {
            description = String.format("The operation %s (%s) for devices of type %s is %s.",
                    config.getCode(), config.getDescription(), deviceType, status);
        }
        NotificationManagementDAOFactory.beginTransaction();
        int notificationId = notificationDAO.insertNotification(
                tenantId, config.getId(), config.getType(), description);
        List<String> usernames = NotificationHelper.extractUsernamesFromRecipients(
                config.getRecipients(), tenantId);
        if (!usernames.isEmpty()) {
            notificationDAO.insertNotificationUserActions(notificationId, usernames);
            for (String username : usernames) {
                int count = notificationDAO.getUnreadNotificationCountForUser(username);
                String payload = String.format(
                        "{\"message\":\"%s\",\"unreadCount\":%d}", description, count);
                NotificationEventBroker.pushMessage(payload, Collections.singletonList(username));
            }
        }
        NotificationManagementDAOFactory.commitTransaction();
    }

    @Override
    public void handleTaskNotificationIfApplicable(String taskCode, int tenantId, String message)
            throws NotificationManagementException {
        try {
            NotificationConfig config = NotificationHelper.getNotificationConfigurationByCode(taskCode);
            if (config != null) {
                String description = String.format(message);
                NotificationManagementDAOFactory.beginTransaction();
                int notificationId = notificationDAO.insertNotification(
                        tenantId, config.getId(), config.getType(), description);
                List<String> usernames = NotificationHelper.extractUsernamesFromRecipients(
                        config.getRecipients(), tenantId);
                if (!usernames.isEmpty()) {
                    notificationDAO.insertNotificationUserActions(notificationId, usernames);
                    for (String username : usernames) {
                        int count = notificationDAO.getUnreadNotificationCountForUser(username);
                        String payload = String.format("{\"message\":\"%s\",\"unreadCount\":%d}", description, count);
                        NotificationEventBroker.pushMessage(payload, Collections.singletonList(username));
                    }
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
        }
    }
}
